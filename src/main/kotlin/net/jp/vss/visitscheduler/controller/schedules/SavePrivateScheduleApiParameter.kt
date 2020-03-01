package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.usecase.schedules.SavePrivateScheduleUseCaseParameter

/**
 * SavePrivateScheduleApiController のパラメータ.
 */
data class SavePrivateScheduleApiParameter(

    @field:NotNull
    @field:Pattern(regexp = "^[0-9]{4}-[0-9]{2}\$")
    @field:JsonProperty("target_year_and_month")
    val targetYearAndMonth: String? = null,

    @field:NotNull
    @field:JsonProperty("target_day_and_memos")
    @field:Valid
    val targetDayAndMemos: List<TargetDayAndMemo>? = null
) {
    /**
     * SavePrivateScheduleUseCaseParameter に変換.
     *
     * @param userCode User.user_code
     * @return 生成 SavePrivateScheduleUseCaseParameter
     */
    fun toParameter(userCode: String): SavePrivateScheduleUseCaseParameter =
        SavePrivateScheduleUseCaseParameter(
            targetDateString = targetYearAndMonth!!,
            createUserCode = userCode,
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
        val memo: String? = null
    ) {
        fun toTargetDayAndMemo(): SavePrivateScheduleUseCaseParameter.TargetDayAndMemo {
            return SavePrivateScheduleUseCaseParameter.TargetDayAndMemo(
                targetDay = targetDay!!,
                memo = memo)
        }
    }
}
