package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import net.jp.vss.visitscheduler.domain.schedules.PrivateScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * JdbcPrivateScheduleRepository のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class JdbcPrivateScheduleRepositoryTest {

    @Autowired
    private lateinit var sut: JdbcPrivateScheduleRepository

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    fun testSave() {
        // setup
        val userCode = User.UserCode("user_code_001")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2019-01")
        val privateSchedules =
            listOf(PrivateScheduleFixtures.create()
                .copy(targetDate = Schedule.ScheduleDate(LocalDate.parse("2019-01-15"))))

        // execution
        sut.save(userCode, targetYearAndMonth, privateSchedules)

        // verify
        val actual = sut.getPrivateSchedules(userCode, targetYearAndMonth)
        assertThat(actual).isEqualTo(privateSchedules)
    }

    @Test
    @DisplayName("save のテスト.削除していることの確認")
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    fun testSave_DeleteRows() {
        // setup
        val userCode = User.UserCode("user_code_001")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2019-01")
        val beforePrivateSchedules =
            listOf(PrivateScheduleFixtures.create()
                .copy(targetDate = Schedule.ScheduleDate(LocalDate.parse("2019-01-15"))))
        sut.save(userCode, targetYearAndMonth, beforePrivateSchedules)
        val privateSchedules =
            listOf(PrivateScheduleFixtures.create()
                .copy(targetDate = Schedule.ScheduleDate(LocalDate.parse("2019-01-01"))),
                PrivateScheduleFixtures.create()
                    .copy(targetDate = Schedule.ScheduleDate(LocalDate.parse("2019-01-31"))))

        // execution
        sut.save(userCode, targetYearAndMonth, privateSchedules)

        // verify
        val actual = sut.getPrivateSchedules(userCode, targetYearAndMonth)
        assertThat(actual).isEqualTo(privateSchedules)
    }
}
