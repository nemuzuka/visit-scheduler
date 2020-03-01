package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.ScheduleRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link ListScheduleUseCase}
 *
 * @property scheduleRepo Schedule のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class ListScheduleUseCaseImpl(
    private val scheduleRepo: ScheduleRepository
) : ListScheduleUseCase {
    override fun allSchedules(userCode: String): List<ScheduleUseCaseResult> {
        return scheduleRepo.allSchedules(User.UserCode(userCode)).map { ScheduleUseCaseResult.of(it) }.toList()
    }
}
