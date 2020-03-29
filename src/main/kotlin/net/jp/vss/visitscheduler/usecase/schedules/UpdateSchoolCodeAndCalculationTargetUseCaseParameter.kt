package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User

/**
 * CreateScheduleUseCase のパラメータ.
 *
 * @property scheduleCode スケジュールコード
 * @property userCode ユーザコード
 * @property schoolCodeAndCalculationTargets スケジュールに関連する school_code と 計算対象の組み合わせリスト
 */
data class UpdateSchoolCodeAndCalculationTargetUseCaseParameter(
    val scheduleCode: Schedule.ScheduleCode,
    val userCode: User.UserCode,
    val schoolCodeAndCalculationTargets: List<SchoolCodeAndCalculationTarget>
)
