package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule
import net.jp.vss.visitscheduler.usecase.schedules.SaveSchoolScheduleUseCaseParameter

/**
 * SaveSchoolScheduleApiController のパラメータ.
 */
data class SaveSchoolScheduleApiParameter(

    @field:NotNull
    @field:Pattern(regexp = "^[0-9]{4}-[0-9]{2}\$")
    @field:JsonProperty("target_year_and_month")
    val targetYearAndMonth: String? = null,

    @field:NotNull
    @field:Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
    @field:JsonProperty("school_code")
    val schoolCode: String? = null,

    @field:NotNull
    @field:JsonProperty("target_day_and_memos")
    @field:Valid
    val targetDayAndMemos: List<TargetDayAndMemo>? = null
) {
    /**
     * SaveSchoolScheduleUseCaseParameter に変換.
     *
     * @param userCode User.user_code
     * @return 生成 SaveSchoolScheduleUseCaseParameter
     */
    fun toParameter(userCode: String): SaveSchoolScheduleUseCaseParameter =
        SaveSchoolScheduleUseCaseParameter(
            targetDateString = targetYearAndMonth!!,
            createUserCode = userCode,
            schoolCode = schoolCode!!,
            targetDayAndMemos = targetDayAndMemos!!.map {
                it.toTargetDayAndMemo()
            }.toList())

    data class TargetDayAndMemo(
        @field:NotNull
        @field:Min(1)
        @field:Max(31)
        @field:JsonProperty("target_day")
        val targetDay: Int? = null,

        @field:JsonProperty("memo")
        val memo: String? = null,

        @field:NotNull
        @field:JsonProperty("priority")
        val priority: SchoolSchedule.Priority? = null

    ) {
        fun toTargetDayAndMemo(): SaveSchoolScheduleUseCaseParameter.TargetDayAndMemo {
            return SaveSchoolScheduleUseCaseParameter.TargetDayAndMemo(
                targetDay = targetDay!!,
                memo = memo,
                priority = priority!!)
        }
    }
}
