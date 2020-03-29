package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.YearMonth
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.domain.schedules.Schedule
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
    val targetDayAndMemos: List<TargetDayAndMemo>? = null,

    @field:Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}\$")
    @field:JsonProperty("last_month_visit_date")
    val lastMonthVisitDate: String? = null
) {
    /**
     * SaveSchoolScheduleUseCaseParameter に変換.
     *
     * @param userCode User.user_code
     * @return 生成 SaveSchoolScheduleUseCaseParameter
     * @throws IllegalArgumentException 先月の最終訪問日が不正だった
     */
    fun toParameter(userCode: String): SaveSchoolScheduleUseCaseParameter {

        val lastMonthVisitDate = if (lastMonthVisitDate != null) {
            val date = LocalDate.parse(lastMonthVisitDate)
            if (date.isBefore(LocalDate.parse("$targetYearAndMonth-01")) == false) {
                throw IllegalArgumentException("先月の訪問日は対象年月($targetYearAndMonth)よりも前の月の日付を設定してください。")
            }
            val targetYearMonth = YearMonth.parse(targetYearAndMonth).minusMonths(1)
            if (date.year != targetYearMonth.year || date.month != targetYearMonth.month) {
                throw IllegalArgumentException("先月の訪問日は対象年月($targetYearAndMonth)の直前の月を設定してください。")
            }
            Schedule.ScheduleDate(LocalDate.parse(lastMonthVisitDate))
        } else null

        return SaveSchoolScheduleUseCaseParameter(
            targetDateString = targetYearAndMonth!!,
            createUserCode = userCode,
            schoolCode = schoolCode!!,
            targetDayAndMemos = targetDayAndMemos!!.map {
                it.toTargetDayAndMemo()
            }.toList(),
            lastMonthVisitDate = lastMonthVisitDate)
    }

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
