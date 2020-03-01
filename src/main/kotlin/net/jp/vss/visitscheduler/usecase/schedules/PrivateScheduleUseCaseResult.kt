package net.jp.vss.visitscheduler.usecase.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import net.jp.vss.visitscheduler.domain.schedules.PrivateSchedule

/**
 * PrivateScheduleUseCase の結果.
 *
 * JSON 化することを想定しています。
 * @property targetDay 対象日
 * @property memo メモ
 */
data class PrivateScheduleUseCaseResult(
    @field:JsonProperty("target_day")
    val targetDay: Int,
    @field:JsonProperty("memo")
    val memo: String?
) {
    companion object {
        /**
         * PrivateSchedule からのインスタンス生成.
         *
         * @param privateSchedule 対象 PrivateSchedule
         * @return 生成インスタンス
         */
        fun of(privateSchedule: PrivateSchedule): PrivateScheduleUseCaseResult {
            val privateScheduleDetail = privateSchedule.privateScheduleDetail
            return PrivateScheduleUseCaseResult(
                privateSchedule.targetDate.date.dayOfMonth,
                privateScheduleDetail.memo)
        }
    }
}
