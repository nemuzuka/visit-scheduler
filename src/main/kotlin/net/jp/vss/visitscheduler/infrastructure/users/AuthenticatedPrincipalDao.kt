package net.jp.vss.visitscheduler.infrastructure.users

import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.experimental.Sql
import org.seasar.doma.jdbc.Result
import org.springframework.dao.DuplicateKeyException

/**
 * AuthenticatedPrincipal Dao.
 */
@Dao
@ConfigAutowireable
interface AuthenticatedPrincipalDao {

    /**
     * authenticatedPrincipalId による取得.
     *
     * @param authenticatedPrincipalId 識別子
     * @return 該当データ(存在しない場合、null)
     */
    @Sql("""
        SELECT *
        FROM
            authenticated_principals
        WHERE
            authenticated_principal_id = /*authenticatedPrincipalId*/'abc'
    """)
    @Select
    fun findByAuthenticatedPrincipalId(authenticatedPrincipalId: String): AuthenticatedPrincipalEntity?

    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: AuthenticatedPrincipalEntity): Result<AuthenticatedPrincipalEntity>
}
