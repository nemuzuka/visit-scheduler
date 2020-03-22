package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedulesRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * SaveVisitScheduleUseCase の実装クラス
 *
 * @property visitSchedulesRepo VisitSchedules の Repository
 */
@Service
@Transactional
class SaveVisitScheduleUseCaseImpl(
    private val visitSchedulesRepo: VisitSchedulesRepository
) : SaveVisitScheduleUseCase {
    override fun saveVisitSchedules(parameter: SaveVisitScheduleUseCaseParameter) {
        visitSchedulesRepo.save(parameter.userCode,
            Schedule.TargetYearAndMonth(parameter.targetDateString), parameter.toVisitSchedules())
    }
}
