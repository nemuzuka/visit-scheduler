package net.jp.vss.visitscheduler.infrastructure.schedules

import org.seasar.doma.Dao
import org.seasar.doma.Delete
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.experimental.Sql
import org.seasar.doma.jdbc.Result
import org.springframework.dao.DuplicateKeyException

/**
 * LastMonthVisitDate Dao.
 */
@Dao
@ConfigAutowireable
interface LastMonthVisitDateDao {

    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: LastMonthVisitDateEntity): Result<LastMonthVisitDateEntity>

    /**
     * 削除.
     *
     * <p>
     * - 学校IDが合致
     * - 対象年月が合致
     *
     * のレコードを削除します。
     * </p>
     * @param schoolId 学校ID
     * @param targetYearAndMonth 対象年月
     * @return 削除件数
     */
    @Sql("""
        DELETE
        FROM
            last_month_visit_dates
        WHERE
            school_id = /*schoolId*/'a'
            AND target_year_and_month = /*targetYearAndMonth*/'2018-01'
    """)
    @Delete
    fun delete(schoolId: String, targetYearAndMonth: String): Int

    /**
     * レコード取得.
     *
     * @param schoolCodes school_code リスト
     * @param targetYearAndMonth 対象年月
     * @return 該当レコード
     */
    @Sql("""
        SELECT
            lmvd.*,
            s.school_code
        FROM
            last_month_visit_dates AS lmvd
            INNER JOIN
                (SELECT school_id, school_code FROM schools
                    WHERE school_code in /*schoolCodes*/('def')) AS s ON lmvd.school_id = s.school_id
        WHERE
            lmvd.target_year_and_month = /*targetYearAndMonth*/'2018-01'
    """)
    @Select
    fun findBySchoolCodesAndTargetDate(
        schoolCodes: List<String>,
        targetYearAndMonth: String
    ): List<LastMonthVisitDateEntity>
}
