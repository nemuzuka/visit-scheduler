package net.jp.vss.visitscheduler.infrastructure.schools

import java.util.UUID
import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.schools.SchoolFixtures
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * JdbcSchoolRepository のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class JdbcSchoolRepositoryTest {

    @Autowired
    private lateinit var sut: JdbcSchoolRepository

    @Test
    @FlywayTest
    fun testCreateSchool() {
        // setup
        val school = SchoolFixtures.create()

        // execution
        val actual = sut.createSchool(school)

        // verify
        assertThat(actual).isEqualTo(sut.getSchool(school.schoolCode, school.userCode))
    }

    @Test
    @FlywayTest
    fun testCreateSchool_NullProperties() {
        // setup
        val baseSchool = SchoolFixtures.create()
        val baseSchoolDetail = baseSchool.schoolDetail
        val school = baseSchool.copy(schoolDetail = baseSchoolDetail.copy(memo = null, attributes = null))

        // execution
        val actual = sut.createSchool(school)

        // verify
        assertThat(actual).isEqualTo(sut.getSchool(school.schoolCode, school.userCode))
    }

    @Test
    @FlywayTest
    fun testCreateSchool_AlreadyExistTaskCode_DE() {
        // setup
        val baseSchool = SchoolFixtures.create()
        sut.createSchool(baseSchool)
        val school = baseSchool.copy(School.SchoolId(UUID.randomUUID().toString())) // uuid は自動生成

        // execution
        val actual = catchThrowable { sut.createSchool(school) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(DuplicateException::class.java) { e ->
            assertThat(e.message).isEqualTo("School(${school.schoolCode.value}) は既に存在しています")
        }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    fun testGetSchool_NotNullProperties() {
        // setup
        val schoolCode = School.SchoolCode("school_code_003")
        val userCode = User.UserCode("user_001")

        // execution
        val actual = sut.getSchool(schoolCode, userCode)

        // verify
        val schoolId = School.SchoolId("school_id_003")
        val schoolDetail = School.SchoolDetail(name = "学校2",
                memo = "メモ2",
                attributes = Attributes("""{"hige":"hage"}"""))
        val resourceAttributes = ResourceAttributes(createUserCode = "create_user_001",
            createAt = 1646732800001,
            lastUpdateUserCode = "last_update_user_001",
            lastUpdateAt = 1746732800001,
            version = 1L)
        val expected = School(schoolId = schoolId, schoolCode = schoolCode, userCode = userCode,
            schoolDetail = schoolDetail, resourceAttributes = resourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    fun testGetSchool_NullProperties() {
        // setup
        val schoolCode = School.SchoolCode("school_code_002")
        val userCode = User.UserCode("user_001")

        // execution
        val actual = sut.getSchool(schoolCode, userCode)

        // verify
        val schoolId = School.SchoolId("school_id_002")
        val schoolDetail = School.SchoolDetail(name = "学校1",
            memo = null,
            attributes = null)
        val resourceAttributes = ResourceAttributes(createUserCode = "create_user_002",
            createAt = 1646732800002,
            lastUpdateUserCode = "last_update_user_002",
            lastUpdateAt = 1746732800002,
            version = 2L)
        val expected = School(schoolId = schoolId, schoolCode = schoolCode, userCode = userCode,
            schoolDetail = schoolDetail, resourceAttributes = resourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    fun testGetSchool_NotFoundTask_NFE() {
        // setup
        val schoolCode = School.SchoolCode("school_code_002")
        val userCode = User.UserCode("user_002")

        // execution
        val actual = catchThrowable { sut.getSchool(schoolCode, userCode) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) { e ->
            assertThat(e.message).isEqualTo("School(${schoolCode.value}) は存在しません")
        }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    fun testLockSchool_NotNullProperties() {
        // setup
        val schoolCode = School.SchoolCode("school_code_002")
        val userCode = User.UserCode("user_001")

        // execution
        val actual = sut.lockSchool(schoolCode, userCode)

        // verify
        assertThat(actual).isEqualTo(sut.getSchool(schoolCode, userCode))
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    fun testLockSchool_NotFoundTask_NFE() {
        // setup
        val schoolCode = School.SchoolCode("school_code_002")
        val userCode = User.UserCode("user_002")

        // execution
        val actual = catchThrowable { sut.lockSchool(schoolCode, userCode) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) { e ->
            assertThat(e.message).isEqualTo("School(${schoolCode.value}) は存在しません")
        }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    fun testAllSchools() {
        // setup
        val userCode = User.UserCode("user_001")

        // execution
        val actual = sut.allSchools(userCode)

        // verify
        assertThat(actual).hasSize(2)
        assertThat(actual[0].schoolId).isEqualTo(School.SchoolId("school_id_003"))
        assertThat(actual[1].schoolId).isEqualTo(School.SchoolId("school_id_002"))
    }

    @Test
    @FlywayTest
    fun testAllSchools_Empty() {
        // setup
        val userCode = User.UserCode("user_001")

        // execution
        val actual = sut.allSchools(userCode)

        // verify
        assertThat(actual).isEmpty()
    }

    @Test
    @FlywayTest
    fun testUpdateSchool() {
        // setup
        val baseSchool = SchoolFixtures.create()
        sut.createSchool(baseSchool)

        val schoolDetail = baseSchool.schoolDetail.copy(
            name = "update name",
            attributes = Attributes("""{"update_sum":1}"""))
        val resourceAttributes = baseSchool.resourceAttributes.copy(
            createUserCode = "UPDATE_USER_12345",
            createAt = 1234567891L,
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L)
        val school = baseSchool.copy(schoolDetail = schoolDetail,
            resourceAttributes = resourceAttributes)

        // execution
        val actual = sut.updateSchool(school)

        // verify
        val rdbmsExpectedResourceAttributes = baseSchool.resourceAttributes.copy(
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L,
            version = baseSchool.resourceAttributes.version + 1)
        val expected = school.copy(resourceAttributes = rdbmsExpectedResourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest
    fun testUpdateSchool_NullProperty() {
        // setup
        val baseSchool = SchoolFixtures.create()
        sut.createSchool(baseSchool)

        val schoolDetail = baseSchool.schoolDetail.copy(
            name = "update name",
            memo = null,
            attributes = null)
        val resourceAttributes = baseSchool.resourceAttributes.copy(
            createUserCode = "UPDATE_USER_12345",
            createAt = 1234567891L,
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L)
        val school = baseSchool.copy(schoolDetail = schoolDetail,
            resourceAttributes = resourceAttributes)

        // execution
        val actual = sut.updateSchool(school)

        // verify
        val expectedResourceAttributes = baseSchool.resourceAttributes.copy(
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L,
            version = baseSchool.resourceAttributes.version + 1)
        val expected = school.copy(resourceAttributes = expectedResourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest
    fun testUpdateSchool_NotFoundUpdateTarget() {
        // setup
        val school = SchoolFixtures.create()

        // execution
        val actual = catchThrowable { sut.updateSchool(school) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) { e ->
            assertThat(e.message).isEqualTo("School(${school.schoolCode.value}) は存在しません")
        }
    }
}
