package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.PrivateScheduleRepository
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
/**
 * Implements {@link ListPrivateScheduleUseCase}
 *
 * @property privateScheduleRepo PrivateSchedule のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class ListPrivateScheduleUseCaseImpl(
    private val privateScheduleRepo: PrivateScheduleRepository
) : ListPrivateScheduleUseCase {
    override fun getPrivateSchedule(userCode: String, targetYearAndMonth: String): List<PrivateScheduleUseCaseResult> {
        return privateScheduleRepo.getPrivateSchedules(User.UserCode(userCode),
            Schedule.TargetYearAndMonth(targetYearAndMonth))
            .map { PrivateScheduleUseCaseResult.of(it) }.toList()
    }
}
