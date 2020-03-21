package net.jp.vss.visitscheduler.usecase.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import net.jp.vss.visitscheduler.domain.schedules.calculate.ScheduleResult

/**
 * CalculateUseCase の結果.
 *
 * JSON 化することを想定しています。
 * @property visitSchedules 学校に紐づく訪問日リスト
 */
data class CalculateUseCaseResult(
    @field:JsonProperty("visit_schedules")
    val visitSchedules: List<VisitDayAndSchool>
) {

    companion object {
        fun of(visitSchedules: List<ScheduleResult.Detail>): CalculateUseCaseResult {
            val results = visitSchedules.flatMap {
                val schoolCode = it.schoolCode.value
                it.visitDates.map {
                    visitSchedule -> VisitDayAndSchool(visitSchedule.date.dayOfMonth, schoolCode)
                }.toList()
            }.toList()
            return CalculateUseCaseResult(results)
        }
    }

    /**
     * 学校に紐づく訪問日
     *
     * @property day 訪問日
     * @property schoolCode 学校コード
     */
    data class VisitDayAndSchool(
        @field:JsonProperty("visit_day")
        val day: Int,
        @field:JsonProperty("school_code")
        val schoolCode: String
    )
}
