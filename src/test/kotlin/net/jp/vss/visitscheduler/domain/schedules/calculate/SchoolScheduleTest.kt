package net.jp.vss.visitscheduler.domain.schedules.calculate

import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/** SchoolSchedule のテスト. */
class SchoolScheduleTest {

    @Test
    fun testIsforceVisitDate_EmptyForceVisitDates() {
        // setup
        val sut = SchoolSchedule(schoolCode = School.SchoolCode("dummy"),
            exclusionDates = listOf(), lastMonthVisitDate = null, forceVisitDates = listOf(),
            priorityVisitDates = listOf())

        // exercise
        val actual = sut.isForceVisitDate(Schedule.ScheduleDate(LocalDate.of(2020, 1, 6)))

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    fun testIsforceVisitDate_ContainsForceVisitDates() {
        // setup
        val sut = SchoolSchedule(schoolCode = School.SchoolCode("dummy"),
            exclusionDates = listOf(), lastMonthVisitDate = null,
            forceVisitDates = listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2020, 1, 9))),
            priorityVisitDates = listOf())

        // exercise
        val actual = sut.isForceVisitDate(Schedule.ScheduleDate(LocalDate.of(2020, 1, 6)))

        // verify
        assertThat(actual).isTrue()
    }

    @Test
    fun testIsforceVisitDate_NotContainsForceVisitDates() {
        // setup
        val sut = SchoolSchedule(schoolCode = School.SchoolCode("dummy"),
            exclusionDates = listOf(), lastMonthVisitDate = null,
            forceVisitDates = listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2020, 1, 9))),
            priorityVisitDates = listOf())

        // exercise
        val actual = sut.isForceVisitDate(Schedule.ScheduleDate(LocalDate.of(2020, 1, 7)))

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    fun testIsPriorityVisitDate_NullPriorityVisitDates() {
        // setup
        val sut = SchoolSchedule(schoolCode = School.SchoolCode("dummy"),
            exclusionDates = listOf(), lastMonthVisitDate = null, forceVisitDates = listOf(),
            priorityVisitDates = listOf())

        // exercise
        val actual = sut.isPriorityVisitDate(Schedule.ScheduleDate(LocalDate.of(2020, 1, 6)))

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    fun testIsPriorityVisitDate_ContainsPriorityVisitDates() {
        // setup
        val sut = SchoolSchedule(schoolCode = School.SchoolCode("dummy"),
            exclusionDates = listOf(), lastMonthVisitDate = null, forceVisitDates = listOf(),
            priorityVisitDates = listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))))

        // exercise
        val actual = sut.isPriorityVisitDate(Schedule.ScheduleDate(LocalDate.of(2020, 1, 6)))

        // verify
        assertThat(actual).isTrue()
    }

    @Test
    fun testIsPriorityVisitDate_NotContainsPriorityVisitDates() {
        // setup
        val sut = SchoolSchedule(schoolCode = School.SchoolCode("dummy"),
            exclusionDates = listOf(), lastMonthVisitDate = null, forceVisitDates = listOf(),
            priorityVisitDates = listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))))

        // exercise
        val actual = sut.isPriorityVisitDate(Schedule.ScheduleDate(LocalDate.of(2020, 1, 7)))

        // verify
        assertThat(actual).isFalse()
    }
}
