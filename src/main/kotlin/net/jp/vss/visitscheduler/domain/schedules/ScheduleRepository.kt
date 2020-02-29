package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.users.User

/**
 * Schedule のリポジトリ.
 */
interface ScheduleRepository {

    /**
     * 登録.
     *
     * @param schedule 対象 Schedule
     * @param schoolCodeAndCalculationTargets スケジュールに関連する school_code と 計算対象の組み合わせリスト
     * @return 登録後 Schedule
     * @throws DuplicateException 既に存在する
     * @throws IllegalStateException 指定したユーザが存在しない
     */
    fun create(
        schedule: Schedule,
        schoolCodeAndCalculationTargets: Schedule.SchoolCodeAndCalculationTargets
    ): Schedule

    /**
     * 取得.
     *
     * @param scheduleCode スケジュールコード
     * @param userCode ユーザコード
     * @return 該当 Schedule
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun getSchedule(scheduleCode: Schedule.ScheduleCode, userCode: User.UserCode): Schedule

    /**
     * Lock して取得.
     *
     * @param scheduleCode スケジュールコード
     * @param userCode ユーザコード
     * @return 該当 Schedule
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun lockSchedule(scheduleCode: Schedule.ScheduleCode, userCode: User.UserCode): Schedule
}
