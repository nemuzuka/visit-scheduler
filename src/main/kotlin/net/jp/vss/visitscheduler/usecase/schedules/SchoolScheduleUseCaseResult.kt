package net.jp.vss.visitscheduler.usecase.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule

/**
 * SchoolScheduleUseCase の結果.
 *
 * JSON 化することを想定しています。
 * @property schoolSchedules 学校のスケジュールリスト
 * @property lastMonthVisitDate 先月の最終訪問日(yyyy-MM-dd 形式)
 */
data class SchoolScheduleUseCaseResult(
    @field:JsonProperty("school_schedules")
    val schoolSchedules: List<SchoolScheduleEntry>,
    @field:JsonProperty("last_month_visit_date")
    val lastMonthVisitDate: String?
) {
    companion object {
        /**
         * SchoolSchedule からのインスタンス生成.
         *
         * @param schoolSchedules 対象 SchoolSchedule リスト
         * @param lastMonthVisitDate 先月の最終訪問日
         * @return 生成インスタンス
         */
        fun of(
            schoolSchedules: List<SchoolSchedule>,
            lastMonthVisitDate: Schedule.ScheduleDate?
        ): SchoolScheduleUseCaseResult {
            val schoolScheduleEntries = schoolSchedules.map {
                val schoolScheduleDetail = it.schoolScheduleDetail
                SchoolScheduleEntry(
                    it.targetDate.date.dayOfMonth,
                    schoolScheduleDetail.memo,
                    schoolScheduleDetail.priority.name)
            }.toList()

            val lastMonthVisitDateValue = lastMonthVisitDate?.date?.toString()
            return SchoolScheduleUseCaseResult(schoolScheduleEntries, lastMonthVisitDateValue)
        }
    }

    /**
     * 学校のスケジュール.
     *
     * @property targetDay 対象日
     * @property memo メモ
     * @property priority 優先度
     */
    data class SchoolScheduleEntry(
        @field:JsonProperty("target_day")
        val targetDay: Int,
        @field:JsonProperty("memo")
        val memo: String?,
        @field:JsonProperty("priority")
        val priority: String
    )
}
