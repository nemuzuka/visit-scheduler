package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.PrivateScheduleRepository
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link SavePrivateScheduleUseCase}
 *
 * @property privateScheduleRepo PrivateSchedule のリポジトリ
 */
@Service
@Transactional
class SavePrivateScheduleUseCaseImpl(
    private val privateScheduleRepo: PrivateScheduleRepository
) : SavePrivateScheduleUseCase {
    override fun savePrivateSchedule(parameter: SavePrivateScheduleUseCaseParameter) {
        privateScheduleRepo.save(User.UserCode(parameter.createUserCode),
            Schedule.TargetYearAndMonth(parameter.targetDateString),
            parameter.toPrivateSchedules())
    }
}
