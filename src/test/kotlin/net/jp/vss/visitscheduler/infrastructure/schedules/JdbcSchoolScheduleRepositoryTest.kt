package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleFixtures
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolDao
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolEntityFixtures
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * JdbcSchoolScheduleRepository のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class JdbcSchoolScheduleRepositoryTest {

    @Autowired
    private lateinit var sut: JdbcSchoolScheduleRepository

    @Autowired
    private lateinit var schoolDao: SchoolDao

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    fun testSave() {
        // setup
        val userCode = User.UserCode("user_code_001")
        val schoolCode = School.SchoolCode("school_code_002")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2019-01")
        val schoolSchedules =
            listOf(SchoolScheduleFixtures.create()
                .copy(targetDate = Schedule.ScheduleDate(LocalDate.parse("2019-01-15")),
                    schoolCode = schoolCode))

        // execution
        sut.save(userCode, schoolCode, targetYearAndMonth,
            schoolSchedules, Schedule.ScheduleDate(LocalDate.of(2018, 12, 28)))

        // verify
        val actual = sut.getSchoolSchedules(listOf(schoolCode), targetYearAndMonth)
        assertThat(actual.first).isEqualTo(schoolSchedules)
        assertThat(actual.second).isEqualTo(mapOf(schoolCode to Schedule.ScheduleDate(LocalDate.of(2018, 12, 28))))
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    fun testFk() {
        // setup
        val userCode = User.UserCode("user_code_001")
        val schoolCode = School.SchoolCode("school_code_002")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2019-01")
        val schoolSchedules =
            listOf(SchoolScheduleFixtures.create()
                .copy(targetDate = Schedule.ScheduleDate(LocalDate.parse("2019-01-15")),
                    schoolCode = schoolCode))
        sut.save(userCode, schoolCode, targetYearAndMonth, schoolSchedules,
            Schedule.ScheduleDate(LocalDate.of(2018, 12, 28)))

        // execution
        val schoolEntity = SchoolEntityFixtures.create().copy(schoolId = "school_id_002", versionNo = 2)
        schoolDao.delete(schoolEntity)

        // verify
        val result = sut.getSchoolSchedules(listOf(schoolCode), targetYearAndMonth)
        assertThat(result.first).isEmpty()
        assertThat(result.second).isEmpty()
    }

    @Test
    @DisplayName("save のテスト.削除していることの確認")
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    fun testSave_DeleteRows() {
        // setup
        val userCode = User.UserCode("user_code_001")
        val schoolCode = School.SchoolCode("school_code_002")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2019-01")
        val beforeSchoolSchedules =
            listOf(SchoolScheduleFixtures.create()
                .copy(targetDate = Schedule.ScheduleDate(LocalDate.parse("2019-01-15"))))
        sut.save(userCode, schoolCode, targetYearAndMonth, beforeSchoolSchedules,
            Schedule.ScheduleDate(LocalDate.parse("2018-12-29")))
        val schoolSchedules =
            listOf(
                SchoolScheduleFixtures.create()
                .copy(targetDate = Schedule.ScheduleDate(LocalDate.parse("2019-01-01")),
                    schoolCode = schoolCode),
                SchoolScheduleFixtures.create()
                    .copy(targetDate = Schedule.ScheduleDate(LocalDate.parse("2019-01-31")),
                        schoolCode = schoolCode))

        // execution
        sut.save(userCode, schoolCode, targetYearAndMonth, schoolSchedules, null)

        // verify
        val actual = sut.getSchoolSchedules(listOf(schoolCode), targetYearAndMonth)
        assertThat(actual.first).isEqualTo(schoolSchedules)
        assertThat(actual.second).isEqualTo(mapOf(schoolCode to null))
    }
}
