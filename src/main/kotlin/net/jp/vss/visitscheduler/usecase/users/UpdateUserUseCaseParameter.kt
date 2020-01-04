package net.jp.vss.visitscheduler.usecase.users

import net.jp.vss.visitscheduler.domain.users.User

/**
 * UpdateUserUseCase のパラメータ.
 *
 * @property userCode ユーザコード
 * @property userName ユーザ名
 */
data class UpdateUserUseCaseParameter(
    val userCode: String,
    val userName: String
) {
    /**
     * 更新対象 User 生成.
     *
     * パラメータを反映した User を生成します
     *
     * @param user 更新元 User
     * @return 更新対象 User
     */
    fun buildUpdateUser(user: User): User {

        val baseUserDetail = user.userDetail
        val userDetail = baseUserDetail.copy(userName = userName)
        return user.copy(userDetail = userDetail)
    }
}
