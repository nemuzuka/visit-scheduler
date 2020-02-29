package net.jp.vss.visitscheduler.controller.schedules

import com.google.common.base.Strings.repeat
import javax.validation.Validation
import net.jp.vss.visitscheduler.usecase.schedules.CreateScheduleUseCaseParameter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * CreateScheduleApiParameter のテスト.
 */
class CreateScheduleApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(CreateScheduleApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = CreateScheduleApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "scheduleCode",
            "targetYearAndMonth",
            "schoolCodeAndCalculationTargets")
        assertThat(actual).hasSize(3)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = CreateScheduleApiParameterFixtures.create()

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
        val sut = CreateScheduleApiParameter(
            scheduleCode = repeat("x", 129),
            targetYearAndMonth = "2019-111",
            attributes = """{"invalid":json_value}""",
            schoolCodeAndCalculationTargets = listOf(
                CreateScheduleApiParameter.SchoolCodeAndCalculationTarget(repeat("x", 129), null))
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(7)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "scheduleCode size must be between 0 and 128",
            "scheduleCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"",
            "attributes must match json string format",
            "targetYearAndMonth must match \"^[0-9]{4}-[0-9]{2}\$\"",
            "schoolCodeAndCalculationTargets[0].schoolCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"",
            "schoolCodeAndCalculationTargets[0].schoolCode size must be between 0 and 128",
            "schoolCodeAndCalculationTargets[0].calculationTarget must not be null")
    }

    /**
     * 異常パターンのテスト.
     */
    @Test
    fun testViolatePatternConstrains_Format() {
        // setup
        val sut = CreateScheduleApiParameter(
            scheduleCode = "スケジュールコード",
            targetYearAndMonth = "2019-11",
            schoolCodeAndCalculationTargets = listOf(
                CreateScheduleApiParameter.SchoolCodeAndCalculationTarget("スクールコード", false))
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(2)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "scheduleCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"",
            "schoolCodeAndCalculationTargets[0].schoolCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }

    @Test
    fun testToParameter() {
        // setup
        val sut = CreateScheduleApiParameterFixtures.create()

        // execution
        val actual = sut.toParameter("USER_0001")

        // verify
        val expected = CreateScheduleUseCaseParameter(
            scheduleCode = sut.scheduleCode!!,
            targetDateString = sut.targetYearAndMonth!!,
            attributes = sut.attributes,
            createUserCode = "USER_0001",
            schoolCodeAndCalculationTargets = listOf(
                CreateScheduleUseCaseParameter.SchoolCodeAndCalculationTarget(
                    sut.schoolCodeAndCalculationTargets!![0].schoolCode!!,
                    sut.schoolCodeAndCalculationTargets!![0].calculationTarget!!),
                CreateScheduleUseCaseParameter.SchoolCodeAndCalculationTarget(
                    sut.schoolCodeAndCalculationTargets!![1].schoolCode!!,
                    sut.schoolCodeAndCalculationTargets!![1].calculationTarget!!)))
        assertThat(actual).isEqualTo(expected)
    }
}
