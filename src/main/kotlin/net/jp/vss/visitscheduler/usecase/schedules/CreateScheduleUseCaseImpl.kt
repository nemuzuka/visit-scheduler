package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.ScheduleRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link CreateScheduleUseCase}
 *
 * @property scheduleRepo Schedule のリポジトリ
 */
@Service
@Transactional
class CreateScheduleUseCaseImpl(
    private val scheduleRepo: ScheduleRepository
) : CreateScheduleUseCase {
    override fun createSchedule(parameter: CreateScheduleUseCaseParameter): ScheduleUseCaseResult {
        val schedule = Schedule.buildForCreate(scheduleCodeValue = parameter.scheduleCode,
            userCode = User.UserCode(parameter.createUserCode),
            targetDateString = parameter.targetDateString,
            attributeJsonString = parameter.attributes,
            createUserCode = parameter.createUserCode)
        return ScheduleUseCaseResult.of(scheduleRepo.create(schedule,
            SchoolCodeAndCalculationTarget.toSchoolCodeAndCalculationTargets(
                parameter.schoolCodeAndCalculationTargets)))
    }
}
