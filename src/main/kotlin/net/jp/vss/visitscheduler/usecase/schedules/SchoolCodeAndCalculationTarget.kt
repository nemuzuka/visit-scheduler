package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School

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
    companion object {
        fun toSchoolCodeAndCalculationTargets(
            schoolCodeAndCalculationTargets: List<SchoolCodeAndCalculationTarget>
        ):
            Schedule.SchoolCodeAndCalculationTargets {
            return Schedule.SchoolCodeAndCalculationTargets(schoolCodeAndCalculationTargets.map {
                it.toSchoolCodeAndCalculationTarget() }.toList())
        }
    }

    fun toSchoolCodeAndCalculationTarget(): Schedule.SchoolCodeAndCalculationTarget {
        return Schedule.SchoolCodeAndCalculationTarget(schoolCode = School.SchoolCode(schoolCodeValue),
            calculationTarget = calculationTarget)
    }
}
