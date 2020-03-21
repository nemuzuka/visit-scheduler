package net.jp.vss.visitscheduler.usecase.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.calculate.SchoolSchedule
import net.jp.vss.visitscheduler.domain.schedules.calculate.VisitRules
import net.jp.vss.visitscheduler.domain.schedules.calculate.WorkerSchedule
import net.jp.vss.visitscheduler.domain.schools.School

/** CalculateUseCaseParameter の Fixtures. */
class CalculateUseCaseParameterFixtures {
    companion object {
        fun create(): CalculateUseCaseParameter {
            val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-03")
            val workerSchedule = WorkerSchedule(listOf(Schedule.ScheduleDate(LocalDate.of(2020, 3, 8))))
            val schoolSchedule = SchoolSchedule(
                School.SchoolCode("school_0001"),
                listOf(Schedule.ScheduleDate(LocalDate.of(2020, 3, 10))),
                Schedule.ScheduleDate(LocalDate.of(2020, 2, 28)),
                listOf(Schedule.ScheduleDate(LocalDate.of(2020, 3, 4))),
                listOf(Schedule.ScheduleDate(LocalDate.of(2020, 3, 19))))
            val visitRules = VisitRules(2, 10)
            return CalculateUseCaseParameter(targetYearAndMonth, workerSchedule, listOf(schoolSchedule), visitRules, 5)
        }
    }
}
