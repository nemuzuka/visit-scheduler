package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleRepository
import net.jp.vss.visitscheduler.domain.schools.School
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
/**
 * Implements {@link ListSchoolScheduleUseCase}
 *
 * @property schoolScheduleRepo ListSchoolSchedule のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class ListSchoolScheduleUseCaseImpl(
    private val schoolScheduleRepo: SchoolScheduleRepository
) : ListSchoolScheduleUseCase {
    override fun getSchoolSchedule(schoolCode: String, targetYearAndMonth: String): List<SchoolScheduleUseCaseResult> {
        return schoolScheduleRepo.getSchoolSchedules(listOf(School.SchoolCode(schoolCode)),
            Schedule.TargetYearAndMonth(targetYearAndMonth))
            .map { SchoolScheduleUseCaseResult.of(it) }.toList()
    }
}
