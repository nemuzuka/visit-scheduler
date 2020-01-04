package net.jp.vss.visitscheduler.controller.users

import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Me の APIController.
 *
 * @property getUserUseCase GetUser の UseCase
 */
@RestController
@RequestMapping("/api/me")
@Validated
class MeApiController(
    private val getUserUseCase: GetUserUseCase
) {

    /**
     * 自分の情報取得.
     *
     * 該当データが存在しない場合、プロパティが全て空文字のユーザ情報をレスポンスします
     * @return レスポンス
     */
    @GetMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun me(): ResponseEntity<UserUseCaseResult> {
        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val principal = authentication.principal
        val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)
        return ResponseEntity.ok(user ?: UserUseCaseResult(userCode = "", userName = ""))
    }
}
