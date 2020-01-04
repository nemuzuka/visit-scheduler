package net.jp.vss.visitscheduler.controller.users

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import net.jp.vss.visitscheduler.usecase.users.CreateUserUseCaseParameter

/**
 * CreateUserController のパラメータ.
 */
data class CreateUserApiParameter(

    @field:NotNull
    @field:Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
    @field:Size(max = 128)
    @field:JsonProperty("user_code")
    val userCode: String? = null,

    @field:NotNull
    @field:Size(max = 128)
    @field:JsonProperty("user_name")
    val userName: String? = null
) {
    /**
     * CreateUserUseCaseParameter に変換.
     *
     * @param authorizedClientRegistrationId AuthorizedClientRegistrationId
     * @param principal Principal
     * @return 生成 CreateUserUseCaseParameter
     */
    fun toParameter(authorizedClientRegistrationId: String, principal: String): CreateUserUseCaseParameter =
        CreateUserUseCaseParameter(
        userCode = userCode!!,
        userName = userName!!,
        authorizedClientRegistrationId = authorizedClientRegistrationId,
        principal = principal)
}
