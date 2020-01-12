package net.jp.vss.visitscheduler.infrastructure.schools

import org.seasar.doma.Dao
import org.seasar.doma.Delete
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Update
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.experimental.Sql
import org.seasar.doma.jdbc.Result
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException

/**
 * School Dao.
 */
@Dao
@ConfigAutowireable
interface SchoolDao {

    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: SchoolEntity): Result<SchoolEntity>

    /**
     * school_code と user_code による取得.
     *
     * @param schoolCode 学校コード
     * @param userCode ユーザコード
     * @param options オプション(FOR UPDATE等)
     * @return 該当データ(存在しない場合、null)
     */
    @Sql("""
        SELECT *
        FROM
            schools
        WHERE
            school_code = /*schoolCode*/'abc' AND
            user_code = /*userCode*/'def'
    """)
    @Select
    fun findBySchoolCodeAndUserCode(schoolCode: String, userCode: String, options: SelectOptions): SchoolEntity?

    /**
     * 更新.
     *
     * @param entity 更新Entity
     * @return 結果
     * @throws OptimisticLockingFailureException 更新対象レコードが存在しない
     */
    @Update(include = ["name", "memo",
        "attributes", "lastUpdateUserCode", "lastUpdateAt", "versionNo"])
    fun update(entity: SchoolEntity): Result<SchoolEntity>

    /**
     * 削除.
     *
     * @param entity 対象 Entity
     * @return 結果
     * @throws OptimisticLockingFailureException 更新対象レコードが存在しない
     */
    @Delete
    fun delete(entity: SchoolEntity): Result<SchoolEntity>

    /**
     * 全件取得.
     *
     * @param userCode ユーザコード
     * @return 該当レコード
     */
    @Sql("SELECT * FROM schools WHERE user_code = /*userCode*/'def' ORDER BY create_at")
    @Select
    fun findAll(userCode: String): List<SchoolEntity>
}
