package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import org.seasar.doma.Dao
import org.seasar.doma.Delete
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.experimental.Sql
import org.seasar.doma.jdbc.Result
import org.springframework.dao.DuplicateKeyException

/**
 * VisitSchedules Dao.
 */
@Dao
@ConfigAutowireable
interface VisitSchedulesDao {

    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: VisitSchedulesEntity): Result<VisitSchedulesEntity>

    /**
     * レコード取得.
     *
     * @param schoolCodes school_code リスト
     * @param visitDateFrom 検索対象日 From
     * @param visitDateTo 検索対象日 To
     * @return 該当レコード
     */
    @Sql("""
        SELECT
            vs.*,
            s.school_code
        FROM
            visit_schedules AS vs
            INNER JOIN
                (SELECT school_id, school_code FROM schools
                    WHERE school_code in /*schoolCodes*/('def')) AS s ON vs.school_id = s.school_id
        WHERE
            /*visitDateFrom*/'2018-01-01' <= vs.visit_date
            AND vs.visit_date <= /*visitDateTo*/'2018-01-31'
        ORDER BY vs.visit_date ASC
    """)
    @Select
    fun findBySchoolCodesAndTargetDate(
        schoolCodes: List<String>,
        visitDateFrom: LocalDate,
        visitDateTo: LocalDate
    ): List<VisitSchedulesEntity>

    /**
     * 削除.
     *
     * <p>
     * - 学校IDが合致
     * - 検索対象日 From 以降(含む)
     * - 検索対象日 To まで(含む)
     *
     * のレコードを削除します。
     * </p>
     * @param schoolIds 学校IDリスト
     * @param visitDateFrom 検索対象日 From
     * @param visitDateTo 検索対象日 To
     * @return 削除件数
     */
    @Sql("""
        DELETE
        FROM
            visit_schedules
        WHERE
            school_id in /*schoolIds*/('a','b')
            AND /*visitDateFrom*/'2018-01-01' <= visit_date
            AND visit_date <= /*visitDateTo*/'2018-01-31'
    """)
    @Delete
    fun delete(schoolIds: List<String>, visitDateFrom: LocalDate, visitDateTo: LocalDate): Int
}
