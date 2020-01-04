package net.jp.vss.visitscheduler.infrastructure.users

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
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * UserDao のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class UserDaoTest {

    @Autowired
    private lateinit var sut: UserDao

    @Autowired
    private lateinit var authenticatedPrincipalDao: AuthenticatedPrincipalDao

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    @DisplayName("authorized_client_registration_id と principal による取得")
    fun testFindByAuthorizedClientRegistrationIdAndPrincipal() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principal = "principal_002"

        // execution
        val actual = sut.findByAuthorizedClientRegistrationIdAndPrincipal(authorizedClientRegistrationId, principal)

        // verify
        val expected = UserEntity(userId = "user_id_002", userCode = "user_code_002",
            authenticatedPrincipalId = "authenticated_principal_id_002", userName = "名前002")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    @DisplayName("authorized_client_registration_id と principal で取得(存在しない)")
    fun testFindByAuthorizedClientRegistrationIdAndPrincipal_NotFount() {
        // setup
        val authorizedClientRegistrationId = "facebook"
        val principal = "principal_002"

        // execution
        val actual = sut.findByAuthorizedClientRegistrationIdAndPrincipal(authorizedClientRegistrationId, principal)

        // verify
        assertThat(actual).isNull()
    }

    @Test
    @FlywayTest
    @DisplayName("create のテスト")
    fun testCreate() {
        // setup
        val authenticatedPrincipal = AuthenticatedPrincipalEntity(authenticatedPrincipalId = "TEST01",
            principal = "HOGE-PRINCIPAL01", authorizedClientRegistrationId = "google")
        authenticatedPrincipalDao.create(authenticatedPrincipal) // 外部参照の為
        val entity = UserEntity(userId = UUID.randomUUID().toString(), userCode = "USER_CODE_001",
            authenticatedPrincipalId = authenticatedPrincipal.authenticatedPrincipalId, userName = "ユーザ名_001")
        // execution
        val actual = sut.create(entity)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        assertThat(actual.entity).isEqualTo(entity)
    }

    @Test
    @FlywayTest
    @DisplayName("一意制約のテスト")
    fun testCreate_Duplicate() {
        // setup
        val authenticatedPrincipal = AuthenticatedPrincipalEntity(authenticatedPrincipalId = "TEST01",
            principal = "HOGE-PRINCIPAL01", authorizedClientRegistrationId = "google")
        authenticatedPrincipalDao.create(authenticatedPrincipal) // 外部参照の為
        val entity = UserEntity(userId = UUID.randomUUID().toString(), userCode = "USER_CODE_001",
            authenticatedPrincipalId = authenticatedPrincipal.authenticatedPrincipalId, userName = "ユーザ名_001")
        sut.create(entity)

        // execution
        val actual = catchThrowable {
            sut.create(entity.copy(userId = UUID.randomUUID().toString())) // userCode の一意制約違反
        }

        // verify
        assertThat(actual).isInstanceOf(DuplicateKeyException::class.java)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    @DisplayName("user_code による取得")
    fun testFindByUserCode() {
        // setup
        val userCode = "user_code_002"

        // execution
        val actual = sut.findByUserCode(userCode, SelectOptions.get())

        // verify
        val expected = UserEntity(userId = "user_id_002", userCode = "user_code_002",
            authenticatedPrincipalId = "authenticated_principal_id_002", userName = "名前002")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    @DisplayName("user_code による取得(Lock)")
    fun testFindByUserCode_ForUpdate() {
        // setup
        val userCode = "user_code_001"

        // execution
        val actual = sut.findByUserCode(userCode, SelectOptions.get().forUpdate())

        // verify
        val expected = UserEntity(userId = "user_id_001", userCode = "user_code_001",
            authenticatedPrincipalId = "authenticated_principal_id_001", userName = "名前001")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest
    @DisplayName("user_code による取得(存在しない)")
    fun testFindByUserCode_NotFount() {
        // setup
        val userCode = "absent_userCode"

        // execution
        val actual = sut.findByUserCode(userCode, SelectOptions.get())

        // verify
        assertThat(actual).isNull()
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    @DisplayName("updateUserName のテスト")
    fun testUpdateUserName() {
        // setup
        val userId = "user_id_002"
        val userName = "更新後-名前002"

        // execution
        val actual = sut.updateUserName(userId, userName)

        // verify
        assertThat(actual).isEqualTo(1)

        val expected = UserEntity(userId = "user_id_002", userCode = "user_code_002",
            authenticatedPrincipalId = "authenticated_principal_id_002", userName = userName)
        assertThat(sut.findByUserCode("user_code_002", SelectOptions.get())).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    @DisplayName("updateUserName のテスト(更新対象が存在しない)")
    fun testUpdateUserName_NotUpdate() {
        // setup
        val userId = "user_id_abc"
        val userName = "更新後-名前"

        // execution
        val actual = sut.updateUserName(userId, userName)

        // verify
        assertThat(actual).isEqualTo(0)
    }
}
