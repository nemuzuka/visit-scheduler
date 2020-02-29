package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import net.jp.vss.visitscheduler.constrains.JsonStringConstrains
import net.jp.vss.visitscheduler.usecase.schedules.CreateScheduleUseCaseParameter

/**
 * CreateScheduleApiController のパラメータ.
 */
data class CreateScheduleApiParameter(

    @field:NotNull
    @field:Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
    @field:Size(max = 128)
    @field:JsonProperty("schedule_code")
    val scheduleCode: String? = null,

    @field:NotNull
    @field:Pattern(regexp = "^[0-9]{4}-[0-9]{2}\$")
    @field:JsonProperty("target_year_and_month")
    val targetYearAndMonth: String? = null,

    @JsonStringConstrains
    @field:JsonProperty("attributes")
    val attributes: String? = null,

    @field:NotNull
    @field:JsonProperty("school_code_and_calculation_targets")
    @field:Valid
    val schoolCodeAndCalculationTargets: List<SchoolCodeAndCalculationTarget>? = null
) {
    /**
     * CreateScheduleUseCaseParameter に変換.
     *
     * @param userCode User.user_code
     * @return 生成 CreateScheduleUseCaseParameter
     */
    fun toParameter(userCode: String): CreateScheduleUseCaseParameter =
        CreateScheduleUseCaseParameter(
            scheduleCode = scheduleCode!!,
            targetDateString = targetYearAndMonth!!,
            attributes = attributes,
            createUserCode = userCode,
            schoolCodeAndCalculationTargets = schoolCodeAndCalculationTargets!!.map {
                it.toSchoolCodeAndCalculationTarget()
            }.toList())

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
        fun toSchoolCodeAndCalculationTarget(): CreateScheduleUseCaseParameter.SchoolCodeAndCalculationTarget {
            return CreateScheduleUseCaseParameter.SchoolCodeAndCalculationTarget(
                schoolCodeValue = schoolCode!!,
                calculationTarget = calculationTarget!!)
        }
    }
}
