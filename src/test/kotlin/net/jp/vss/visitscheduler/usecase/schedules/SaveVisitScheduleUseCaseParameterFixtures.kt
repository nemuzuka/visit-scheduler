package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User

/** SaveVisitScheduleUseCaseParameter の Fixtures. */
class SaveVisitScheduleUseCaseParameterFixtures {
    companion object {
        fun create(): SaveVisitScheduleUseCaseParameter {
            val visitDayAndSchoolCode =
                SaveVisitScheduleUseCaseParameter.VisitDayAndSchoolCode(School.SchoolCode("SCHOOL-001"), 15)
            return SaveVisitScheduleUseCaseParameter("2019-11",
                User.UserCode("USER-0001"), listOf(visitDayAndSchoolCode))
        }
    }
}
