package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException

/**
 * PrivateSchedule を登録する UseCase.
 */
interface SavePrivateScheduleUseCase {

    /**
     * PrivateSchedule 登録.
     *
     * @param parameter パラメータ
     * @throws DuplicateException 既に存在する
     * @throws IllegalStateException 指定したユーザが存在しない
     */
    fun savePrivateSchedule(parameter: SavePrivateScheduleUseCaseParameter)
}
