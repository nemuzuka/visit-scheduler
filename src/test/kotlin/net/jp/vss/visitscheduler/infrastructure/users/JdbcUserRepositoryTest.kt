package net.jp.vss.visitscheduler.infrastructure.users

import java.util.UUID
import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.domain.users.UserFixtures
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * JdbcUserRepository のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class JdbcUserRepositoryTest {

    @Autowired
    private lateinit var sut: JdbcUserRepository

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    fun testGetUserOrNull_Exists() {
        // setup
        val authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("google")
        val principal = User.Principal("principal_002")

        // execution
        val actual = sut.getUserOrNull(authorizedClientRegistrationId, principal)

        // verify
        val userId = User.UserId("user_id_002")
        val userCode = User.UserCode("user_code_002")
        val userDetail = User.UserDetail("名前002")
        val expected = User(userId = userId, userCode = userCode, userDetail = userDetail)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    fun testGetUserOrNull_NotExists() {
        // setup
        val authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("facebook")
        val principal = User.Principal("principal_002")

        // execution
        val actual = sut.getUserOrNull(authorizedClientRegistrationId, principal)

        // verify
        assertThat(actual).isNull()
    }

    @Test
    @FlywayTest
    fun testCreateUser() {
        // setup
        val authenticatedPrincipalId = User.AuthenticatedPrincipalId(UUID.randomUUID().toString())
        val authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("facebook")
        val principal = User.Principal("principal_001")
        val user = UserFixtures.create()

        // execution
        val actual = sut.createUser(user = user,
            authorizedClientRegistrationId = authorizedClientRegistrationId,
            authenticatedPrincipalId = authenticatedPrincipalId, principal = principal)

        // verify
        assertThat(actual).isNotNull.isEqualTo(user)
        assertThat(actual).isEqualTo(sut.getUserOrNull(user.userCode))
        assertThat(actual).isEqualTo(sut.getUserOrNull(authorizedClientRegistrationId, principal))
    }

    @Test
    @FlywayTest
    fun testCreateUser_AlreadyExistUserCode_DE() {
        // setup
        val authenticatedPrincipalId = User.AuthenticatedPrincipalId(UUID.randomUUID().toString())
        val authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("facebook")
        val principal = User.Principal("principal_001")
        val user = UserFixtures.create()
        sut.createUser(user = user, authorizedClientRegistrationId = authorizedClientRegistrationId,
            authenticatedPrincipalId = authenticatedPrincipalId, principal = principal)

        // execution
        val actual = Assertions.catchThrowable {
            sut.createUser(user = user.copy(userId = User.UserId(UUID.randomUUID().toString())),
                authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("google"),
                authenticatedPrincipalId = User.AuthenticatedPrincipalId(UUID.randomUUID().toString()),
                principal = User.Principal("principal_002"))
        }

        // verify
        assertThat(actual).isInstanceOfSatisfying(DuplicateException::class.java) { e ->
            assertThat(e.message).isEqualTo("User(${user.userCode.value}) は既に存在しています")
        }
    }

    @Test
    @FlywayTest
    fun testUpdateUser() {
        // setup
        val authenticatedPrincipalId = User.AuthenticatedPrincipalId(UUID.randomUUID().toString())
        val authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("facebook")
        val principal = User.Principal("principal_001")
        val baseUser = UserFixtures.create()
        sut.createUser(user = baseUser, authenticatedPrincipalId = authenticatedPrincipalId,
            authorizedClientRegistrationId = authorizedClientRegistrationId, principal = principal)

        val userDetail = baseUser.userDetail.copy(
            userName = "updated userName")
        val user = baseUser.copy(userDetail = userDetail)

        // execution
        val actual = sut.updateUser(user)

        // verify
        val expected = user.copy(userDetail = userDetail)
        assertThat(actual).isEqualTo(expected)
        assertThat(actual).isEqualTo(sut.getUserOrNull(user.userCode))
    }

    @Test
    @FlywayTest
    fun testUpdateUser_NotFoundUpdateTarget() {
        // setup
        val user = UserFixtures.create()

        // execution
        val actual = catchThrowable { sut.updateUser(user) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) { e ->
            assertThat(e.message).isEqualTo("User(${user.userCode.value}) は存在しません")
        }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    fun testLockUser_Exists() {
        // setup
        val userCode = User.UserCode("user_code_002")

        // execution
        val actual = sut.lockUser(userCode)

        // verify
        val userId = User.UserId("user_id_002")
        val userDetail = User.UserDetail("名前002")
        val expected = User(userId = userId, userCode = userCode, userDetail = userDetail)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    fun testLockUser_NotFoundUser_NFE() {
        // setup
        val userCode = User.UserCode("absent_user_code")

        // execution
        val actual = catchThrowable { sut.lockUser(userCode) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) { e ->
            assertThat(e.message).isEqualTo("User(${userCode.value}) は存在しません")
        }
    }
}
