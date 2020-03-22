package net.jp.vss.visitscheduler.domain.schedules.calculate

import com.google.common.annotations.VisibleForTesting
import java.time.LocalDate
import java.util.stream.IntStream
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School
import org.slf4j.LoggerFactory

/**
 * 訪問スケジュール算出
 *
 * @property targetYearAndMonth 対象年月
 * @property workerSchedule 勤務者のスケジュール
 * @property schoolSchedules 学校のスケジュールリスト
 * @property visitRules 訪問設定
 */
class VisitScheduleCalculator(
    private val targetYearAndMonth: Schedule.TargetYearAndMonth,
    private val workerSchedule: WorkerSchedule,
    private val schoolSchedules: List<SchoolSchedule>,
    private val visitRules: VisitRules
) {

    companion object {
        private val log = LoggerFactory.getLogger(VisitScheduleCalculator::class.java)
    }

    /** 訪問可能日リスト. */
    private var visitTargetDates: List<Schedule.ScheduleDate>

    @VisibleForTesting
    fun getVisitTargetDates() = visitTargetDates

    @VisibleForTesting
    fun setVisitTargetDates(value: List<Schedule.ScheduleDate>) {
        visitTargetDates = value
    }

    /** 訪問スケジュール Map. */
    private val visitScheduleMap: Map<School.SchoolCode, VisitSchedule>

    @VisibleForTesting
    fun getVisitScheduleMap() = this.visitScheduleMap

    /** 訪問日として設定した日付リスト. */
    private val visitDates: MutableList<LocalDate>

    @VisibleForTesting
    fun getVisitDates() = this.visitDates.toList()

    init {
        visitTargetDates = workerSchedule.buildVisitTargetDates(targetYearAndMonth)
        visitScheduleMap = schoolSchedules.map { it.schoolCode to VisitSchedule.of(it) }.toMap()
        visitDates = mutableListOf()
        val visitTargetDatesSize = visitTargetDates.size
        val visitTargetDateCount = schoolSchedules.size * 2 // 学校あたり2つの訪問が必要

        if (visitTargetDatesSize < visitTargetDateCount) {
            val message = "Invalid parameter. " +
                "visitTargetDatesSize($visitTargetDatesSize) >= visitTargetDateCount($visitTargetDateCount)"
            log.info("Message: {}", message)
            throw IllegalStateException("訪問可能日が足りないのでスケジュールを立てられません。")
        }
    }

    /**
     * スケジュール算出.
     *
     * <pre>
     * - 強制訪問日の設定
     * - 優先訪問日の設定
     * - 訪問除外日以外
     *
     * の順番でスケジュールを埋めていきます。
     * </pre>
     *
     * @return 算出後の学校のスケジュールリスト
     * @throws IllegalArgumentException 設定値が不正
     */
    fun calculate(): List<VisitSchedule> {
        setForceVisitDates()
        setPriorityVisitDates()

        setVisitDates()
        return sortVisitSchedule()
    }

    /**
     * 強制訪問日の設定.
     *
     * <pre>
     * 訪問可能日リストの中から、学校毎に強制訪問日と設定されている日付を訪問日として設定します。
     * 複数の学校で同一日に対して強制訪問日として設定した場合、IllegalArgumentException を throw します。
     *
     * 訪問日として設定した日は、訪問可能日リストから除外します。
     * </pre>
     *
     * @throws IllegalArgumentException 複数の学校で同一日に対して強制訪問日として設定した場合
     */
    @VisibleForTesting
    fun setForceVisitDates() {
        visitTargetDates.forEach { visitDate: Schedule.ScheduleDate ->
            schoolSchedules
                    .filter { schoolSchedule: SchoolSchedule -> schoolSchedule.isForceVisitDate(visitDate) }
                    .forEach { schoolSchedule: SchoolSchedule ->
                        if (visitDates.contains(visitDate.date)) {
                            // 複数の学校で同一日に対して強制訪問日として設定した場合
                            val message =
                                "VisitDate($visitDate) is already setting in School(${schoolSchedule.schoolCode.value})"
                            log.info("Message: {}", message)
                            throw IllegalArgumentException("複数の学校で ${visitDate.date} に" +
                                "対して強制訪問日として設定しています。見直してください。")
                        }
                        addVisitDate(schoolSchedule, visitDate)
                    }
        }
        // 訪問可能日リスト更新
        updateVisitTargetDates()
    }

    /** 優先訪問日の設定.  */
    @VisibleForTesting
    fun setPriorityVisitDates() {
        // 優先訪問日の設定
        val visitTargetDateSize = visitTargetDates.size
        IntStream.range(0, visitTargetDateSize)
            .forEach { index: Int ->
                val visitDate: Schedule.ScheduleDate = visitTargetDates[index]
                val targetSchoolSchedules: List<SchoolSchedule> = schoolSchedules
                    .filter { it.isPriorityVisitDate(visitDate) }
                    .filter { visitScheduleMap[it.schoolCode]?.isVisitTargetDate(visitDate, visitRules) ?: false }
                    .toList()
                if (targetSchoolSchedules.isEmpty()) {
                    return@forEach
                }
                val schoolSchedule: SchoolSchedule
                schoolSchedule = if (targetSchoolSchedules.size == 1) {
                    targetSchoolSchedules[0]
                } else {
                    choiceSchoolSchedule(targetSchoolSchedules, index)
                }
                addVisitDate(schoolSchedule, visitDate)
            }

        // 訪問可能日リスト更新
        updateVisitTargetDates()
    }

    @VisibleForTesting
    fun choiceSchoolSchedule(targetSchoolSchedules: List<SchoolSchedule>, visitTargetDateIndex: Int): SchoolSchedule {
        val priorityPoints = targetSchoolSchedules
            .map { calculatePoints(it, visitTargetDateIndex) }
            .toList()
        val minPoint = priorityPoints
            .stream()
            .min(Comparator.naturalOrder())
            .orElseThrow { IllegalStateException("Not Found min point.") }
        val minPointIndex = IntStream.range(0, targetSchoolSchedules.size)
            .filter { minPoint == priorityPoints[it] }
            .findFirst()
            .orElseThrow { IllegalStateException("Not Found min point index.") }
        return targetSchoolSchedules[minPointIndex]
    }

    /**
     * ポイント計算.
     *
     * <pre>
     * 訪問可能日リスト Index 以降の3つの日付に対して、優先訪問日として設定されている件数をポイントとして返します。
     * 訪問可能日リスト Index から日付が取れない場合、0件とします。
     * </pre>
     *
     * @param schoolSchedule 対象学校のスケジュール
     * @param visitTargetDateIndex 訪問可能日リストIndex
     * @return ポイント数
     */
    private fun calculatePoints(schoolSchedule: SchoolSchedule, visitTargetDateIndex: Int): Int {
        val point1 = calculatePoint(schoolSchedule, visitTargetDateIndex + 1)
        val point2 = calculatePoint(schoolSchedule, visitTargetDateIndex + 2)
        val point3 = calculatePoint(schoolSchedule, visitTargetDateIndex + 3)
        return point1 + point2 + point3
    }

    private fun calculatePoint(schoolSchedule: SchoolSchedule, visitTargetDateIndex: Int): Int {
        if (visitTargetDates.size <= visitTargetDateIndex) {
            // 指定した index の要素がないので、0
            return 0
        }
        val targetDate: Schedule.ScheduleDate = visitTargetDates[visitTargetDateIndex]
        val visitSchedule = visitScheduleMap[schoolSchedule.schoolCode]
        return if (schoolSchedule.isPriorityVisitDate(targetDate) &&
            visitSchedule!!.isVisitTargetDate(targetDate, visitRules)) {
            // 指定した index が優先訪問日の場合、ポイント1
            1
        } else 0
    }

    @VisibleForTesting
    fun setVisitDates() {
        // 空き日数が 2 の学校に対して処理を行う
        setVisitDates(2)
        // 空き日数が 1 の学校に対して処理を行う
        setVisitDates(1)
    }

    private fun setVisitDates(emptyVisitDateCount: Int) {
        schoolSchedules.forEach {
            val visitSchedule = visitScheduleMap.getValue(it.schoolCode)
            if (visitSchedule.getEmptyVisitDateCount() != emptyVisitDateCount) {
                return@forEach
            }
            val findScheduleDate = visitTargetDates.firstOrNull {
                targetDate -> visitSchedule.isVisitTargetDate(targetDate, visitRules) }
            if (findScheduleDate != null) {
                addVisitDate(it, findScheduleDate)
            }
            updateVisitTargetDates()
        }
    }

    private fun sortVisitSchedule(): List<VisitSchedule> {
        return schoolSchedules.map { visitScheduleMap[it.schoolCode] ?: error("invalid") }.toList()
    }

    private fun addVisitDate(schoolSchedule: SchoolSchedule, visitDate: Schedule.ScheduleDate) {
        val visitSchedule = visitScheduleMap[schoolSchedule.schoolCode]
        visitSchedule!!.addVisitDate(visitDate)
        visitDates.add(visitDate.date)
    }

    private fun updateVisitTargetDates() {
        visitTargetDates = visitTargetDates
            .filter { visitDates.contains(it.date) == false }
            .toList()
    }
}
