package net.jp.vss.visitscheduler.infrastructure.schedules

import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.experimental.Sql
import org.seasar.doma.jdbc.Result
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.dao.DuplicateKeyException

/**
 * Schedule Dao.
 */
@Dao
@ConfigAutowireable
interface ScheduleDao {

    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: ScheduleEntity): Result<ScheduleEntity>

    /**
     * schedule_code と user_code による取得.
     *
     * @param scheduleCode スケジュールコード
     * @param userCode ユーザコード
     * @param options オプション(FOR UPDATE等)
     * @return 該当データ(存在しない場合、null)
     */
    @Sql("""
        SELECT
            s.*,
            u.user_code
        FROM
            schedules AS s
            INNER JOIN (SELECT user_id, user_code FROM users WHERE user_code = /*userCode*/'def') AS u ON s.user_id = u.user_id
        WHERE
            s.schedule_code = /*scheduleCode*/'abc'
    """)
    @Select
    fun findByScheduleCodeAndUserCode(scheduleCode: String, userCode: String, options: SelectOptions): ScheduleEntity?
}
