package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException

/**
 * Schedule を登録する UseCase.
 */
interface CreateScheduleUseCase {

    /**
     * Schedule 登録.
     *
     * @param parameter パラメータ
     * @return 登録結果
     * @throws DuplicateException 既に存在する
     * @throws IllegalStateException 指定したユーザが存在しない
     */
    fun createSchedule(parameter: CreateScheduleUseCaseParameter): ScheduleUseCaseResult
}
