package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleRepository
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link SaveSchoolScheduleUseCase}
 *
 * @property schoolScheduleRepo SchoolSchedule のリポジトリ
 */
@Service
@Transactional
class SaveSchoolScheduleUseCaseImpl(
    private val schoolScheduleRepo: SchoolScheduleRepository
) : SaveSchoolScheduleUseCase {
    override fun saveSchoolSchedule(parameter: SaveSchoolScheduleUseCaseParameter) {
        schoolScheduleRepo.save(User.UserCode(parameter.createUserCode),
            School.SchoolCode(parameter.schoolCode),
            Schedule.TargetYearAndMonth(parameter.targetDateString),
            parameter.toSchoolSchedules(),
            parameter.lastMonthVisitDate)
    }
}
