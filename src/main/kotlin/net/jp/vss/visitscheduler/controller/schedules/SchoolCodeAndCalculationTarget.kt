package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * 学校コードとスケジュール計算対象要素.
 *
 * @property schoolCode 学校コード
 * @property calculationTarget スケジュール計算対象の場合、true
 */
data class SchoolCodeAndCalculationTarget(
    @field:NotNull
    @field:Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
    @field:Size(max = 128)
    @field:JsonProperty("school_code")
    val schoolCode: String? = null,

    @field:NotNull
    @field:JsonProperty("calculation_target")
    val calculationTarget: Boolean? = null
) {
    fun toSchoolCodeAndCalculationTarget(): net.jp.vss.visitscheduler.usecase.schedules.SchoolCodeAndCalculationTarget {
        return net.jp.vss.visitscheduler.usecase.schedules.SchoolCodeAndCalculationTarget(
            schoolCodeValue = schoolCode!!,
            calculationTarget = calculationTarget!!)
    }
}
