package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.users.User

/**
 * PrivateSchedule のリポジトリ.
 */
interface PrivateScheduleRepository {

    /**
     * 永続化.
     *
     * <p>
     * ユーザコード、対象年月の範囲内のスケジュールを削除した後で登録します。
     * </p>
     * @param userCode ユーザコード
     * @param targetYearAndMonth 対象年月
     * @param privateSchedules 対象 PrivateSchedule リスト
     * @throws DuplicateException 既に存在する
     * @throws IllegalStateException 指定したユーザが存在しない
     */
    fun save(
        userCode: User.UserCode,
        targetYearAndMonth: Schedule.TargetYearAndMonth,
        privateSchedules: List<PrivateSchedule>
    )

    /**
     * 取得.
     *
     * @param userCode ユーザコード
     * @param targetYearAndMonth 対象年月
     * @return 該当レコード
     */
    fun getPrivateSchedules(
        userCode: User.UserCode,
        targetYearAndMonth: Schedule.TargetYearAndMonth
    ): List<PrivateSchedule>
}
