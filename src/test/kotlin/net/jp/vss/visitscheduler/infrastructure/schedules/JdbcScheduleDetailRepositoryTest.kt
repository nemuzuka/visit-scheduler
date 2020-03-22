package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.schedules.PrivateSchedule
import net.jp.vss.visitscheduler.domain.schedules.PrivateScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.ScheduleDetail
import net.jp.vss.visitscheduler.domain.schedules.ScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedules
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolEntityFixtures
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * JdbcScheduleDetailRepository のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class JdbcScheduleDetailRepositoryTest {

    @Autowired
    private lateinit var sut: JdbcScheduleDetailRepository

    @Test
    @DisplayName("buildScheduleDetail のテスト.ユーザに紐づく School 全ての schedule_school_connections が存在")
    fun testBuildScheduleDetail() {
        // setup
        val userCode = User.UserCode("USER-001")
        val schedule = ScheduleFixtures.create().copy(userCode = userCode)
        val privateSchedules = listOf(PrivateScheduleFixtures.create())

        val school1 = SchoolEntityFixtures.create()
            .copy(schoolId = "SCHOOL_ID_001", schoolCode = "SCHOOL_CODE_001", userCode = userCode.value)
        val school2 = SchoolEntityFixtures.create()
            .copy(schoolId = "SCHOOL_ID_002", schoolCode = "SCHOOL_CODE_002", userCode = userCode.value)
        val schools = listOf(school1, school2)
        val scheduleSchoolConnection1 = ScheduleSchoolConnectionEntity.buildForCreate("schedule_id_1",
            School.SchoolId(school1.schoolId), 0, false)
        val scheduleSchoolConnection2 = ScheduleSchoolConnectionEntity.buildForCreate("schedule_id_1",
            School.SchoolId(school2.schoolId), 1, true)
        val scheduleSchoolConnections = listOf(scheduleSchoolConnection1, scheduleSchoolConnection2)

        val school1Schedule1 = SchoolScheduleFixtures.create()
            .copy(schoolCode = School.SchoolCode(school1.schoolCode),
                targetDate = Schedule.ScheduleDate(LocalDate.parse("2020-01-01")))
        val school1Schedule2 = SchoolScheduleFixtures.create()
            .copy(schoolCode = School.SchoolCode(school1.schoolCode),
                targetDate = Schedule.ScheduleDate(LocalDate.parse("2020-01-03")))
        val school1Schedule3 = SchoolScheduleFixtures.create()
            .copy(schoolCode = School.SchoolCode(school1.schoolCode),
                targetDate = Schedule.ScheduleDate(LocalDate.parse("2020-01-24")))
        val schoolSchedules = listOf(school1Schedule1, school1Schedule2, school1Schedule3)
        val visitSchedules = VisitSchedules(
            listOf(
                VisitSchedules.VisitSchedule(Schedule.ScheduleDate(
                    LocalDate.of(2019, 1, 28)), School.SchoolCode("SCHOOL-01"))))

        // execution
        val actual = sut.buildScheduleDetail(schedule, privateSchedules,
            schools, scheduleSchoolConnections, schoolSchedules, visitSchedules)

        // verify
        val schoolWithSchedules = listOf(
            ScheduleDetail.SchoolWithSchedule(school1.toSchool(), false,
                listOf(school1Schedule1, school1Schedule2, school1Schedule3)),
                ScheduleDetail.SchoolWithSchedule(school2.toSchool(), true, null))
        val expected = ScheduleDetail(schedule, ScheduleDetail.PrivateSchedules(privateSchedules),
            ScheduleDetail.SchoolWithSchedules(schoolWithSchedules), visitSchedules)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @DisplayName("buildScheduleDetail のテスト.ユーザに紐づく School に schedule_school_connections が存在しないケースがある")
    fun testBuildScheduleDetail_NotScheduleSchoolConnection() {
        // setup
        val userCode = User.UserCode("USER-001")
        val schedule = ScheduleFixtures.create().copy(userCode = userCode)
        val privateSchedules = listOf(PrivateScheduleFixtures.create())

        val school1 = SchoolEntityFixtures.create()
            .copy(schoolId = "SCHOOL_ID_001", schoolCode = "SCHOOL_CODE_001", userCode = userCode.value)
        val school2 = SchoolEntityFixtures.create()
            .copy(schoolId = "SCHOOL_ID_002", schoolCode = "SCHOOL_CODE_002", userCode = userCode.value)
        val schools = listOf(school1, school2)
        val schedule2SchoolConnection1 = ScheduleSchoolConnectionEntity.buildForCreate("schedule_id_1",
            School.SchoolId(school2.schoolId), 1, true)
        val scheduleSchoolConnections = listOf(schedule2SchoolConnection1)

        val school2Schedule1 = SchoolScheduleFixtures.create()
            .copy(schoolCode = School.SchoolCode(school2.schoolCode),
                targetDate = Schedule.ScheduleDate(LocalDate.parse("2020-01-01")))
        val schoolSchedules = listOf(school2Schedule1)
        val visitSchedules =
            VisitSchedules(
                listOf(
                    VisitSchedules.VisitSchedule(Schedule.ScheduleDate(
                        LocalDate.of(2019, 1, 28)), School.SchoolCode("SCHOOL-01"))))

        // execution
        val actual = sut.buildScheduleDetail(schedule, privateSchedules, schools,
            scheduleSchoolConnections, schoolSchedules, visitSchedules)

        // verify
        val schoolWithSchedules = listOf(
            ScheduleDetail.SchoolWithSchedule(school2.toSchool(), true, listOf(school2Schedule1)),
            ScheduleDetail.SchoolWithSchedule(school1.toSchool(), false, null))
        val expected = ScheduleDetail(schedule, ScheduleDetail.PrivateSchedules(privateSchedules),
            ScheduleDetail.SchoolWithSchedules(schoolWithSchedules), visitSchedules)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_school_calculation_target"])
    fun testGetScheduleDetail() {
        // execution
        val actual = sut.getScheduleDetail(Schedule.ScheduleCode("schedule_code_001"), User.UserCode("user_code_001"))

        // verify
        assertThat(actual.schedule.scheduleCode).isEqualTo(Schedule.ScheduleCode("schedule_code_001"))
        assertThat(actual.privateSchedules.privateSchedules).hasSize(2)
        assertThat(actual.privateSchedules.privateSchedules[0])
            .returns(Schedule.ScheduleDate(LocalDate.parse("2019-01-03")), PrivateSchedule::targetDate)
        assertThat(actual.privateSchedules.privateSchedules[1])
            .returns(Schedule.ScheduleDate(LocalDate.parse("2019-01-15")), PrivateSchedule::targetDate)

        val schoolWithSchedules = actual.schoolWithSchedules
        assertThat(schoolWithSchedules.schoolWithSchedules).hasSize(3)

        // 1つめの school
        val schoolWithSchedule1 = schoolWithSchedules.schoolWithSchedules[0]
        assertThat(schoolWithSchedule1.school.schoolCode).isEqualTo(School.SchoolCode("school_code_002"))
        assertThat(schoolWithSchedule1.calculationTarget).isEqualTo(true)
        assertThat(schoolWithSchedule1.schedules).hasSize(2)
        assertThat(schoolWithSchedule1.schedules!![0].targetDate)
            .isEqualTo(Schedule.ScheduleDate(LocalDate.parse("2019-01-05")))
        assertThat(schoolWithSchedule1.schedules!![1].targetDate)
            .isEqualTo(Schedule.ScheduleDate(LocalDate.parse("2019-01-20")))

        // 2つめの school
        val schoolWithSchedule2 = schoolWithSchedules.schoolWithSchedules[1]
        assertThat(schoolWithSchedule2.school.schoolCode).isEqualTo(School.SchoolCode("school_code_003"))
        assertThat(schoolWithSchedule2.calculationTarget).isEqualTo(false)
        assertThat(schoolWithSchedule2.schedules).isNull()

        // 3つめの school
        val schoolWithSchedule3 = schoolWithSchedules.schoolWithSchedules[2]
        assertThat(schoolWithSchedule3.school.schoolCode).isEqualTo(School.SchoolCode("school_code_003_1"))
        assertThat(schoolWithSchedule3.calculationTarget).isEqualTo(false) // target 未設定なので強制的に false
        assertThat(schoolWithSchedule3.schedules).hasSize(1)
        assertThat(schoolWithSchedule3.schedules!![0].targetDate)
            .isEqualTo(Schedule.ScheduleDate(LocalDate.parse("2019-01-13")))
    }

    @Test
    @DisplayName("getScheduleDetail のテスト. schedule_code と user_code の組み合わせが不正")
    @FlywayTest(locationsForMigrate = ["db/fixtures_school_calculation_target"])
    fun testGetScheduleDetail_InvalidUserCode() {
        // execution
        val actual = catchThrowable { sut.getScheduleDetail(Schedule.ScheduleCode("schedule_code_001"),
            User.UserCode("user_code_002")) } // invalid user_code

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) { e ->
            assertThat(e.message).isEqualTo("Schedule(schedule_code_001) は存在しません") }
    }
}
