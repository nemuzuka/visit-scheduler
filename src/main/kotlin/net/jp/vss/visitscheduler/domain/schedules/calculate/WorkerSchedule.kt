package net.jp.vss.visitscheduler.domain.schedules.calculate

import java.time.YearMonth
import net.jp.vss.visitscheduler.domain.schedules.Schedule

/**
 * 勤務者のスケジュール.
 *
 * @property exclusionDates 訪問除外日
 */
class WorkerSchedule(
    private val exclusionDates: List<Schedule.ScheduleDate>
) {
    /**
     * 訪問可能日リスト生成.
     *
     * <p>
     * 対象年月の１日から月末までの間に
     * 除外日として指定されていない日付を
     * 訪問可能日リストとして返却します。
     * </p>
     * @param targetYearMonth 対象年月
     * @return 訪問可能日リスト
     */
    fun buildVisitTargetDates(targetYearMonth: Schedule.TargetYearAndMonth): List<Schedule.ScheduleDate> {
        val yearMonth = YearMonth.parse(targetYearMonth.value)
        var targetDate = yearMonth.atDay(1)
        val nextMonthOne = yearMonth.plusMonths(1).atDay(1)
        val visitTargetDates: MutableList<Schedule.ScheduleDate> = mutableListOf()
        while (targetDate.isBefore(nextMonthOne)) {
            if (exclusionDates.contains(Schedule.ScheduleDate(targetDate)) == false) {
                visitTargetDates.add(Schedule.ScheduleDate(targetDate))
            }
            targetDate = targetDate.plusDays(1)
        }
        return visitTargetDates.toList()
    }
}
