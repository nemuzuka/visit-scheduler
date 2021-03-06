package net.jp.vss.visitscheduler.usecase.schedules

/**
 * CreateScheduleUseCase のパラメータ.
 *
 * @property scheduleCode スケジュールコード
 * @param targetDateString 対象年月文字列(yyyy-MM)
 * @property attributes 付帯情報(JSON 文字列)
 * @property createUserCode 登録ユーザコード
 * @property schoolCodeAndCalculationTargets スケジュールに関連する school_code と 計算対象の組み合わせリスト
 */
data class CreateScheduleUseCaseParameter(
    val scheduleCode: String,
    val targetDateString: String,
    val attributes: String?,
    val createUserCode: String,
    val schoolCodeAndCalculationTargets: List<SchoolCodeAndCalculationTarget>
)
