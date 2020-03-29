package net.jp.vss.visitscheduler.usecase.schedules

import java.time.YearMonth
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleRepository
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedulesRepository
import net.jp.vss.visitscheduler.domain.schools.School
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link ListSchoolScheduleUseCase}
 *
 * @property schoolScheduleRepo ListSchoolSchedule のリポジトリ
 * @property visitSchedulesRepo 訪問日のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class ListSchoolScheduleUseCaseImpl(
    private val schoolScheduleRepo: SchoolScheduleRepository,
    private val visitSchedulesRepo: VisitSchedulesRepository
) : ListSchoolScheduleUseCase {
    override fun getSchoolSchedule(schoolCodeValue: String, targetYearAndMonth: String): SchoolScheduleUseCaseResult {

        val schoolCode = School.SchoolCode(schoolCodeValue)
        val result = schoolScheduleRepo.getSchoolSchedules(listOf(schoolCode),
            Schedule.TargetYearAndMonth(targetYearAndMonth))

        val lastMonthVisitDate = if (result.second.containsKey(schoolCode)) {
            result.second[schoolCode]
        } else {
            // Map に存在しなければ先月の最終訪問日を初期値として設定
            val lastMonth = YearMonth.parse(targetYearAndMonth).minusMonths(1)
            val resultOfLastMonth = visitSchedulesRepo.getVisitSchedules(listOf(schoolCode),
                Schedule.TargetYearAndMonth(lastMonth.toString())).visitSchedules
            if (resultOfLastMonth.isEmpty()) null else {
                resultOfLastMonth.last().visitDate
            }
        }
        return SchoolScheduleUseCaseResult.of(result.first, lastMonthVisitDate)
    }
}
