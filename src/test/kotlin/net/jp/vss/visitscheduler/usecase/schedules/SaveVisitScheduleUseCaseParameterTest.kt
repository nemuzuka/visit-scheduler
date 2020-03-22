package net.jp.vss.visitscheduler.usecase.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedules
import net.jp.vss.visitscheduler.domain.schools.School
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/** SaveVisitScheduleUseCaseParameter のテスト. */
class SaveVisitScheduleUseCaseParameterTest {

    @Test
    fun testToVisitSchedules() {
        // setup
        val sut = SaveVisitScheduleUseCaseParameterFixtures.create()

        // execution
        val actual = sut.toVisitSchedules()

        // verify
        val visitSchedule = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2019, 11, 15)), School.SchoolCode("SCHOOL-001"))
        assertThat(actual).isEqualTo(VisitSchedules(listOf(visitSchedule)))
    }
}
