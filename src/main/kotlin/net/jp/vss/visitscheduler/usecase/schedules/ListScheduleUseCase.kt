package net.jp.vss.visitscheduler.usecase.schedules

/**
 * Schedule 一覧を取得する UseCase.
 */
interface ListScheduleUseCase {

    /**
     * 全ての Schedule 取得.
     *
     * @param userCode 対象ユーザコード
     * @return 対象 Schedule List
     */
    fun allSchedules(userCode: String): List<ScheduleUseCaseResult>
}
