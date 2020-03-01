package net.jp.vss.visitscheduler.domain.schedules

import java.lang.IllegalArgumentException
import java.time.LocalDate
import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Schedule のテスト.
 */
class ScheduleTest {

    companion object {
        const val NOW = 1546268400001L
    }

    @BeforeEach
    fun setUp() {
        DatetimeUtils.setDummyDatetimeResource(NOW)
    }

    @AfterEach
    fun tearDown() {
        DatetimeUtils.clearDummyDatetimeResource()
    }

    @Test
    fun testBuildForCreate() {
        // setup
        val scheduleCodeValue = "SCHEDULE-0001"
        val userCode = User.UserCode("CUSTOMER-0004")
        val attributeJsonString = """{"hoge":"hige"}"""
        val createUserCode = "CUSTOMER-0003"
        val targetYearAndMonth = "2018-09"

        // execution
        val actual = Schedule.buildForCreate(scheduleCodeValue,
            userCode, targetYearAndMonth, attributeJsonString, createUserCode)

        // verify
        val scheduleCode = Schedule.ScheduleCode(scheduleCodeValue)
        val scheduleDetail = Schedule.ScheduleDetail(attributes = Attributes(attributeJsonString))
        val resourceAttributes = ResourceAttributes.buildForCreate(createUserCode)
        val expected = Schedule(scheduleCode = scheduleCode, userCode = userCode,
            targetYearAndMonth = Schedule.TargetYearAndMonth(targetYearAndMonth),
            scheduleDetail = scheduleDetail, resourceAttributes = resourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testValidateVersion() {
        // setup
        val sut = ScheduleFixtures.create()
        val version = sut.resourceAttributes.version

        // execution
        sut.validateVersion(version) // Exception を throw しないこと
    }

    @Test
    fun testValidateVersion_NoValidate() {
        // setup
        val sut = ScheduleFixtures.create()

        // execution
        sut.validateVersion(null) // Exception を throw しないこと
    }

    @Test
    fun testValidateVersion_InvalidVersion_UVE() {
        // setup
        val sut = ScheduleFixtures.create()
        val version = sut.resourceAttributes.version + 1

        // execution
        val actual = catchThrowable { sut.validateVersion(version) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(UnmatchVersionException::class.java) { e ->
            assertThat(e.message).isEqualTo("指定した version が不正です")
        }
    }

    @Test
    fun testScheduleDateOf() {
        // setup
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-01")

        // execution
        val actual = Schedule.ScheduleDate.of(targetYearAndMonth, 31)

        // verify
        assertThat(actual).isEqualTo(Schedule.ScheduleDate(LocalDate.parse("2020-01-31")))
    }

    @Test
    fun testScheduleDateOf_InvalidDay_ThrowIllegalArgumentException() {
        // setup
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-01")

        // execution
        val actual = catchThrowable { Schedule.ScheduleDate.of(targetYearAndMonth, 32) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(IllegalArgumentException::class.java) { e ->
            assertThat(e.message).isEqualTo("2020-01-32 は不正な日付です。")
        }
    }
}
