package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.calculate.ScheduleResult
import net.jp.vss.visitscheduler.domain.schedules.calculate.VisitScheduleCalculator
import org.springframework.stereotype.Service

/**
 * CalculateUseCase の実装.
 */
@Service
class CalculateUseCaseImpl : CalculateUseCase {
    override fun calculateSchedule(parameter: CalculateUseCaseParameter): CalculateUseCaseResult {
        for (delta in 0..parameter.tryCount) {
            val targetVisitRule = parameter.visitRules.updateDays(-delta)
            val calculator = VisitScheduleCalculator(parameter.targetYearAndMonth,
                parameter.workerSchedule, parameter.schoolSchedules, targetVisitRule)
            val result = ScheduleResult.of(calculator.calculate(), targetVisitRule)
            if (result.isCompleted()) {
                return CalculateUseCaseResult.of(result.details)
            }
        }
        throw CalculateUseCase.CalculateException("スケジュールを計算できませんでした。")
    }
}
