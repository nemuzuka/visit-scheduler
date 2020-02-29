package net.jp.vss.visitscheduler.infrastructure.schedules

import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.ResourceAttributesFixtures
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * JdbcScheduleRepository のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class JdbcScheduleRepositoryTest {

    @Autowired
    private lateinit var sut: JdbcScheduleRepository

    @Autowired
    private lateinit var scheduleSchoolConnectionDao: ScheduleSchoolConnectionDao

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    fun testCreate() {
        // setup
        val schedule = Schedule(scheduleCode = Schedule.ScheduleCode("SCHEDULE_0001"),
            userCode = User.UserCode("user_code_001"),
            targetYearAndMonth = Schedule.TargetYearAndMonth("2019-02"),
            scheduleDetail = Schedule.ScheduleDetail(attributes = null),
            resourceAttributes = ResourceAttributesFixtures.create())

        val schoolCodeAndCalculationTarget1 = Schedule.SchoolCodeAndCalculationTarget(
            schoolCode = School.SchoolCode("school_code_003"),
            calculationTarget = true)
        val schoolCodeAndCalculationTarget2 = Schedule.SchoolCodeAndCalculationTarget(
            schoolCode = School.SchoolCode("school_code_002"),
            calculationTarget = false)

        // execution
        val actual = sut.create(schedule,
            Schedule.SchoolCodeAndCalculationTargets(
                listOf(schoolCodeAndCalculationTarget1, schoolCodeAndCalculationTarget2)))

        // verify
        assertThat(actual).isEqualTo(sut.getSchedule(schedule.scheduleCode, schedule.userCode))

        val scheduleSchoolConnections = scheduleSchoolConnectionDao.findByScheduleCode(schedule.scheduleCode.value)
        assertThat(scheduleSchoolConnections).hasSize(2)
        assertThat(scheduleSchoolConnections[0])
            .returns("school_id_003", ScheduleSchoolConnectionEntity::schoolId)
            .returns(0, ScheduleSchoolConnectionEntity::connectionIndex)
            .returns(true, ScheduleSchoolConnectionEntity::calculationTarget)
        assertThat(scheduleSchoolConnections[1])
            .returns("school_id_002", ScheduleSchoolConnectionEntity::schoolId)
            .returns(1, ScheduleSchoolConnectionEntity::connectionIndex)
            .returns(false, ScheduleSchoolConnectionEntity::calculationTarget)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    @DisplayName("存在しない user_code")
    fun testCreate_NotFoundUser() {
        // setup
        val schedule = Schedule(scheduleCode = Schedule.ScheduleCode("SCHEDULE_0001"),
            userCode = User.UserCode("user_code_00X"), // 存在しないユーザ
            targetYearAndMonth = Schedule.TargetYearAndMonth("2019-02"),
            scheduleDetail = Schedule.ScheduleDetail(attributes = null),
            resourceAttributes = ResourceAttributesFixtures.create())

        val schoolCodeAndCalculationTarget = Schedule.SchoolCodeAndCalculationTarget(
            schoolCode = School.SchoolCode("school_code_003"),
            calculationTarget = true)

        // execution
        val actual = catchThrowable {
            sut.create(schedule, Schedule.SchoolCodeAndCalculationTargets(listOf(schoolCodeAndCalculationTarget)))
        }

        // verify
        assertThat(actual).isInstanceOfSatisfying(IllegalStateException::class.java) {
            assertThat(it.message).isEqualTo("User(user_code_00X) は存在しません") }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    @DisplayName("user_code と target_year_and_month が既に存在する")
    fun testCreate_AlreadyUserCodeAndTargetYearAndMonth() {
        // setup
        val schedule = Schedule(scheduleCode = Schedule.ScheduleCode("SCHEDULE_0001"),
            userCode = User.UserCode("user_code_001"),
            targetYearAndMonth = Schedule.TargetYearAndMonth("2019-01"), // 既に作成済みの対象年月
            scheduleDetail = Schedule.ScheduleDetail(attributes = null),
            resourceAttributes = ResourceAttributesFixtures.create())

        val schoolCodeAndCalculationTarget = Schedule.SchoolCodeAndCalculationTarget(
            schoolCode = School.SchoolCode("school_code_003"),
            calculationTarget = true)

        // execution
        val actual = catchThrowable {
            sut.create(schedule, Schedule.SchoolCodeAndCalculationTargets(listOf(schoolCodeAndCalculationTarget)))
        }

        // verify
        assertThat(actual).isInstanceOfSatisfying(DuplicateException::class.java) {
            assertThat(it.message).isEqualTo("Schedule(SCHEDULE_0001) は既に存在しています") }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    @DisplayName("ユーザが管理する School ではない")
    fun testCreate_NotFoundSchool() {
        // setup
        val schedule = Schedule(scheduleCode = Schedule.ScheduleCode("SCHEDULE_0001"),
            userCode = User.UserCode("user_code_001"),
            targetYearAndMonth = Schedule.TargetYearAndMonth("2019-02"),
            scheduleDetail = Schedule.ScheduleDetail(attributes = null),
            resourceAttributes = ResourceAttributesFixtures.create())

        val schoolCodeAndCalculationTarget = Schedule.SchoolCodeAndCalculationTarget(
            schoolCode = School.SchoolCode("school_code_004"), // ユーザが管理する School ではない
            calculationTarget = true)

        // execution
        val actual = catchThrowable {
            sut.create(schedule, Schedule.SchoolCodeAndCalculationTargets(listOf(schoolCodeAndCalculationTarget)))
        }

        // verify
        assertThat(actual).isInstanceOfSatisfying(IllegalStateException::class.java) {
            assertThat(it.message).isEqualTo("School(school_code_004) は存在しません") }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    @DisplayName("getSchedule のテスト")
    fun testGetSchedule() {
        // execution
        val actual = sut.getSchedule(Schedule.ScheduleCode("schedule_code_001"), User.UserCode("user_code_001"))

        // verify
        assertThat(actual)
            .returns(Schedule.ScheduleCode("schedule_code_001"), Schedule::scheduleCode)
            .returns(User.UserCode("user_code_001"), Schedule::userCode)
            .returns(Schedule.TargetYearAndMonth("2019-01"), Schedule::targetYearAndMonth)
            .returns(Schedule.ScheduleDetail(attributes = Attributes.of("{\"hige\":\"hage\"}")),
                Schedule::scheduleDetail)
            .returns(ResourceAttributes("create_user_002", 1646732800002, "last_update_user_002", 1746732800002, 2),
                Schedule::resourceAttributes)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    @DisplayName("getSchedule のテスト. schedule_code が存在しない")
    fun testGetSchedule_NotFound() {
        // execution
        val actual = catchThrowable { sut.getSchedule(Schedule.ScheduleCode("schedule_code_001"),
            User.UserCode("user_code_002")) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) {
            assertThat(it.message).isEqualTo("Schedule(schedule_code_001) は存在しません") }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    @DisplayName("lockSchedule のテスト")
    fun testLockSchedule() {
        // execution
        val actual = sut.lockSchedule(Schedule.ScheduleCode("schedule_code_001"), User.UserCode("user_code_001"))

        // verify
        assertThat(actual)
            .returns(Schedule.ScheduleCode("schedule_code_001"), Schedule::scheduleCode)
            .returns(User.UserCode("user_code_001"), Schedule::userCode)
            .returns(Schedule.TargetYearAndMonth("2019-01"), Schedule::targetYearAndMonth)
            .returns(Schedule.ScheduleDetail(attributes = Attributes.of("{\"hige\":\"hage\"}")),
                Schedule::scheduleDetail)
            .returns(ResourceAttributes("create_user_002", 1646732800002, "last_update_user_002", 1746732800002, 2),
                Schedule::resourceAttributes)
    }
}
