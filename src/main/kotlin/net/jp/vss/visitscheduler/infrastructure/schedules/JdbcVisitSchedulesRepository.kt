package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.YearMonth
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedules
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedulesRepository
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolDao
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする VisitSchedulesRepository の実装.
 */
@Repository
class JdbcVisitSchedulesRepository(
    private val schoolDao: SchoolDao,
    private val visitSchedulesDao: VisitSchedulesDao
) : VisitSchedulesRepository {
    override fun save(
        userCode: User.UserCode,
        targetYearAndMonth: Schedule.TargetYearAndMonth,
        visitSchedules: VisitSchedules
    ) {

        val schoolEntities = schoolDao.findAll(userCode.value)
        if (schoolEntities.isEmpty()) {
            return
        }
        val schoolIds = schoolEntities.map { it.schoolId }.toList()
        val yearMonth = YearMonth.parse(targetYearAndMonth.value)
        val from = yearMonth.atDay(1)
        val to = yearMonth.atDay(from.lengthOfMonth())
        visitSchedulesDao.delete(schoolIds, from, to)

        val schoolIdMap = schoolEntities.map {
            School.SchoolCode(it.schoolCode) to School.SchoolId(it.schoolId) }.toMap()
        VisitSchedulesEntity.fromVisitSchedules(visitSchedules, schoolIdMap).forEach { visitSchedulesDao.create(it) }
    }

    override fun getVisitSchedules(
        schoolCodes: List<School.SchoolCode>,
        targetYearAndMonth: Schedule.TargetYearAndMonth
    ): VisitSchedules {
        val yearMonth = YearMonth.parse(targetYearAndMonth.value)
        val from = yearMonth.atDay(1)
        val to = yearMonth.atDay(from.lengthOfMonth())
        val result = visitSchedulesDao.findBySchoolCodesAndTargetDate(
            schoolCodes.map { it.value }.toList(), from, to)
        return VisitSchedulesEntity.toVisitSchedules(result)
    }
}
