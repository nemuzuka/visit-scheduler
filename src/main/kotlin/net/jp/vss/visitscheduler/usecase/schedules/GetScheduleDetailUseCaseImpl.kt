package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.ScheduleDetailRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link GetScheduleDetailUseCase}
 *
 * @property scheduleDetailRepo ScheduleDetail のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class GetScheduleDetailUseCaseImpl(
    private val scheduleDetailRepo: ScheduleDetailRepository
) : GetScheduleDetailUseCase {
    override fun getScheduleDetail(scheduleCode: String, userCode: String) =
        ScheduleDetailUseCaseResult.of(
            scheduleDetailRepo.getScheduleDetail(Schedule.ScheduleCode(scheduleCode), User.UserCode(userCode)))
}
