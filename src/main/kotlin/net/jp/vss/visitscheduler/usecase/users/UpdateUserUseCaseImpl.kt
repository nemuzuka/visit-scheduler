package net.jp.vss.visitscheduler.usecase.users

import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.domain.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link UpdateUserUseCase}
 *
 * @property userRepo User のリポジトリ
 */
@Service
@Transactional
class UpdateUserUseCaseImpl(private val userRepo: UserRepository) : UpdateUserUseCase {
    override fun updateUser(parameter: UpdateUserUseCaseParameter): UserUseCaseResult {
        val user = userRepo.lockUser(User.UserCode(parameter.userCode))
        val updateUser = parameter.buildUpdateUser(user)
        return UserUseCaseResult.of(userRepo.updateUser(updateUser))
    }
}
