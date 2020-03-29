package net.jp.vss.visitscheduler.controller.schedules

import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule

/**
 * SaveSchoolScheduleApiParameter の Fixture.
 */
class SaveSchoolScheduleApiParameterFixtures {

    companion object {
        fun create() = SaveSchoolScheduleApiParameter(
                targetYearAndMonth = "2019-12",
                schoolCode = "SCHOOL_0001",
                targetDayAndMemos = listOf(
                    SaveSchoolScheduleApiParameter.TargetDayAndMemo(1, "メモ1", SchoolSchedule.Priority.DONT_COME),
                    SaveSchoolScheduleApiParameter.TargetDayAndMemo(3, "メモ2", SchoolSchedule.Priority.POSSIBLE)),
                lastMonthVisitDate = "2019-11-25")
    }
}
