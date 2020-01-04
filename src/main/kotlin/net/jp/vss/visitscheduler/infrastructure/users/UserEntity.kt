package net.jp.vss.visitscheduler.infrastructure.users

import net.jp.vss.visitscheduler.domain.users.User
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table

/**
 * ユーザ Entity.
 *
 * @property userId 識別子
 * @property userCode ユーザコード
 * @property authenticatedPrincipalId OpenID Connect 認証情報識別子
 * @property userName ユーザ名
 */
@Entity(immutable = true)
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "user_code")
    val userCode: String,

    @Column(name = "authenticated_principal_id")
    val authenticatedPrincipalId: String,

    @Column(name = "user_name")
    val userName: String
) {
    /**
     * User 変換
     *
     * @return 変換後 User
     */
    fun toUser(): User {
        return User(userId = User.UserId(userId), userCode = User.UserCode(userCode),
            userDetail = User.UserDetail(userName))
    }
}
