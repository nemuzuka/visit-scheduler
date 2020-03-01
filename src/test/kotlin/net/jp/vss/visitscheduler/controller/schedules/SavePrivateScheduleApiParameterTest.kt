package net.jp.vss.visitscheduler.controller.schedules

import javax.validation.Validation
import net.jp.vss.visitscheduler.usecase.schedules.SavePrivateScheduleUseCaseParameter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * SavePrivateScheduleApiParameter のテスト.
 */
class SavePrivateScheduleApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(SavePrivateScheduleApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = SavePrivateScheduleApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "targetYearAndMonth",
            "targetDayAndMemos")
        assertThat(actual).hasSize(2)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = SavePrivateScheduleApiParameterFixtures.create()

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
        val sut = SavePrivateScheduleApiParameter(
            targetYearAndMonth = "2019-111",
            targetDayAndMemos = listOf(
                SavePrivateScheduleApiParameter.TargetDayAndMemo(0, null))
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(2)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "targetYearAndMonth must match \"^[0-9]{4}-[0-9]{2}\$\"",
            "targetDayAndMemos[0].targetDay must be greater than or equal to 1")
    }

    @Test
    fun testToParameter() {
        // setup
        val sut = SavePrivateScheduleApiParameterFixtures.create()

        // execution
        val actual = sut.toParameter("USER_0001")

        // verify
        val expected = SavePrivateScheduleUseCaseParameter(
            targetDateString = sut.targetYearAndMonth!!,
            createUserCode = "USER_0001",
            targetDayAndMemos = listOf(
                SavePrivateScheduleUseCaseParameter.TargetDayAndMemo(
                    sut.targetDayAndMemos!![0].targetDay!!,
                    sut.targetDayAndMemos!![0].memo!!),
                SavePrivateScheduleUseCaseParameter.TargetDayAndMemo(
                    sut.targetDayAndMemos!![1].targetDay!!,
                    sut.targetDayAndMemos!![1].memo!!)))
        assertThat(actual).isEqualTo(expected)
    }
}
