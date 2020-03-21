package net.jp.vss.visitscheduler.domain.schedules.calculate

import java.time.temporal.ChronoUnit
import kotlin.math.abs
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School

/**
 * 確定スケジュール.
 *
 * @property details 詳細リスト
 * @property visitRules 訪問設定
 */
data class ScheduleResult(
    val details: List<Detail>,
    val visitRules: VisitRules
) {

    companion object {
        fun of(visitSchedules: List<VisitSchedule>, visitRules: VisitRules): ScheduleResult {
            return ScheduleResult(
                visitSchedules.map { Detail.of(it) }.toList(), visitRules)
        }
    }

    /**
     * 全て確定しているか.
     *
     * @return 全て確定している場合、true
     */
    fun isCompleted() = details.all { it.completed }

    /**
     * 詳細.
     *
     * @property schoolCode 学校コード
     * @property visitDates 今月の訪問日
     * @property completed 設定完了しているか
     * @property differenceDays 訪問日の間隔(絶対値)
     */
    data class Detail(
        val schoolCode: School.SchoolCode,
        val visitDates: List<Schedule.ScheduleDate>,
        val completed: Boolean,
        val differenceDays: Long?
    ) {
        companion object {
            fun of(visitSchedule: VisitSchedule): Detail {
                val schoolCode = visitSchedule.schoolCode
                val visitDates = visitSchedule.getVisitDateList()
                val completed = visitSchedule.isCompleted()
                val differenceDays = if (visitDates.size == 2) {
                    val firstDate = visitDates[0].date
                    val secondDate = visitDates[1].date
                    abs(ChronoUnit.DAYS.between(firstDate, secondDate))
                } else null
                return Detail(schoolCode, visitDates, completed, differenceDays)
            }
        }
    }
}
