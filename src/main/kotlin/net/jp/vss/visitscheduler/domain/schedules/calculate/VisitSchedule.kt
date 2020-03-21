package net.jp.vss.visitscheduler.domain.schedules.calculate

import com.google.common.annotations.VisibleForTesting
import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School

data class VisitSchedule(
    val schoolCode: School.SchoolCode,
    val exclusionDates: List<Schedule.ScheduleDate>,
    val lastMonthVisitDate: Schedule.ScheduleDate?
) {

    companion object {
        fun of(schoolSchedule: SchoolSchedule): VisitSchedule =
            VisitSchedule(schoolSchedule.schoolCode, schoolSchedule.exclusionDates, schoolSchedule.lastMonthVisitDate)
    }

    /** 今月の訪問日. */
    private var visitDates: MutableList<Schedule.ScheduleDate> = mutableListOf()

    fun getVisitDateList() = visitDates.toList()

    /**
     * 今月の訪問日追加.
     *
     * @param visitDate 訪問日
     */
    fun addVisitDate(visitDate: Schedule.ScheduleDate) {
        visitDates.add(visitDate)
    }

    /**
     * 今月の訪問日は埋まったか？
     *
     * @return 今月の訪問日が埋まった場合、true
     */
    fun isCompleted(): Boolean = getEmptyVisitDateCount() == 0

    /**
     * 今月の訪問日の空き数取得
     *
     * @return 今月の訪問日の空き数
     */
    fun getEmptyVisitDateCount(): Int {
        return 2 - visitDates.size
    }

    /**
     * 対象日が訪問日か？
     *
     * <p>
     * - 訪問日が全て埋まっていない
     * - 除外日として指定されていないこと
     * - 先月の最終出勤日 + 先月の最終訪問日から次回訪問日に空ける日数 よりも後の日付であること
     * - 確定した訪問日 ± 当月の初回訪問日から次回訪問日に空ける日数 の範囲外であること(全ての確定した訪問日に対してチェックする)
     *
     * を満たす場合、訪問日とします。
     * </p>
     *
     * @param targetDate 対象日
     * @param visitRules 訪問設定
     * @return 訪問日として使用可能であれば true
     */
    fun isVisitTargetDate(targetDate: Schedule.ScheduleDate, visitRules: VisitRules): Boolean {
        if (isCompleted()) {
            return false
        }

        if (exclusionDates.contains(targetDate)) {
            return false
        }

        val isOutOfRangeFromVisitDate = visitDates.all { isOutOfRangeFromVisitDate(it, targetDate, visitRules) }
        return isOutOfRangeFromLastMonthVisitDate(targetDate.date, visitRules) && isOutOfRangeFromVisitDate
    }

    @VisibleForTesting
    fun isOutOfRangeFromLastMonthVisitDate(targetDate: LocalDate, visitRules: VisitRules): Boolean {
        if (lastMonthVisitDate == null) {
            return true
        }
        val lastDate: LocalDate = lastMonthVisitDate.date.plusDays(visitRules.daysToWaitSinceLastVisit.toLong())
        return targetDate.isAfter(lastDate)
    }

    @VisibleForTesting
    fun isOutOfRangeFromVisitDate(
        visitDate: Schedule.ScheduleDate,
        targetDate: Schedule.ScheduleDate,
        visitRules: VisitRules
    ): Boolean {
        val daysToWaitSinceVisitForTargetMonth = visitRules.daysToWaitSinceVisitForTargetMonth
        val visitDateValue = visitDate.date
        val startdate = visitDateValue.minusDays(daysToWaitSinceVisitForTargetMonth.toLong())
        val lastDate = visitDateValue.plusDays(daysToWaitSinceVisitForTargetMonth.toLong())
        val targetDateValue = targetDate.date
        return targetDateValue.isBefore(startdate) || targetDateValue.isAfter(lastDate)
    }
}
