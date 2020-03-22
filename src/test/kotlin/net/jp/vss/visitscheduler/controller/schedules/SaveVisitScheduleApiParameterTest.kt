package net.jp.vss.visitscheduler.controller.schedules

import javax.validation.Validation
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.usecase.schedules.SaveVisitScheduleUseCaseParameter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/** SaveVisitScheduleApiParameter の Test. */
class SaveVisitScheduleApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(SaveVisitScheduleApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = SaveVisitScheduleApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "targetYearAndMonth",
            "visitDayAndSchoolCodes")
        assertThat(actual).hasSize(2)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = SaveVisitScheduleApiParameterFixtures.create()

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
        val sut = SaveVisitScheduleApiParameter(
            targetYearAndMonth = "2019-111",
            visitDayAndSchoolCodes = listOf(
                SaveVisitScheduleApiParameter.VisitDayAndSchoolCodeParameter(0, null))
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(3)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "targetYearAndMonth must match \"^[0-9]{4}-[0-9]{2}\$\"",
            "visitDayAndSchoolCodes[0].schoolCode must not be null",
            "visitDayAndSchoolCodes[0].visitDay must be greater than or equal to 1")
    }

    @Test
    fun testToParameter() {
        // setup
        val sut = SaveVisitScheduleApiParameterFixtures.create()

        // execution
        val actual = sut.toParameter("USER-001")

        // verify
        val visitDayAndSchoolCode = SaveVisitScheduleUseCaseParameter.VisitDayAndSchoolCode(
            School.SchoolCode("SCHOOL_0001"), 21)
        val expected = SaveVisitScheduleUseCaseParameter("2020-03", User.UserCode("USER-001"),
            listOf(visitDayAndSchoolCode))
        assertThat(actual).isEqualTo(expected)
    }
}
