package net.jp.vss.visitscheduler.controller.schedules

import com.google.common.base.Strings.repeat
import javax.validation.Validation
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.usecase.schedules.UpdateSchoolCodeAndCalculationTargetUseCaseParameter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * UpdateSchoolCodeAndCalculationTargetApiParameter のテスト.
 */
class UpdateSchoolCodeAndCalculationTargetApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(UpdateSchoolCodeAndCalculationTargetApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = UpdateSchoolCodeAndCalculationTargetApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "schoolCodeAndCalculationTargets")
        assertThat(actual).hasSize(1)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = UpdateSchoolCodeAndCalculationTargetApiParameterFixtures.create()

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
        val sut = UpdateSchoolCodeAndCalculationTargetApiParameter(
            schoolCodeAndCalculationTargets = listOf(
                SchoolCodeAndCalculationTarget(repeat("x", 129), null))
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(3)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
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
        val sut = UpdateSchoolCodeAndCalculationTargetApiParameter(
            schoolCodeAndCalculationTargets = listOf(
                SchoolCodeAndCalculationTarget("スクールコード", false))
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(1)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "schoolCodeAndCalculationTargets[0].schoolCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }

    @Test
    fun testToParameter() {
        // setup
        val sut = UpdateSchoolCodeAndCalculationTargetApiParameterFixtures.create()

        // execution
        val actual = sut.toParameter("SCHEDULE-0001", "USER_0001")

        // verify
        val expected = UpdateSchoolCodeAndCalculationTargetUseCaseParameter(
            scheduleCode = Schedule.ScheduleCode("SCHEDULE-0001"),
            userCode = User.UserCode("USER_0001"),
                schoolCodeAndCalculationTargets = listOf(
                    net.jp.vss.visitscheduler.usecase.schedules.SchoolCodeAndCalculationTarget(
                        sut.schoolCodeAndCalculationTargets!![0].schoolCode!!,
                        sut.schoolCodeAndCalculationTargets!![0].calculationTarget!!),
                    net.jp.vss.visitscheduler.usecase.schedules.SchoolCodeAndCalculationTarget(
                        sut.schoolCodeAndCalculationTargets!![1].schoolCode!!,
                        sut.schoolCodeAndCalculationTargets!![1].calculationTarget!!)))
        assertThat(actual).isEqualTo(expected)
    }
}
