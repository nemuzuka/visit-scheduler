package net.jp.vss.visitscheduler.controller

import com.fasterxml.jackson.annotation.JsonProperty
import javax.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 共通の ApiController.
 *
 * @property session HttpSession
 */
@RestController
@RequestMapping("/api")
@Validated
class IndexApiController(
    private val session: HttpSession,
    private val clientRegistrationRepository: InMemoryClientRegistrationRepository
) {

    /**
     * health チェック.
     *
     * @return レスポンス
     */
    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("It's work!")
    }

    /**
     * OpenID Connect のプロバイダ一覧取得.
     *
     * @return レスポンス
     */
    @GetMapping("/open-id-connects")
    fun openIdConnectList(): ResponseEntity<ListResponse<OAuth2Registration>> {
        return ResponseEntity.ok(ListResponse(clientRegistrationRepository.map {
            val registrationId = it.registrationId
            val authorizationUrl = "/oauth2/authorization/$registrationId"
            val registrationName = it.clientName
            OAuth2Registration(authorizationUrl = authorizationUrl,
                registrationName = registrationName, registrationId = registrationId)
        }.toList()))
    }

    data class OAuth2Registration(
        @field:JsonProperty("authorization_url")
        val authorizationUrl: String,

        @field:JsonProperty("registration_name")
        val registrationName: String,

        @field:JsonProperty("registration_id")
        val registrationId: String
    )
}
