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
 * SchoolSchedule Dao.
 */
@Dao
@ConfigAutowireable
interface SchoolScheduleDao {
    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: SchoolScheduleEntity): Result<SchoolScheduleEntity>

    /**
     * 全件取得.
     *
     * <p>
     * - 学校コードが合致
     * - 検索対象日 From 以降(含む)
     * - 検索対象日 To まで(含む)
     *
     * のレコードを取得します。
     * </p>
     * @param schoolCodes ユーザコードリスト
     * @param targetDateFrom 検索対象日 From
     * @param targetDateTo 検索対象日 To
     * @return 該当データ(存在しない場合、空リスト)
     */
    @Sql("""
        SELECT
            ss.*,
            s.school_code
        FROM
            school_schedules AS ss
            INNER JOIN
                (SELECT school_id, school_code FROM schools
                    WHERE school_code in /*schoolCodes*/('def')) AS s ON ss.school_id = s.school_id
        WHERE
            /*targetDateFrom*/'2018-01-01' <= ss.target_date
            AND ss.target_date <= /*targetDateTo*/'2018-01-31'
        ORDER BY ss.target_date ASC
    """)
    @Select
    fun findBySchoolCodesAndTargetDate(
        schoolCodes: List<String>,
        targetDateFrom: LocalDate,
        targetDateTo: LocalDate
    ): List<SchoolScheduleEntity>

    /**
     * 削除.
     *
     * <p>
     * - 学校コードが合致
     * - 検索対象日 From 以降(含む)
     * - 検索対象日 To まで(含む)
     *
     * のレコードを削除します。
     * </p>
     * @param schoolCode 学校コード
     * @param targetDateFrom 検索対象日 From
     * @param targetDateTo 検索対象日 To
     * @return 削除件数
     */
    @Sql("""
        DELETE
        FROM
            school_schedules
        WHERE
            school_id in (SELECT school_id FROM schools WHERE school_code = /*schoolCode*/'def')
            AND /*targetDateFrom*/'2018-01-01' <= target_date
            AND target_date <= /*targetDateTo*/'2018-01-31'
    """)
    @Delete
    fun delete(schoolCode: String, targetDateFrom: LocalDate, targetDateTo: LocalDate): Int
}
