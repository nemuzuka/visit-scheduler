package net.jp.vss.visitscheduler.domain.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * SchoolSchedule のテスト.
 */
class SchoolScheduleTest {

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
        val schoolCode = School.SchoolCode("SCHOOL-0003")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2019-01")
        val targetDayAndMemos = SchoolSchedule.TargetDayAndMemos(listOf(
            SchoolSchedule.TargetDayAndMemo(1, SchoolSchedule.Priority.DONT_COME, "メモ1"),
            SchoolSchedule.TargetDayAndMemo(10, SchoolSchedule.Priority.ABSOLUTELY, "メモ2"),
            SchoolSchedule.TargetDayAndMemo(20, SchoolSchedule.Priority.POSSIBLE, "メモ3")))

        // execution
        val actual = SchoolSchedule.of(userCode, schoolCode, targetYearAndMonth, targetDayAndMemos)

        // verify
        assertThat(actual).hasSize(3)

        val expectedResourceAttributes = ResourceAttributes.buildForCreate(userCode.value)
        assertThat(actual[0])
            .isEqualTo(
                SchoolSchedule(Schedule.ScheduleDate(LocalDate.parse("2019-01-01")),
                    schoolCode,
                    SchoolSchedule.SchoolScheduleDetail(memo = "メモ1", priority = SchoolSchedule.Priority.DONT_COME),
                    expectedResourceAttributes))
        assertThat(actual[1])
            .isEqualTo(
                SchoolSchedule(Schedule.ScheduleDate(LocalDate.parse("2019-01-10")),
                    schoolCode,
                    SchoolSchedule.SchoolScheduleDetail(memo = "メモ2", priority = SchoolSchedule.Priority.ABSOLUTELY),
                    expectedResourceAttributes))
        assertThat(actual[2])
            .isEqualTo(
                SchoolSchedule(Schedule.ScheduleDate(LocalDate.parse("2019-01-20")),
                    schoolCode,
                    SchoolSchedule.SchoolScheduleDetail(memo = "メモ3", priority = SchoolSchedule.Priority.POSSIBLE),
                    expectedResourceAttributes))
    }
}
