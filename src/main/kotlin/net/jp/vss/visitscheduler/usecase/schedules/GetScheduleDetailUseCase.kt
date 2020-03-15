package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException

/**
 * Schedule を取得する UseCase.
 */
interface GetScheduleDetailUseCase {

    /**
     * Schedule 詳細取得.
     *
     * @param scheduleCode schedule_code
     * @param userCode ログインユーザのユーザコード
     * @return 対象 Schedule
     * @throws NotFoundException 指定したScheduleが存在しない
     */
    fun getScheduleDetail(scheduleCode: String, userCode: String): ScheduleDetailUseCaseResult
}
