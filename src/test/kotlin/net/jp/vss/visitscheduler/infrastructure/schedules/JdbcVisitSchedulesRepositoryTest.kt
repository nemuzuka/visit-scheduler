package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedules
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
 * JdbcVisitSchedulesRepository のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class JdbcVisitSchedulesRepositoryTest {

    @Autowired
    private lateinit var sut: JdbcVisitSchedulesRepository

    @Autowired
    private lateinit var visitSchedulesDao: VisitSchedulesDao

    @Autowired
    private lateinit var schoolDao: SchoolDao

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_school_calculation_target"])
    fun testSave() {
        // setup
        val visitSchedule1 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 1)),
            School.SchoolCode("school_code_003"))
        val visitSchedule2 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 31)),
            School.SchoolCode("school_code_003_1"))
        val visitSchedule3 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 15)),
            School.SchoolCode("school_code_002"))
        val visitSchedules = VisitSchedules(listOf(visitSchedule1, visitSchedule2, visitSchedule3))

        val userCode = User.UserCode("user_code_001")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-01")

        // execution
        sut.save(userCode, targetYearAndMonth, visitSchedules)

        // verify
        val actual = visitSchedulesDao.findBySchoolCodesAndTargetDate(
            listOf("school_code_002", "school_code_003", "school_code_003_1", "school_code_004"),
            LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
        assertThat(actual).hasSize(3)
        assertThat(actual[0])
            .returns("school_id_003", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 1), VisitSchedulesEntity::visitDate)
            .returns("school_code_003", VisitSchedulesEntity::schoolCode)
        assertThat(actual[1])
            .returns("school_id_002", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 15), VisitSchedulesEntity::visitDate)
            .returns("school_code_002", VisitSchedulesEntity::schoolCode)
        assertThat(actual[2])
            .returns("school_id_003_1", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 31), VisitSchedulesEntity::visitDate)
            .returns("school_code_003_1", VisitSchedulesEntity::schoolCode)
    }

    @Test
    @DisplayName("delete することの確認")
    @FlywayTest(locationsForMigrate = ["db/fixtures_school_calculation_target"])
    fun testSave_DeleteRows() {
        // setup
        val visitSchedule1 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 1)),
            School.SchoolCode("school_code_003"))
        val visitSchedule2 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 31)),
            School.SchoolCode("school_code_003_1"))
        val visitSchedule3 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 15)),
            School.SchoolCode("school_code_002"))
        val setupVisitSchedules = VisitSchedules(listOf(visitSchedule1, visitSchedule2, visitSchedule3))

        val userCode = User.UserCode("user_code_001")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-01")
        sut.save(userCode, targetYearAndMonth, setupVisitSchedules) // 同一日で1度 insert

        val visitSchedules = VisitSchedules(listOf(visitSchedule1))

        // execution
        sut.save(userCode, targetYearAndMonth, visitSchedules)

        // verify
        val actual = visitSchedulesDao.findBySchoolCodesAndTargetDate(
            listOf("school_code_002", "school_code_003", "school_code_003_1", "school_code_004"),
            LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
        assertThat(actual).hasSize(1)
        assertThat(actual[0])
            .returns("school_id_003", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 1), VisitSchedulesEntity::visitDate)
            .returns("school_code_003", VisitSchedulesEntity::schoolCode)
    }

    @Test
    @DisplayName("処理しないことの確認")
    @FlywayTest(locationsForMigrate = ["db/fixtures_school_calculation_target"])
    fun testSave_NotProcess() {
        // setup
        val setupVisitSchedule1 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 1)),
            School.SchoolCode("school_code_003"))
        val setupVisitSchedule2 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 31)),
            School.SchoolCode("school_code_003_1"))
        val setupVisitSchedules = VisitSchedules(listOf(setupVisitSchedule1, setupVisitSchedule2))

        val setupUserCode = User.UserCode("user_code_001")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-01")
        sut.save(setupUserCode, targetYearAndMonth, setupVisitSchedules) // 同一日で1度 insert

        val visitSchedule = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 2)),
            School.SchoolCode("school_code_003"))
        val userCode = User.UserCode("user_code_003") // 関連する学校が存在しない

        // execution
        sut.save(userCode, targetYearAndMonth, VisitSchedules(listOf(visitSchedule)))

        // verify
        val actual = visitSchedulesDao.findBySchoolCodesAndTargetDate(
            listOf("school_code_002", "school_code_003", "school_code_003_1", "school_code_004"),
            LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
        assertThat(actual).hasSize(2)
        assertThat(actual[0])
            .returns("school_id_003", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 1), VisitSchedulesEntity::visitDate)
            .returns("school_code_003", VisitSchedulesEntity::schoolCode)
        assertThat(actual[1])
            .returns("school_id_003_1", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 31), VisitSchedulesEntity::visitDate)
            .returns("school_code_003_1", VisitSchedulesEntity::schoolCode)
    }

    @Test
    @DisplayName("別のユーザが処理しても影響を受けない")
    @FlywayTest(locationsForMigrate = ["db/fixtures_school_calculation_target"])
    fun testSave_OtherUser() {
        // setup
        val setupVisitSchedule1 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 1)),
            School.SchoolCode("school_code_003"))
        val setupVisitSchedule2 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 31)),
            School.SchoolCode("school_code_003_1"))
        val setupVisitSchedules = VisitSchedules(listOf(setupVisitSchedule1, setupVisitSchedule2))

        val setupUserCode = User.UserCode("user_code_001")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-01")
        sut.save(setupUserCode, targetYearAndMonth, setupVisitSchedules) // 同一日で1度 insert

        val visitSchedule = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 2)),
            School.SchoolCode("school_code_004"))
        val userCode = User.UserCode("user_code_002")

        // execution
        sut.save(userCode, targetYearAndMonth, VisitSchedules(listOf(visitSchedule)))

        // verify
        val actual = visitSchedulesDao.findBySchoolCodesAndTargetDate(
            listOf("school_code_002", "school_code_003", "school_code_003_1", "school_code_004"),
            LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
        assertThat(actual).hasSize(3)
        assertThat(actual[0])
            .returns("school_id_003", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 1), VisitSchedulesEntity::visitDate)
            .returns("school_code_003", VisitSchedulesEntity::schoolCode)
        assertThat(actual[1])
            .returns("school_id_004", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 2), VisitSchedulesEntity::visitDate)
            .returns("school_code_004", VisitSchedulesEntity::schoolCode)
        assertThat(actual[2])
            .returns("school_id_003_1", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 31), VisitSchedulesEntity::visitDate)
            .returns("school_code_003_1", VisitSchedulesEntity::schoolCode)
    }

    @Test
    @DisplayName("fk の確認")
    @FlywayTest(locationsForMigrate = ["db/fixtures_school_calculation_target"])
    fun testFk() {
        // setup
        val setupVisitSchedule1 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 1)),
            School.SchoolCode("school_code_003"))
        val setupVisitSchedule2 = VisitSchedules.VisitSchedule(
            Schedule.ScheduleDate(LocalDate.of(2020, 1, 31)),
            School.SchoolCode("school_code_003_1"))
        val setupVisitSchedules = VisitSchedules(listOf(setupVisitSchedule1, setupVisitSchedule2))

        val setupUserCode = User.UserCode("user_code_001")
        val targetYearAndMonth = Schedule.TargetYearAndMonth("2020-01")
        sut.save(setupUserCode, targetYearAndMonth, setupVisitSchedules) // 同一日で1度 insert

        // execution
        val schoolEntity = SchoolEntityFixtures.create().copy(schoolId = "school_id_003_1", versionNo = 14)
        schoolDao.delete(schoolEntity)

        // verify
        val actual = visitSchedulesDao.findBySchoolCodesAndTargetDate(
            listOf("school_code_002", "school_code_003", "school_code_003_1", "school_code_004"),
            LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31))
        assertThat(actual).hasSize(1)
        assertThat(actual[0])
            .returns("school_id_003", VisitSchedulesEntity::schoolId)
            .returns(LocalDate.of(2020, 1, 1), VisitSchedulesEntity::visitDate)
            .returns("school_code_003", VisitSchedulesEntity::schoolCode)
    }
}
