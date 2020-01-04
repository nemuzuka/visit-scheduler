package net.jp.vss.visitscheduler.usecase.users

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.domain.users.UserFixtures
import net.jp.vss.visitscheduler.domain.users.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * GetUserUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class GetUserUseCaseImplTest {

    @Mock
    private lateinit var userRepo: UserRepository

    @InjectMocks
    private lateinit var sut: GetUserUseCaseImpl

    @Test
    fun testGetTask() {
        // setup
        val user = UserFixtures.create()
        whenever(userRepo.getUserOrNull(any(), any())).thenReturn(user)
        val authorizedClientRegistrationId = "google"
        val principal = "principal_0001"

        // execution
        val actual = sut.getUser(authorizedClientRegistrationId = authorizedClientRegistrationId, principal = principal)

        // verify
        assertThat(actual).isEqualTo(UserUseCaseResult.of(user))

        verify(userRepo)
            .getUserOrNull(
                authorizedClientRegistrationId = User.AuthorizedClientRegistrationId(authorizedClientRegistrationId),
                principal = User.Principal(principal))
    }

    @Test
    fun testGetTask_NotExsits() {
        // setup
        whenever(userRepo.getUserOrNull(any(), any())).thenReturn(null)
        val authorizedClientRegistrationId = "google"
        val principal = "principal_0001"

        // execution
        val actual = sut.getUser(authorizedClientRegistrationId = authorizedClientRegistrationId, principal = principal)

        // verify
        assertThat(actual).isNull()
    }
}
