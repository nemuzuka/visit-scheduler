package net.jp.vss.visitscheduler.domain.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * PrivateSchedule のテスト.
 */
class PrivateScheduleTest {

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
    fun testOf() {
        // setup
        val userCode = User.UserCode("CUSTOMER-0004")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2019-01")
        val targetDayAndMemos = PrivateSchedule.TargetDayAndMemos(listOf(
            PrivateSchedule.TargetDayAndMemo(1, "メモ1"),
            PrivateSchedule.TargetDayAndMemo(10, "メモ2"),
            PrivateSchedule.TargetDayAndMemo(20, "メモ3")))

        // execution
        val actual = PrivateSchedule.of(userCode, targetYearAndMonth, targetDayAndMemos)

        // verify
        assertThat(actual).hasSize(3)

        val expectedResourceAttributes = ResourceAttributes.buildForCreate(userCode.value)
        assertThat(actual[0])
            .isEqualTo(
                PrivateSchedule(Schedule.ScheduleDate(LocalDate.parse("2019-01-01")),
                    PrivateSchedule.PrivateScheduleDetail(memo = "メモ1"), expectedResourceAttributes))
        assertThat(actual[1])
            .isEqualTo(
                PrivateSchedule(Schedule.ScheduleDate(LocalDate.parse("2019-01-10")),
                    PrivateSchedule.PrivateScheduleDetail(memo = "メモ2"), expectedResourceAttributes))
        assertThat(actual[2])
            .isEqualTo(
                PrivateSchedule(Schedule.ScheduleDate(LocalDate.parse("2019-01-20")),
                    PrivateSchedule.PrivateScheduleDetail(memo = "メモ3"), expectedResourceAttributes))
    }
}
