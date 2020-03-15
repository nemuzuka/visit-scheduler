package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.users.User

/**
 * ScheduleDetail のリポジトリ
 */
interface ScheduleDetailRepository {

    /**
     * Schedule 詳細取得.
     *
     * @param scheduleCode スケジュールコード
     * @param userCode ユーザコード
     * @return 該当 Schedule 詳細
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun getScheduleDetail(scheduleCode: Schedule.ScheduleCode, userCode: User.UserCode): ScheduleDetail
}
