package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.calculate.SchoolSchedule
import net.jp.vss.visitscheduler.domain.schedules.calculate.VisitRules
import net.jp.vss.visitscheduler.domain.schedules.calculate.WorkerSchedule

/**
 * スケジュール算出パラメータ.
 *
 * @property targetYearAndMonth 対象年月
 * @property workerSchedule 勤務者のスケジュール
 * @property schoolSchedules 学校のスケジュールリスト
 * @property visitRules 訪問設定
 * @property tryCount 再計算回数
 */
data class CalculateUseCaseParameter(
    val targetYearAndMonth: Schedule.TargetYearAndMonth,
    val workerSchedule: WorkerSchedule,
    val schoolSchedules: List<SchoolSchedule>,
    val visitRules: VisitRules,
    val tryCount: Int = 3
)
