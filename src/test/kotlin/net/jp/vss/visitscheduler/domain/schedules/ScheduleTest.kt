package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
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
        val actual = Assertions.catchThrowable { sut.validateVersion(version) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(UnmatchVersionException::class.java) { e ->
            assertThat(e.message).isEqualTo("指定した version が不正です")
        }
    }
}
