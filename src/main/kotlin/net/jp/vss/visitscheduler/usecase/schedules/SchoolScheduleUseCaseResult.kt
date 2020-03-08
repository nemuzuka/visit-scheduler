package net.jp.vss.visitscheduler.usecase.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule

/**
 * SchoolScheduleUseCase の結果.
 *
 * JSON 化することを想定しています。
 * @property targetDay 対象日
 * @property memo メモ
 * @property priority 優先度
 */
data class SchoolScheduleUseCaseResult(
    @field:JsonProperty("target_day")
    val targetDay: Int,
    @field:JsonProperty("memo")
    val memo: String?,
    @field:JsonProperty("priority")
    val priority: String
) {
    companion object {
        /**
         * SchoolSchedule からのインスタンス生成.
         *
         * @param schoolSchedule 対象 SchoolSchedule
         * @return 生成インスタンス
         */
        fun of(schoolSchedule: SchoolSchedule): SchoolScheduleUseCaseResult {
            val schoolScheduleDetail = schoolSchedule.schoolScheduleDetail
            return SchoolScheduleUseCaseResult(
                schoolSchedule.targetDate.date.dayOfMonth,
                schoolScheduleDetail.memo,
                schoolScheduleDetail.priority.name)
        }
    }
}
