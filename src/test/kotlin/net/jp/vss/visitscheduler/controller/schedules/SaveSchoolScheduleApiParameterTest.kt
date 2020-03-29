package net.jp.vss.visitscheduler.controller.schedules

import java.lang.IllegalArgumentException
import java.time.LocalDate
import javax.validation.Validation
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.usecase.schedules.SaveSchoolScheduleUseCaseParameter
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * SaveSchoolScheduleApiParameter のテスト.
 */
class SaveSchoolScheduleApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(SaveSchoolScheduleApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = SaveSchoolScheduleApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "targetYearAndMonth",
            "schoolCode",
            "targetDayAndMemos")
        assertThat(actual).hasSize(3)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = SaveSchoolScheduleApiParameterFixtures.create()

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
        val sut = SaveSchoolScheduleApiParameter(
            targetYearAndMonth = "2019-111",
            schoolCode = "スクールコード",
            targetDayAndMemos = listOf(
                SaveSchoolScheduleApiParameter.TargetDayAndMemo(0, null, null)),
            lastMonthVisitDate = "2018-12-3a"
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(5)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "schoolCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"",
            "targetYearAndMonth must match \"^[0-9]{4}-[0-9]{2}\$\"",
            "targetDayAndMemos[0].priority must not be null",
            "targetDayAndMemos[0].targetDay must be greater than or equal to 1",
            "lastMonthVisitDate must match \"^[0-9]{4}-[0-9]{2}-[0-9]{2}\$\"")
    }

    @Test
    fun testToParameter() {
        // setup
        val sut = SaveSchoolScheduleApiParameterFixtures.create()

        // execution
        val actual = sut.toParameter("USER_0001")

        // verify
        val expected = SaveSchoolScheduleUseCaseParameter(
            targetDateString = sut.targetYearAndMonth!!,
            createUserCode = "USER_0001",
            schoolCode = sut.schoolCode!!,
            targetDayAndMemos = listOf(
                SaveSchoolScheduleUseCaseParameter.TargetDayAndMemo(
                    sut.targetDayAndMemos!![0].targetDay!!,
                    sut.targetDayAndMemos!![0].memo!!,
                    sut.targetDayAndMemos!![0].priority!!),
                SaveSchoolScheduleUseCaseParameter.TargetDayAndMemo(
                    sut.targetDayAndMemos!![1].targetDay!!,
                    sut.targetDayAndMemos!![1].memo!!,
                    sut.targetDayAndMemos!![1].priority!!)),
            lastMonthVisitDate = Schedule.ScheduleDate(LocalDate.parse(sut.lastMonthVisitDate)))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testToParameter_InvalidLastMonthVisitDate_NotBeforeMonth() {
        // setup
        val sut = SaveSchoolScheduleApiParameterFixtures.create()
            .copy(lastMonthVisitDate = "2019-12-01")

        // execution
        val actual = catchThrowable { sut.toParameter("USER_0001") }

        // verify
        assertThat(actual).isInstanceOfSatisfying(IllegalArgumentException::class.java) {
            e -> assertThat(e.message).isEqualTo("先月の訪問日は対象年月(2019-12)よりも前の月の日付を設定してください。") }
    }

    @Test
    fun testToParameter_InvalidLastMonthVisitDate_NotLastMonth() {
        // setup
        val sut = SaveSchoolScheduleApiParameterFixtures.create()
            .copy(lastMonthVisitDate = "2019-10-20") // 先月でない過去日付

        // execution
        val actual = catchThrowable { sut.toParameter("USER_0001") }

        // verify
        assertThat(actual).isInstanceOfSatisfying(IllegalArgumentException::class.java) {
            e -> assertThat(e.message).isEqualTo("先月の訪問日は対象年月(2019-12)の直前の月を設定してください。") }
    }
}
