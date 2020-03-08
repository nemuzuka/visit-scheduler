package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException

/**
 * SchoolSchedule を登録する UseCase.
 */
interface SaveSchoolScheduleUseCase {

    /**
     * SchoolSchedule 登録.
     *
     * @param parameter パラメータ
     * @throws DuplicateException 既に存在する
     * @throws IllegalStateException 指定したユーザが存在しない
     */
    fun saveSchoolSchedule(parameter: SaveSchoolScheduleUseCaseParameter)
}
