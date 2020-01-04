package net.jp.vss.visitscheduler.infrastructure.users

import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Update
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.experimental.Sql
import org.seasar.doma.jdbc.Result
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.dao.DuplicateKeyException

/**
 * User Dao.
 */
@Dao
@ConfigAutowireable
interface UserDao {

    /**
     * user_code による取得.
     *
     * @param userCode ユーザコード
     * @param options オプション(FOR UPDATE等)
     * @return 該当データ(存在しない場合、null)
     */
    @Sql("""
        SELECT *
        FROM
            users
        WHERE
            user_code = /*userCode*/'abc'
    """)
    @Select
    fun findByUserCode(userCode: String, options: SelectOptions): UserEntity?

    /**
     * authorized_client_registration_id と principal による取得.
     *
     * @param authorizedClientRegistrationId ユーザが認証した AuthorizedClientRegistrationId (e.g. google)
     * @param principal ユーザが認証した AuthorizedClientRegistration 上の principal
     * @return 該当データ(存在しない場合、null)
     */
    @Select
    fun findByAuthorizedClientRegistrationIdAndPrincipal(
        authorizedClientRegistrationId: String,
        principal: String
    ): UserEntity?

    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: UserEntity): Result<UserEntity>

    /**
     * ユーザ名更新.
     *
     * @param userId 対象識別子
     * @param userName ユーザ名
     * @return 件数
     */
    @Update(sqlFile = true)
    fun updateUserName(userId: String, userName: String): Int
}
