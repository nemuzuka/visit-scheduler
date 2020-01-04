package net.jp.vss.visitscheduler.infrastructure.users

import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * AuthenticatedPrincipalDao のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class AuthenticatedPrincipalDaoTest {

    @Autowired
    private lateinit var sut: AuthenticatedPrincipalDao

    @Test
    @FlywayTest
    @DisplayName("create のテスト")
    fun testCreate() {
        // setup
        val entity = AuthenticatedPrincipalEntity(authenticatedPrincipalId = "TEST01",
            principal = "HOGE-PRINCIPAL01", authorizedClientRegistrationId = "google")

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
        val entity = AuthenticatedPrincipalEntity(authenticatedPrincipalId = "TEST02",
            principal = "HOGE-PRINCIPAL02", authorizedClientRegistrationId = "google")
        sut.create(entity)

        // execution
        val actual = catchThrowable { sut.create(entity) }

        // verify
        assertThat(actual).isInstanceOf(DuplicateKeyException::class.java)
    }

    @Test
    @FlywayTest
    @DisplayName("ID 指定で取得できることのテスト")
    fun testFindByAuthenticatedPrincipalId() {
        // setup
        val entity = AuthenticatedPrincipalEntity(authenticatedPrincipalId = "TEST03",
            principal = "HOGE-PRINCIPAL03", authorizedClientRegistrationId = "google")
        sut.create(entity)

        // execution
        val actual = sut.findByAuthenticatedPrincipalId(entity.authenticatedPrincipalId)

        // verify
        assertThat(actual).isEqualTo(entity)
    }

    @Test
    @FlywayTest
    @DisplayName("存在しない識別子を指定した時のテスト")
    fun testFindByAuthenticatedPrincipalId_NotFound() {
        // execution
        val actual = sut.findByAuthenticatedPrincipalId("absent_id")

        // verify
        assertThat(actual).isNull()
    }
}
