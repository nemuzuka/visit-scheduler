package net.jp.vss.visitscheduler.usecase.users

import com.fasterxml.jackson.annotation.JsonProperty
import net.jp.vss.visitscheduler.domain.users.User

/**
 * UserUseCase の結果.
 *
 * JSON 化することを想定しています。
 * @property userCode ユーザコード
 * @property userName ユーザ名
 */
data class UserUseCaseResult(
    @field:JsonProperty("user_code")
    val userCode: String,

    @field:JsonProperty("user_name")
    val userName: String
) {
    companion object {
        /**
         * User からのインスタンス生成.
         *
         * @param user 対象 User
         * @return 生成インスタンス
         */
        fun of(user: User): UserUseCaseResult {
            return UserUseCaseResult(
                userCode = user.userCode.value,
                userName = user.userDetail.userName)
        }
    }
}
