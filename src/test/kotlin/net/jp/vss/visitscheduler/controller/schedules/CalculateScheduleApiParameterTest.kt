package net.jp.vss.visitscheduler.controller.schedules

import java.time.LocalDate
import javax.validation.Validation
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule.Priority
import net.jp.vss.visitscheduler.domain.schedules.calculate.SchoolSchedule
import net.jp.vss.visitscheduler.domain.schedules.calculate.VisitRules
import net.jp.vss.visitscheduler.domain.schedules.calculate.WorkerSchedule
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.usecase.schedules.CalculateUseCaseParameter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/** CalculateScheduleApiParameter の test. */
class CalculateScheduleApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(CalculateScheduleApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = CalculateScheduleApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "targetYearAndMonth",
            "workerExclusionDates",
            "schoolResuestedSchedules")
        assertThat(actual).hasSize(3)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = CalculateScheduleApiParameterFixtures.create()

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(0)
    }

    /**
     * 異常パターンのテスト.
     */
    @Test
    fun testViolatePatternConstrains() {
        // setup
        val targetDay = CalculateScheduleApiParameter.TargetDay(0)
        val dayWithPriority = CalculateScheduleApiParameter.DayWithPriority(-1, null)
        val schoolResuestedSchedule = CalculateScheduleApiParameter.SchoolResuestedSchedule(
            "スクールコード",
            32,
            listOf(dayWithPriority))
        val sut = CalculateScheduleApiParameter("2020-011",
            listOf(targetDay),
            listOf(schoolResuestedSchedule),
            CalculateScheduleApiParameter.RequestVisitRules(33, 34))

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(8)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "targetYearAndMonth must match \"^[0-9]{4}-[0-9]{2}\$\"",
            "workerExclusionDates[0].day must be greater than or equal to 1",
            "schoolResuestedSchedules[0].schoolCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"",
            "schoolResuestedSchedules[0].lastMonthVisitDay must be less than or equal to 31",
            "schoolResuestedSchedules[0].targetAndPriproties[0].priority must not be null",
            "schoolResuestedSchedules[0].targetAndPriproties[0].day must be greater than or equal to 1",
            "requestVisitRules.daysToWaitSinceVisitForTargetMonth must be less than or equal to 31",
            "requestVisitRules.daysToWaitSinceLastVisit must be less than or equal to 31")
    }

    @Test
    fun testToParameter() {
        // execution
        val actual = CalculateScheduleApiParameterFixtures.create().toParameter()

        // verify
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-01")
        val workerSchedule = WorkerSchedule(listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 1))))
        val schoolSchedule = SchoolSchedule(School.SchoolCode("SCHOOL-001"),
            listOf(),
            Schedule.ScheduleDate(LocalDate.of(2019, 12, 28)),
            listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 3))),
            listOf())
        val schoolSchedules = listOf(schoolSchedule)
        val visitRules = VisitRules(15, 16)
        val expected = CalculateUseCaseParameter(targetYearAndMonth,
            workerSchedule,
            schoolSchedules,
            visitRules,
            10)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testToParameter_WithNullvalue() {
        // setup
        val dayWithPriority1 = CalculateScheduleApiParameter.DayWithPriority(10, Priority.DONT_COME)
        val dayWithPriority2 = CalculateScheduleApiParameter.DayWithPriority(11, Priority.ABSOLUTELY)
        val dayWithPriority3 = CalculateScheduleApiParameter.DayWithPriority(13, Priority.POSSIBLE)
        val schoolResuestedSchedule = CalculateScheduleApiParameter.SchoolResuestedSchedule(
            "SCHOOL-002",
            null,
            listOf(dayWithPriority1, dayWithPriority2, dayWithPriority3))
        val sut = CalculateScheduleApiParameterFixtures.create()
            .copy(schoolResuestedSchedules = listOf(schoolResuestedSchedule), requestVisitRules = null, tryCount = null)

        // execution
        val actual = sut.toParameter()

        // verify
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-01")
        val workerSchedule = WorkerSchedule(listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 1))))
        val schoolSchedule = SchoolSchedule(School.SchoolCode("SCHOOL-002"),
            listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 10))),
            null,
            listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 11))),
            listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 13))))
        val schoolSchedules = listOf(schoolSchedule)
        val visitRules = VisitRules(7, 14)
        val expected = CalculateUseCaseParameter(targetYearAndMonth,
            workerSchedule,
            schoolSchedules,
            visitRules,
            3)
        assertThat(actual).isEqualTo(expected)
    }
}
