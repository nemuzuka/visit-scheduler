package net.jp.vss.visitscheduler.domain.users

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException

/**
 * User のリポジトリ.
 */
interface UserRepository {

    /**
     * 認証情報からの取得.
     *
     * @param authorizedClientRegistrationId AuthorizedClientRegistrationId
     * @param principal Principal
     * @return 該当ユーザ(存在しない場合、null)
     */
    fun getUserOrNull(
        authorizedClientRegistrationId: User.AuthorizedClientRegistrationId,
        principal: User.Principal
    ): User?

    /**
     * 登録.
     *
     * @param user 対象 User
     * @param authenticatedPrincipalId 認証済みユーザ識別子
     * @param authorizedClientRegistrationId AuthorizedClientRegistrationId
     * @param principal Principal
     * @return 登録後 User
     * @throws DuplicateException 既に存在する
     */
    fun createUser(
        user: User,
        authenticatedPrincipalId: User.AuthenticatedPrincipalId,
        authorizedClientRegistrationId: User.AuthorizedClientRegistrationId,
        principal: User.Principal
    ): User

    /**
     * 更新.
     *
     * @param user 対象 User
     * @return 更新後 User
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun updateUser(user: User): User

    /**
     * lock して取得.
     *
     * @param userCode ユーザコード
     * @return 該当 User
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun lockUser(userCode: User.UserCode): User
}
