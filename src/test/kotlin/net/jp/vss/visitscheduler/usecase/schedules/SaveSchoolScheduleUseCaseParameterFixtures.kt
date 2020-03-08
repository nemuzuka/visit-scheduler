package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule

/**
 * SaveSchoolScheduleUseCaseParameter の Fixture.
 */
class SaveSchoolScheduleUseCaseParameterFixtures {
    companion object {
        fun create() = SaveSchoolScheduleUseCaseParameter("2019-01",
            "USER_0011",
            "SCHOOL_01A",
            listOf(
                SaveSchoolScheduleUseCaseParameter.TargetDayAndMemo(3, "授業参観", SchoolSchedule.Priority.DONT_COME),
                SaveSchoolScheduleUseCaseParameter.TargetDayAndMemo(21, "補修", SchoolSchedule.Priority.POSSIBLE)))
    }
}
