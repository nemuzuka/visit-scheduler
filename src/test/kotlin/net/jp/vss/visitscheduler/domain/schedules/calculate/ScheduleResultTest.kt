package net.jp.vss.visitscheduler.domain.schedules.calculate

import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * ScheduleResult のテスト.
 */
class ScheduleResultTest {

    @Test
    fun testOf() {
        // setup
        val visitSchedule = VisitSchedule(School.SchoolCode("ID-0001"), listOf(), null)
        visitSchedule.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 31)))
        visitSchedule.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 16)))

        val visitSchedules = listOf(visitSchedule)
        val visitRules = VisitRules(7, 14)

        // exercise
        val actual = ScheduleResult.of(visitSchedules, visitRules)

        // verify
        val detail = ScheduleResult.Detail(
            visitSchedule.schoolCode, visitSchedule.getVisitDateList(), true, 15L)
        val details = listOf(detail)
        val expected = ScheduleResult(details, visitRules)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testOf_NotCompleted() {
        // setup
        val visitSchedule = VisitSchedule(School.SchoolCode("ID-0001"), listOf(), null)
        visitSchedule.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)))

        val visitSchedules = listOf(visitSchedule)
        val visitRules = VisitRules(7, 14)

        // exercise
        val actual = ScheduleResult.of(visitSchedules, visitRules)

        // verify
        val detail = ScheduleResult.Detail(
            visitSchedule.schoolCode, visitSchedule.getVisitDateList(), false, null)
        val details = listOf(detail)
        val expected = ScheduleResult(details, visitRules)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testIsCompleted_True() {
        // setup
        val detail = ScheduleResult.Detail(School.SchoolCode("ID-0001"), listOf(), true, null)
        val details = listOf(detail)
        val visitRules = VisitRules(7, 14)
        val sut = ScheduleResult(details, visitRules)

        // exercise
        val actual = sut.isCompleted()

        // verify
        assertThat(actual).isTrue()
    }

    @Test
    fun testIsCompleted_False() {
        // setup
        val detail = ScheduleResult.Detail(School.SchoolCode("ID-0001"), listOf(), false, null)
        val details = listOf(detail)
        val visitRules = VisitRules(7, 14)
        val sut = ScheduleResult(details, visitRules)

        // exercise
        val actual = sut.isCompleted()

        // verify
        assertThat(actual).isFalse()
    }
}
