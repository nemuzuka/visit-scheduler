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
 * PrivateSchedule Dao.
 */
@Dao
@ConfigAutowireable
interface PrivateScheduleDao {
    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: PrivateScheduleEntity): Result<PrivateScheduleEntity>

    /**
     * 全件取得.
     *
     * <p>
     * - ユーザコードが合致
     * - 検索対象日 From 以降(含む)
     * - 検索対象日 To まで(含む)
     *
     * のレコードを取得します。
     * </p>
     * @param userCode ユーザコード
     * @param targetDateFrom 検索対象日 From
     * @param targetDateTo 検索対象日 To
     * @return 該当データ(存在しない場合、空リスト)
     */
    @Sql("""
        SELECT
            ps.*
        FROM
            private_schedules AS ps
            INNER JOIN (SELECT user_id FROM users WHERE user_code = /*userCode*/'def') AS u ON ps.user_id = u.user_id
        WHERE
            /*targetDateFrom*/'2018-01-01' <= ps.target_date
            AND ps.target_date <= /*targetDateTo*/'2018-01-31'
        ORDER BY ps.target_date ASC
    """)
    @Select
    fun findByUserCodeAndTargetDate(
        userCode: String,
        targetDateFrom: LocalDate,
        targetDateTo: LocalDate
    ): List<PrivateScheduleEntity>

    /**
     * 削除.
     *
     * <p>
     * - ユーザコードが合致
     * - 検索対象日 From 以降(含む)
     * - 検索対象日 To まで(含む)
     *
     * のレコードを削除します。
     * </p>
     * @param userCode ユーザコード
     * @param targetDateFrom 検索対象日 From
     * @param targetDateTo 検索対象日 To
     * @return 削除件数
     */
    @Sql("""
        DELETE
        FROM
            private_schedules
        WHERE
            user_id in (SELECT user_id FROM users WHERE user_code = /*userCode*/'def')
            AND /*targetDateFrom*/'2018-01-01' <= target_date
            AND target_date <= /*targetDateTo*/'2018-01-31'
    """)
    @Delete
    fun delete(userCode: String, targetDateFrom: LocalDate, targetDateTo: LocalDate): Int
}
