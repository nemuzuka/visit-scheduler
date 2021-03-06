package net.jp.vss.visitscheduler.usecase.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import net.jp.vss.visitscheduler.domain.schedules.ScheduleDetail
import net.jp.vss.visitscheduler.usecase.schools.SchoolUseCaseResult

/**
 * ScheduleDetailUseCase の結果.
 *
 * JSON 化することを想定しています。
 * @property scheduleUseCaseResult ScheduleUseCase の結果
 * @property privateScheduleUseCaseResults PrivateScheduleUseCase の結果
 * @property schoolWithSchedules スケジュールに紐づく学校と学校固有のスケジュール
 */
data class ScheduleDetailUseCaseResult(
    @field:JsonUnwrapped
    val scheduleUseCaseResult: ScheduleUseCaseResult,

    @field:JsonProperty("private_schedules")
    val privateScheduleUseCaseResults: List<PrivateScheduleUseCaseResult>,

    @field:JsonUnwrapped
    val schoolWithSchedules: SchoolWithSchedulesUseCaseResult,

    @field:JsonUnwrapped
    val calculateUseCaseResult: CalculateUseCaseResult
) {

    companion object {
        fun of(scheduleDetail: ScheduleDetail): ScheduleDetailUseCaseResult {
            val scheduleUseCaseResult = ScheduleUseCaseResult.of(scheduleDetail.schedule)
            val privateScheduleUseCaseResults = scheduleDetail.privateSchedules.privateSchedules.map {
                PrivateScheduleUseCaseResult.of(it)
            }.toList()
            val schoolWithSchedules = SchoolWithSchedulesUseCaseResult.of(scheduleDetail.schoolWithSchedules)
            val calculateUseCaseResult = CalculateUseCaseResult.of(scheduleDetail.visitSchedules)
            return ScheduleDetailUseCaseResult(scheduleUseCaseResult,
                privateScheduleUseCaseResults, schoolWithSchedules, calculateUseCaseResult)
        }
    }

    /**
     * スケジュールに紐づく学校と学校固有のスケジュール.
     *
     * @property schoolWithSchedules スケジュールに紐づく学校と学校固有のスケジュール要素リスト
     */
    data class SchoolWithSchedulesUseCaseResult(
        @field:JsonProperty("school_with_schedules")
        val schoolWithSchedules: List<SchoolWithScheduleUseCaseResult>
    ) {

        companion object {
            fun of(schoolWithSchedules: ScheduleDetail.SchoolWithSchedules): SchoolWithSchedulesUseCaseResult =
                SchoolWithSchedulesUseCaseResult(
                    schoolWithSchedules.schoolWithSchedules.map { SchoolWithScheduleUseCaseResult.of(it) }.toList())
        }
    }

    data class SchoolWithScheduleUseCaseResult(
        @field:JsonProperty("school")
        val school: SchoolUseCaseResult,

        @field:JsonProperty("calculation_target")
        val calculationTarget: Boolean,

        @field:JsonUnwrapped
        val schoolSchedule: SchoolScheduleUseCaseResult
    ) {

        companion object {
            fun of(schoolWithSchedule: ScheduleDetail.SchoolWithSchedule): SchoolWithScheduleUseCaseResult {
                return SchoolWithScheduleUseCaseResult(
                    SchoolUseCaseResult.of(schoolWithSchedule.school),
                    schoolWithSchedule.calculationTarget,
                    SchoolScheduleUseCaseResult.of(schoolSchedules = schoolWithSchedule.schedules ?: listOf(),
                        lastMonthVisitDate = schoolWithSchedule.lastMonthVisitDate)
                )
            }
        }
    }
}
