package net.jp.vss.visitscheduler.domain.users

import java.util.UUID

/**
 * ユーザ.
 */
data class User(
    val userId: UserId,
    val userCode: UserCode,
    val userDetail: UserDetail
) {

    companion object {
        /**
         * 登録時のインスタンス生成.
         *
         * @param userCodeValue ユーザコード文字列
         * @param userName ユーザ名
         */
        fun buildForCreate(
            userCodeValue: String,
            userName: String
        ) = User(
            userId = UserId(UUID.randomUUID().toString()),
            userCode = UserCode(userCodeValue),
            userDetail = UserDetail(userName = userName))
    }

    /**
     * ユーザ 識別子値オブジェクト.
     *
     * @property value 値
     */
    data class UserId(val value: String)

    /**
     * ユーザコード値オブジェクト.
     *
     * @property value 値
     */
    data class UserCode(val value: String)

    /**
     * ユーザ詳細値オブジェクト.
     *
     * @property userName ユーザ名
     */
    data class UserDetail(val userName: String)

    /**
     * ユーザが認証した AuthorizedClientRegistrationId.
     *
     * @property value 値
     */
    data class AuthorizedClientRegistrationId(val value: String)

    /**
     * ユーザが認証した AuthorizedClientRegistration 上の principal.
     *
     * @property value 値
     */
    data class Principal(val value: String)

    /**
     * 認証済みユーザ 識別子値オブジェクト.
     *
     * @property value 値
     */
    data class AuthenticatedPrincipalId(val value: String)
}
