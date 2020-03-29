package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.ScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * UpdateSchoolCodeAndCalculationTargetUseCase の実装クラス.
 *
 * @property scheduleRepo Schedule のリポジトリ
 */
@Service
@Transactional
class UpdateSchoolCodeAndCalculationTargetUseCaseImpl(
    private val scheduleRepo: ScheduleRepository
) : UpdateSchoolCodeAndCalculationTargetUseCase {
    override fun update(parameter: UpdateSchoolCodeAndCalculationTargetUseCaseParameter) {
        scheduleRepo.updateSchoolCodeAndCalculationTargets(parameter.scheduleCode, parameter.userCode,
            SchoolCodeAndCalculationTarget.toSchoolCodeAndCalculationTargets(parameter.schoolCodeAndCalculationTargets))
    }
}
