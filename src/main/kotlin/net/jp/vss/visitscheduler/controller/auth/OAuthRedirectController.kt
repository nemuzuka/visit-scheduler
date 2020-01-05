package net.jp.vss.visitscheduler.controller.auth

import net.jp.vss.visitscheduler.configurations.VssConfigurationProperties
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * OAuth 認証後のリダイレクト先となる Controller.
 */
@Controller
class OAuthRedirectController(
    private val vssConfigurationProperties: VssConfigurationProperties,
    private val getUserUseCase: GetUserUseCase
) {

    /**
     * 認証後のリダイレクト先ディスパッチ.
     *
     * 認証情報から登録済みユーザかを判定し、リダイレクトします
     * - 未登録の場合 -> ユーザ登録画面
     * - 登録済みの場合 -> TOP
     *
     * @return リダイレクト先 URL
     */
    @GetMapping("/auth/approved")
    fun approved(): String {
        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val principal = authentication.principal
        val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

        return if (user == null) {
            // ユーザ登録画面へリダイレクト
            "redirect:${vssConfigurationProperties.redirectBaseUrl}/user-settings"
        } else {
            // TOP画面へリダイレクト
            "redirect:${vssConfigurationProperties.redirectBaseUrl}/"
        }
    }
}
