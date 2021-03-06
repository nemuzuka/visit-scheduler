package net.jp.vss.visitscheduler.infrastructure.schedules

import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolDao
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolEntityFixtures
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ThrowableAssert.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * ScheduleSchoolConnectionDao のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class ScheduleSchoolConnectionDaoTest {

    @Autowired
    private lateinit var sut: ScheduleSchoolConnectionDao

    @Autowired
    private lateinit var schoolDao: SchoolDao

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    @DisplayName("create のテスト")
    fun testCreate() {
        // setup
        val entity = ScheduleSchoolConnectionEntity(
            scheduleSchoolConnectionId = "schedule_school_connection_id_001",
            scheduleId = "schedule_id_001",
            schoolId = "school_id_002",
            connectionIndex = 1,
            calculationTarget = true)

        // execution
        val actual = sut.create(entity)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        assertThat(actual.entity).isEqualTo(entity)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    @DisplayName("create のテスト。FK 違反")
    fun testCreate_InvalidSchool() {
        // setup
        val entity = ScheduleSchoolConnectionEntity(
            scheduleSchoolConnectionId = "schedule_school_connection_id_001",
            scheduleId = "schedule_id_001",
            schoolId = "school_id_001", // 存在しない
            connectionIndex = 1,
            calculationTarget = true)

        // execution
        val actual = catchThrowable { sut.create(entity) }

        // verify
        assertThat(actual).isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["db/fixtures_schedule"])
    @DisplayName("fk のテスト")
    fun testFk() {
        // setup
        val entity = ScheduleSchoolConnectionEntity(
            scheduleSchoolConnectionId = "schedule_school_connection_id_001",
            scheduleId = "schedule_id_001",
            schoolId = "school_id_002",
            connectionIndex = 1,
            calculationTarget = true)
        sut.create(entity)

        // execution
        val schoolEntity = SchoolEntityFixtures.create().copy(schoolId = "school_id_002", versionNo = 2)
        schoolDao.delete(schoolEntity)

        // verify
        assertThat(sut.findByScheduleCode("schedule_code_001")).isEmpty()
    }
}
