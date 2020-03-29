package net.jp.vss.visitscheduler.usecase.schedules

/**
 * SchoolSchedule 一覧を取得する UseCase.
 */
interface ListSchoolScheduleUseCase {

    /**
     * SchoolSchedule 取得.
     *
     * <p>
     * 先月の最終訪問日は、
     *
     * 1. 登録されていればその値
     * 2. 登録されておらず、前月の最終訪問日が存在する場合その値
     * 3. 登録されておらず、前月の最終訪問日が存在しない場合、null
     *
     * を設定します。
     * </p>
     * @param schoolCodeValue 対象学校コード
     * @param targetYearAndMonth 対象年月(YYYY-MM 形式)
     * @return 対象 SchoolSchedule
     */
    fun getSchoolSchedule(schoolCodeValue: String, targetYearAndMonth: String): SchoolScheduleUseCaseResult
}
