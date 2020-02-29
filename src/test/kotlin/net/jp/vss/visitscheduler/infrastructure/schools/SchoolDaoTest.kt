package net.jp.vss.visitscheduler.infrastructure.schools

import java.util.UUID
import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * SchoolDao のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class SchoolDaoTest {

    @Autowired
    private lateinit var sut: SchoolDao

    @Test
    @FlywayTest
    @DisplayName("create のテスト")
    fun testCreate() {
        // setup
        val school = SchoolEntityFixtures.create()

        // execution
        val actual = sut.create(school)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        assertThat(actual.entity).isEqualTo(school)
    }

    @Test
    @FlywayTest
    @DisplayName("create のテスト(一意制約)")
    fun testCreate_Duplicate() {
        // setup
        val school = SchoolEntityFixtures.create()
        sut.create(school)

        // execution
        val actual = catchThrowable {
            sut.create(school.copy(schoolId = UUID.randomUUID().toString())) // school_code の一意制約違反
        }

        // verify
        assertThat(actual).isInstanceOf(DuplicateKeyException::class.java)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    @DisplayName("findBySchoolCodeAndUserCode のテスト(全てのプロパティが存在する)")
    fun testFindBySchoolCodeAndUserCode_AllProperties() {
        // setup
        val schoolCode = "school_code_003"
        val userCode = "user_001"

        // execution
        val actual = sut.findBySchoolCodeAndUserCode(schoolCode, userCode, SelectOptions.get())

        // verify
        val expected = SchoolEntity(schoolId = "school_id_003",
            schoolCode = "school_code_003",
            userCode = "user_001",
            name = "学校2",
            memo = "メモ2",
            attributes = """{"hige":"hage"}""",
            createUserCode = "create_user_001",
            createAt = 1646732800001,
            lastUpdateUserCode = "last_update_user_001",
            lastUpdateAt = 1746732800001,
            versionNo = 1L)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    @DisplayName("findBySchoolCodeAndUserCode のテスト(NotNullプロパティのみ)")
    fun testFindBySchoolCodeAndUserCode_NullProperties() {
        // setup
        val schoolCode = "school_code_002"
        val userCode = "user_001"

        // execution
        val actual = sut.findBySchoolCodeAndUserCode(schoolCode, userCode, SelectOptions.get())

        // verify
        val expected = SchoolEntity(schoolId = "school_id_002",
            schoolCode = "school_code_002",
            userCode = "user_001",
            name = "学校1",
            memo = null,
            attributes = null,
            createUserCode = "create_user_002",
            createAt = 1646732800002,
            lastUpdateUserCode = "last_update_user_002",
            lastUpdateAt = 1746732800002,
            versionNo = 2L)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest
    @DisplayName("該当レコードが存在しない")
    fun testFindBySchoolCodeAndUserCode_NotFound() {
        // setup
        val schoolCode = "absent_school_code"
        val userCode = "user_001"

        // execution
        val actual = sut.findBySchoolCodeAndUserCode(schoolCode, userCode, SelectOptions.get())

        // verify
        assertThat(actual).isNull()
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    @DisplayName("findAll のテスト")
    fun testFindAll() {
        // setup
        val userCode = "user_001"

        // execution
        val actual = sut.findAll(userCode)

        // verify
        assertThat(actual).hasSize(2)
        assertThat(actual[0].schoolId).isEqualTo("school_id_003")
        assertThat(actual[1].schoolId).isEqualTo("school_id_002")
    }

    @Test
    @FlywayTest
    @DisplayName("findAll のテスト(未登録)")
    fun testFindAll_Empty() {
        // setup
        val userCode = "user_001"

        // execution
        val actual = sut.findAll(userCode)

        // verify
        assertThat(actual).isEmpty()
    }

    @Test
    @FlywayTest
    @DisplayName("update のテスト全てのプロパティ指定")
    fun testUpdate_AllProperties() {
        // setup
        val baseSchool = SchoolEntityFixtures.create()
        sut.create(baseSchool)

        val school = baseSchool.copy(
            schoolCode = "NOT_UPDATE", // 対象外
            userCode = "NOT_UPDATE_USER_CODE", // 対象外
            name = "update name",
            memo = "update memo",
            attributes = """{"update_sum":1}""",
            createUserCode = "NOT_UPDATE", // 対象外
            createAt = 3234567891L, // 対象外
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L)

        // execution
        val actual = sut.update(school)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        // Doma2 の update の戻り値はパラメータ +  verison をインクリメントしたもの
        // プロパティ指定の update だとちょっと使えないな...
        assertThat(actual.entity).isEqualTo(school.copy(versionNo = school.versionNo + 1))

        // 取得した時は変わっていること
        val expected = baseSchool.copy(
            name = "update name",
            memo = "update memo",
            attributes = """{"update_sum":1}""",
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L,
            versionNo = baseSchool.versionNo + 1) // インクリメントする
        assertThat(sut.findBySchoolCodeAndUserCode(baseSchool.schoolCode, baseSchool.userCode, SelectOptions.get()))
            .isEqualTo(expected)
    }

    @Test
    @FlywayTest
    @DisplayName("update のテストNotNullプロパティのみ指定")
    fun testUpdate_NullProperties() {
        // setup
        val baseSchool = SchoolEntityFixtures.create()
        sut.create(baseSchool)

        val school = baseSchool.copy(
            schoolCode = "NOT_UPDATE", // 対象外
            userCode = "NOT_UPDATE_USER_CODE", // 対象外
            name = "update name",
            memo = null,
            attributes = null,
            createUserCode = "NOT_UPDATE", // 対象外
            createAt = 3234567891L, // 対象外
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L)

        // execution
        val actual = sut.update(school)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        assertThat(actual.entity).isEqualTo(school.copy(versionNo = school.versionNo + 1))

        // 取得した時は変わっていること
        val expected = baseSchool.copy(
            name = "update name",
            memo = null,
            attributes = null,
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L,
            versionNo = baseSchool.versionNo + 1) // インクリメントする
        assertThat(sut.findBySchoolCodeAndUserCode(baseSchool.schoolCode, baseSchool.userCode, SelectOptions.get()))
            .isEqualTo(expected)
    }

    @Test
    @FlywayTest
    @DisplayName("update のテスト version 不正")
    fun testUpdate_InvalidVersion() {
        // setup
        val baseSchool = SchoolEntityFixtures.create()
        sut.create(baseSchool)
        val school = baseSchool.copy(versionNo = baseSchool.versionNo + 1)

        // execution
        val actual = catchThrowable { sut.update(school) }

        // verify
        assertThat(actual).isInstanceOf(OptimisticLockingFailureException::class.java)
    }

    @Test
    @FlywayTest
    @DisplayName("update のテスト 更新対象が存在しない")
    fun testUpdate_NotFoundUpdateTarget() {
        // setup
        val school = SchoolEntityFixtures.create()

        // execution
        val actual = catchThrowable { sut.update(school) }

        // verify
        assertThat(actual).isInstanceOf(OptimisticLockingFailureException::class.java)
    }

    @Test
    @FlywayTest
    @DisplayName("delete のテスト")
    fun testDelete() {
        // setup
        val baseSchool = SchoolEntityFixtures.create()
        val school = sut.create(baseSchool)

        // execution
        val actual = sut.delete(school.entity)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        assertThat(sut.findBySchoolCodeAndUserCode(baseSchool.schoolCode, baseSchool.userCode, SelectOptions.get()))
            .isNull()
    }

    @Test
    @FlywayTest
    @DisplayName("delete のテスト version 不正")
    fun testDelete_InvalidVersion() {
        // setup
        val baseSchool = SchoolEntityFixtures.create()
        sut.create(baseSchool)
        val school = baseSchool.copy(versionNo = baseSchool.versionNo + 1)

        // execution
        val actual = catchThrowable { sut.delete(school) }

        // verify
        assertThat(actual).isInstanceOf(OptimisticLockingFailureException::class.java)
    }

    @Test
    @FlywayTest
    @DisplayName("delete のテスト 更新対象が存在しない")
    fun testDelete_NotFoundUpdateTarget() {
        // setup
        val school = SchoolEntityFixtures.create()

        // execution
        val actual = catchThrowable { sut.update(school) }

        // verify
        assertThat(actual).isInstanceOf(OptimisticLockingFailureException::class.java)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    @DisplayName("findBySchoolCodeAndUserCode のテスト(存在する)")
    fun testFindBySchoolCodeAndUserCode_Exists() {
        // setup
        val schoolCodes = listOf("school_code_002", "school_code_003")
        val userCode = "user_001"

        // execution
        val actual = sut.findBySchoolCodesAndUserCode(schoolCodes, userCode)

        // verify
        assertThat(actual).hasSize(2)
        assertThat(actual[0].schoolId).isEqualTo("school_id_003")
        assertThat(actual[1].schoolId).isEqualTo("school_id_002")
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_school"])
    @DisplayName("findBySchoolCodeAndUserCode のテスト(存在しない)")
    fun testFindBySchoolCodeAndUserCode_NotExists() {
        // setup
        val schoolCodes = listOf("school_code_999", "school_code_003")
        val userCode = "user_002" // user_code が異なる

        // execution
        val actual = sut.findBySchoolCodesAndUserCode(schoolCodes, userCode)

        // verify
        assertThat(actual).isEmpty()
    }
}
