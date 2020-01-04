package net.jp.vss.visitscheduler.usecase.users

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
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
 * CreateUserUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class CreateUserUseCaseImplTest {

    @Mock
    private lateinit var userRepo: UserRepository

    @InjectMocks
    private lateinit var sut: CreateUserUseCaseImpl

    @Test
    fun testCreateUser() {
        // setup
        val createdUser = UserFixtures.create()
        whenever(userRepo.createUser(any(), any(), any(), any())).thenReturn(createdUser)

        val input = CreateUserUseCaseParameterFixtures.create()

        // execution
        val actual = sut.createUser(input)

        // verify
        assertThat(actual).isEqualTo(UserUseCaseResult.of(createdUser))

        val userCaptor = argumentCaptor<User>()
        val authenticatedPrincipalIdCaptor = argumentCaptor<User.AuthenticatedPrincipalId>()
        val authorizedClientRegistrationIdCaptor = argumentCaptor<User.AuthorizedClientRegistrationId>()
        val principalCaptor = argumentCaptor<User.Principal>()
        verify(userRepo).createUser(userCaptor.capture(), authenticatedPrincipalIdCaptor.capture(),
            authorizedClientRegistrationIdCaptor.capture(), principalCaptor.capture())

        val capturedUser = userCaptor.firstValue
        val expectedUser = User(userId = capturedUser.userId,
            userCode = User.UserCode(input.userCode),
            userDetail = User.UserDetail(userName = input.userName))
        assertThat(capturedUser).isEqualTo(expectedUser)

        assertThat(authenticatedPrincipalIdCaptor.firstValue).isNotNull

        assertThat(authorizedClientRegistrationIdCaptor.firstValue)
            .isEqualTo(User.AuthorizedClientRegistrationId(input.authorizedClientRegistrationId))
        assertThat(principalCaptor.firstValue).isEqualTo(User.Principal(input.principal))
    }
}
