package net.jp.vss.visitscheduler

import net.jp.vss.visitscheduler.usecase.users.CreateUserUseCase
import net.jp.vss.visitscheduler.usecase.users.CreateUserUseCaseParameter
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.springframework.stereotype.Component

@Component
class IntegrationHelper(
    private val createUserUseCase: CreateUserUseCase
) {

    fun createUser(userCode: String, authorizedClientRegistrationId: String, principal: String): UserUseCaseResult {
        val parameter = CreateUserUseCaseParameter(
            userCode = userCode,
            userName = "ユーザ名",
            authorizedClientRegistrationId = authorizedClientRegistrationId,
            principal = principal)
        return createUserUseCase.createUser(parameter)
    }
}
