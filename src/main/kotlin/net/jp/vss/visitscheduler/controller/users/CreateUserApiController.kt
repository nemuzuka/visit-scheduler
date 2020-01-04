package net.jp.vss.visitscheduler.controller.users

import net.jp.vss.visitscheduler.controller.exceptions.HttpConflictException
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.usecase.users.CreateUserUseCase
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * CreateUser の APIController.
 *
 * @property createUserUseCase CreateUser の UseCase
 */
@RestController
@RequestMapping("/api/users")
@Validated
class CreateUserApiController(
    private val createUserUseCase: CreateUserUseCase
) {

    companion object {
        private val log = LoggerFactory.getLogger(CreateUserApiController::class.java)
    }

    /**
     * CreateUser.
     *
     * @param parameter パラメータ
     * @return レスポンス
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(
        @Validated
        @RequestBody
        parameter: CreateUserApiParameter
    ): ResponseEntity<UserUseCaseResult> {

        try {

            val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val principal = authentication.principal

            val result = createUserUseCase.createUser(
                parameter.toParameter(authentication.authorizedClientRegistrationId, principal.name))
            return ResponseEntity.ok(result)
        } catch (e: DuplicateException) {
            // 既に user_code が存在した場合
            log.info("Conclift Parameter({}) {}", parameter, e.message)
            throw HttpConflictException(e.message!!, e)
        }
    }
}
