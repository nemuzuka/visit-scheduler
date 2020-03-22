package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.schools.School

/**
 * 学校訪問日.
 *
 * @property visitSchedules 学校訪問日リスト
 */
data class VisitSchedules(
    val visitSchedules: List<VisitSchedule>
) {
    /**
     * 学校の訪問日
     *
     * @property visitDate 訪問日
     * @property schoolCode 学校コード.
     */
    data class VisitSchedule(
        val visitDate: Schedule.ScheduleDate,
        val schoolCode: School.SchoolCode
    )
}
