package net.jp.vss.visitscheduler.usecase.schedules

/**
 * PrivateSchedule 一覧を取得する UseCase.
 */
interface ListPrivateScheduleUseCase {

    /**
     * PrivateSchedule 取得.
     *
     * @param userCode 対象ユーザコード
     * @param targetYearAndMonth 対象年月(YYYY-MM 形式)
     * @return 対象 PrivateSchedule List
     */
    fun getPrivateSchedule(userCode: String, targetYearAndMonth: String): List<PrivateScheduleUseCaseResult>
}
