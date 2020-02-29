package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School

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
) {

    /**
     * SchoolCodeAndCalculationTargets 生成.
     *
     * @return SchoolCodeAndCalculationTargets インスタンス
     */
    fun toSchoolCodeAndCalculationTargets(): Schedule.SchoolCodeAndCalculationTargets {
        return Schedule.SchoolCodeAndCalculationTargets(schoolCodeAndCalculationTargets.map {
            it.toSchoolCodeAndCalculationTarget() }.toList())
    }

    /**
     * スケジュールに関連する school_code と 計算対象の組み合わせ,
     *
     * @property schoolCodeValue school_code 値
     * @property calculationTarget 計算対象の場合、true
     */
    data class SchoolCodeAndCalculationTarget(
        val schoolCodeValue: String,
        val calculationTarget: Boolean
    ) {
        fun toSchoolCodeAndCalculationTarget(): Schedule.SchoolCodeAndCalculationTarget {
            return Schedule.SchoolCodeAndCalculationTarget(schoolCode = School.SchoolCode(schoolCodeValue),
                calculationTarget = calculationTarget)
        }
    }
}
