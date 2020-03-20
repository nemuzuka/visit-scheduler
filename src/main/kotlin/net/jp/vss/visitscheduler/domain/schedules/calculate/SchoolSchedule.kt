package net.jp.vss.visitscheduler.domain.schedules.calculate

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School

/**
 * 計算用学校のスケジュール.
 *
 * <p>
 * - 強制訪問日: ここで設定した日付は強制的に訪問日とします。 ひと月に1学校辺り最大2日設定できます
 * - 優先訪問日: ここで設定した日付を訪問日になるようにスケジュールを組みます。 強制訪問日と優先訪問日の指定でも訪問日が2つ埋まらない場合、
 *     勤務者の訪問可能日リスト - 強制訪問日 - 優先訪問日 - 訪問除外日 の中から訪問日を設定します
 * </p>
 * @property schoolCode 学校コード
 * @property exclusionDates 訪問除外日リスト
 * @property lastMonthVisitDate 先月の最終訪問日
 * @property forceVisitDates 強制訪問日
 * @property priorityVisitDates 優先訪問日
 */
data class SchoolSchedule(
    val schoolCode: School.SchoolCode,
    val exclusionDates: List<Schedule.ScheduleDate>,
    val lastMonthVisitDate: Schedule.ScheduleDate?,
    val forceVisitDates: List<Schedule.ScheduleDate>,
    val priorityVisitDates: List<Schedule.ScheduleDate>
) {

    /**
     * 強制訪問日か？
     *
     * @param targetDate 対象日
     * @return 強制訪問日として設定されていれば true
     */
    fun isForceVisitDate(targetDate: Schedule.ScheduleDate) = forceVisitDates.contains(targetDate)

    /**
     * 優先訪問日か？
     *
     * @param targetDate 対象日
     * @return 優先訪問日として設定されていれば true
     */
    fun isPriorityVisitDate(targetDate: Schedule.ScheduleDate) = priorityVisitDates.contains(targetDate)
}
