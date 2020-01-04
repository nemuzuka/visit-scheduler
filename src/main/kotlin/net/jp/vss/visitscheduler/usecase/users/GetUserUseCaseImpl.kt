package net.jp.vss.visitscheduler.usecase.users

import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.domain.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link GetUserUseCase}
 *
 * @property userRepo User のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class GetUserUseCaseImpl(
    private val userRepo: UserRepository
) : GetUserUseCase {

    override fun getUser(authorizedClientRegistrationId: String, principal: String): UserUseCaseResult? {
        val user = userRepo.getUserOrNull(
            authorizedClientRegistrationId = User.AuthorizedClientRegistrationId(authorizedClientRegistrationId),
            principal = User.Principal(principal))
        return user?.let { UserUseCaseResult.of(it) }
    }
}
