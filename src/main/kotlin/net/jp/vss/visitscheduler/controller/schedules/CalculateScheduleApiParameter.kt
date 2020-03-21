package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.YearMonth
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule.Priority
import net.jp.vss.visitscheduler.domain.schedules.calculate.SchoolSchedule
import net.jp.vss.visitscheduler.domain.schedules.calculate.VisitRules
import net.jp.vss.visitscheduler.domain.schedules.calculate.WorkerSchedule
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.usecase.schedules.CalculateUseCaseParameter

/**
 * 訪問スケジュール計算パラメータ
 *
 * @property targetYearAndMonth 対象年月(yyyy-MM)
 * @property workerExclusionDates 勤務者のスケジュール(除外日を指定)
 * @property schoolResuestedSchedules 学校の希望スケジュールリスト
 * @property requestVisitRules 訪問ルール
 * @property tryCount 試行回数
 */
data class CalculateScheduleApiParameter(
    @field:NotNull
    @field:Pattern(regexp = "^[0-9]{4}-[0-9]{2}\$")
    @field:JsonProperty("target_year_and_month")
    val targetYearAndMonth: String? = null,

    @field:NotNull
    @field:JsonProperty("worker_exclusion_dates")
    @field:Valid
    val workerExclusionDates: List<TargetDay>? = null,

    @field:NotNull
    @field:JsonProperty("school_resuested_schedules")
    @field:Valid
    val schoolResuestedSchedules: List<SchoolResuestedSchedule>? = null,

    @field:JsonProperty("request_visit_rules")
    @field:Valid
    val requestVisitRules: RequestVisitRules? = null,

    @field:JsonProperty("try_count")
    var tryCount: Int? = 3
) {

    /**
     * 対象日
     *
     * @property day 対象日
     */
    data class TargetDay(
        @field:NotNull
        @field:Min(1)
        @field:Max(31)
        @field:JsonProperty("day")
        val day: Int? = null
    )

    /**
     * 学校の希望スケジュール
     *
     * @property schoolCode 学校コード
     * @property lastMonthVisitDay 先月の最終訪問日
     * @property targetAndPriproties 対象日と優先度リスト
     */
    data class SchoolResuestedSchedule(
        @field:NotNull
        @field:Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        @field:JsonProperty("school_code")
        val schoolCode: String? = null,

        @field:Min(1)
        @field:Max(31)
        @field:JsonProperty("last_month_visit_day")
        val lastMonthVisitDay: Int? = null,

        @field:NotNull
        @field:JsonProperty("day_with_priproties")
        @field:Valid
        val targetAndPriproties: List<DayWithPriority>? = null
    )

    /**
     * 対象日と優先度.
     *
     * @property day 対象日
     * @property priority 優先度
     */
    data class DayWithPriority(
        @field:NotNull
        @field:Min(1)
        @field:Max(31)
        @field:JsonProperty("day")
        val day: Int? = null,

        @field:NotNull
        @field:JsonProperty("priority")
        val priority: Priority? = null
    )

    /**
     * 訪問ルール.
     *
     * @property daysToWaitSinceLastVisit 先月の最終訪問日から何日経過するか
     * @property daysToWaitSinceVisitForTargetMonth 2回目の訪問日は1回目の訪問日から何日経過するか
     */
    data class RequestVisitRules(
        @field:NotNull
        @field:Min(1)
        @field:Max(31)
        @field:JsonProperty("days_to_wait_since_last_visit")
        val daysToWaitSinceLastVisit: Int? = null,

        @field:NotNull
        @field:Min(1)
        @field:Max(31)
        @field:JsonProperty("days_to_wait_since_visit_for_target_month")
        val daysToWaitSinceVisitForTargetMonth: Int? = null
    )

    /**
     * パラメータ作成.
     *
     * @return パラメータ
     */
    fun toParameter(): CalculateUseCaseParameter {
        val targetYearAndMonth = Schedule.TargetYearAndMonth(targetYearAndMonth!!)
        val workerSchedule = WorkerSchedule(
            workerExclusionDates!!.map { Schedule.ScheduleDate.of(targetYearAndMonth, it.day!!) }.toList())
        val visitRules = if (requestVisitRules == null) VisitRules(7, 14)
        else VisitRules(requestVisitRules.daysToWaitSinceLastVisit!!,
            requestVisitRules.daysToWaitSinceVisitForTargetMonth!!)
        val schoolSchedules = schoolResuestedSchedules!!.map {
            val schoolCode = School.SchoolCode(it.schoolCode!!)
            val targetAndPriproties = it.targetAndPriproties!!
            val exclusionDates = targetAndPriproties
                .filter { targetAndPriproty -> targetAndPriproty.priority == Priority.DONT_COME }
                .map { targetAndPriproty -> Schedule.ScheduleDate.of(targetYearAndMonth, targetAndPriproty.day!!) }
                .toList()
            val forceVisitDates = targetAndPriproties
                .filter { targetAndPriproty -> targetAndPriproty.priority == Priority.ABSOLUTELY }
                .map { targetAndPriproty -> Schedule.ScheduleDate.of(targetYearAndMonth, targetAndPriproty.day!!) }
                .toList()
            val priorityVisitDates = targetAndPriproties
                .filter { targetAndPriproty -> targetAndPriproty.priority == Priority.POSSIBLE }
                .map { targetAndPriproty -> Schedule.ScheduleDate.of(targetYearAndMonth, targetAndPriproty.day!!) }
                .toList()

            val lastMonthVisitDay = it.lastMonthVisitDay
            val lastMonthVisitDate = if (lastMonthVisitDay != null) {
                val targetYearAndMonthBeforeValue = YearMonth.parse(targetYearAndMonth.value).minusMonths(1)
                Schedule.ScheduleDate(targetYearAndMonthBeforeValue.atDay(lastMonthVisitDay))
            } else null
            SchoolSchedule(schoolCode, exclusionDates, lastMonthVisitDate, forceVisitDates, priorityVisitDates)
        }.toList()

        return CalculateUseCaseParameter(
            targetYearAndMonth,
            workerSchedule,
            schoolSchedules,
            visitRules,
            tryCount ?: let { 3 })
    }
}
