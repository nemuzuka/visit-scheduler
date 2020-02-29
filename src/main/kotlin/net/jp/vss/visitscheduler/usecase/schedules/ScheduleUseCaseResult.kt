package net.jp.vss.visitscheduler.usecase.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue
import com.fasterxml.jackson.annotation.JsonUnwrapped
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.usecase.ResourceAttributesResult

/**
 * ScheduleUseCase の結果.
 *
 * JSON 化することを想定しています。
 * @property scheduleCode スケジュールコード
 * @property targetYearAndMonth 対象年月文字列(yyyy-MM)
 * @property attributes 付帯情報
 * @property resourceAttributesResult リソース付帯情報
 */
data class ScheduleUseCaseResult(
    @field:JsonProperty("schedule_code")
    val scheduleCode: String,

    @field:JsonProperty("target_year_and_month")
    val targetYearAndMonth: String,

    @field:JsonRawValue
    @field:JsonProperty("attributes")
    val attributes: String?,

    @field:JsonUnwrapped
    val resourceAttributesResult: ResourceAttributesResult
) {
    companion object {
        /**
         * Schedule からのインスタンス生成.
         *
         * @param schedule 対象 Schedule
         * @return 生成インスタンス
         */
        fun of(schedule: Schedule): ScheduleUseCaseResult {
            val scheduleDetail = schedule.scheduleDetail
            val attributes = scheduleDetail.attributes
            return ScheduleUseCaseResult(
                scheduleCode = schedule.scheduleCode.value,
                targetYearAndMonth = schedule.targetYearAndMonth.value,
                attributes = attributes?.value,
                resourceAttributesResult = ResourceAttributesResult.of(schedule.resourceAttributes))
        }
    }
}
