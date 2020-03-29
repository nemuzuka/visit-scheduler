package net.jp.vss.visitscheduler.usecase.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.schedules.PrivateScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleFixtures
import net.jp.vss.visitscheduler.usecase.schools.SchoolUseCaseResultFixtures

/**
 * ScheduleDetailUseCaseResult の Fixtures.
 */
class ScheduleDetailUseCaseResultFixtures {
    companion object {
        fun create(): ScheduleDetailUseCaseResult {
            val schoolWithScheduleUseCaseResult = ScheduleDetailUseCaseResult.SchoolWithScheduleUseCaseResult(
                SchoolUseCaseResultFixtures.create(),
                true,
                SchoolScheduleUseCaseResult.of(listOf(SchoolScheduleFixtures.create()),
                    Schedule.ScheduleDate(LocalDate.parse("2019-12-26"))))
            return ScheduleDetailUseCaseResult(
                ScheduleUseCaseResultFixtures.create(),
                listOf(PrivateScheduleUseCaseResult.of(PrivateScheduleFixtures.create())),
                ScheduleDetailUseCaseResult.SchoolWithSchedulesUseCaseResult(listOf(schoolWithScheduleUseCaseResult)),
                CalculateUseCaseResultFixtures.create())
        }
    }
}
