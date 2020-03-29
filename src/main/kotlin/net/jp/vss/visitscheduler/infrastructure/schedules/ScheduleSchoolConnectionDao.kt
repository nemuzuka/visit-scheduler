package net.jp.vss.visitscheduler.infrastructure.schedules

import org.seasar.doma.Dao
import org.seasar.doma.Delete
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Update
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.experimental.Sql
import org.seasar.doma.jdbc.Result
import org.springframework.dao.DuplicateKeyException

/**
 * ScheduleSchoolConnection Dao.
 */
@Dao
@ConfigAutowireable
interface ScheduleSchoolConnectionDao {

    /**
     * 登録.
     *
     * @param entity 登録Entity
     * @return 結果
     * @throws DuplicateKeyException 一意制約違反
     */
    @Insert
    fun create(entity: ScheduleSchoolConnectionEntity): Result<ScheduleSchoolConnectionEntity>

    /**
     * 更新.
     *
     * @param entity 更新Entity
     * @return 結果
     */
    @Update
    fun update(entity: ScheduleSchoolConnectionEntity): Result<ScheduleSchoolConnectionEntity>

    /**
     * schedule_code による取得.
     *
     * @param scheduleCode スケジュールコード
     * @return 該当データ(存在しない場合、空リスト)
     */
    @Sql("""
        SELECT ssc.*
        FROM
            schedule_school_connections AS ssc
            INNER JOIN (SELECT schedule_id FROM schedules WHERE schedule_code = /*scheduleCode*/'def') AS s ON s.schedule_id = ssc.schedule_id
        ORDER BY ssc.connection_index
    """)
    @Select
    fun findByScheduleCode(scheduleCode: String): List<ScheduleSchoolConnectionEntity>

    /**
     * 削除.
     *
     * @param entity 対象 Entity
     * @return 結果
     */
    @Delete
    fun delete(entity: ScheduleSchoolConnectionEntity): Result<ScheduleSchoolConnectionEntity>
}
