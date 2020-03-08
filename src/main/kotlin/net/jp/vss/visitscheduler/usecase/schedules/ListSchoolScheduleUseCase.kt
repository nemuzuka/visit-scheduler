package net.jp.vss.visitscheduler.usecase.schedules

/**
 * SchoolSchedule 一覧を取得する UseCase.
 */
interface ListSchoolScheduleUseCase {

    /**
     * SchoolSchedule 取得.
     *
     * @param schoolCode 対象学校コード
     * @param targetYearAndMonth 対象年月(YYYY-MM 形式)
     * @return 対象 SchoolSchedule List
     */
    fun getSchoolSchedule(schoolCode: String, targetYearAndMonth: String): List<SchoolScheduleUseCaseResult>
}
