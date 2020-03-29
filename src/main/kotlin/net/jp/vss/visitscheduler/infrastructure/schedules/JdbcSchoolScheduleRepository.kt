package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.YearMonth
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleRepository
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolDao
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする SchoolScheduleRepository の実装.
 */
@Repository
class JdbcSchoolScheduleRepository(
    private val schoolScheduleDao: SchoolScheduleDao,
    private val schoolDao: SchoolDao,
    private val lastMonthVisitDateDao: LastMonthVisitDateDao
) : SchoolScheduleRepository {
    override fun save(
        userCode: User.UserCode,
        schoolCode: School.SchoolCode,
        targetYearAndMonth: Schedule.TargetYearAndMonth,
        schoolSchedules: List<SchoolSchedule>,
        lastMonthVisitDate: Schedule.ScheduleDate?
    ) {

        val userCodeValue = userCode.value
        val schoolCodeValue = schoolCode.value
        val schoolEntity = schoolDao.findBySchoolCodeAndUserCode(schoolCodeValue, userCodeValue, SelectOptions.get())
            ?: throw IllegalStateException("School(${schoolCode.value}) は存在しません")
        val schoolId = School.SchoolId(schoolEntity.schoolId)

        val yearMonth = YearMonth.parse(targetYearAndMonth.value)
        val from = yearMonth.atDay(1)
        val to = yearMonth.atDay(from.lengthOfMonth())

        schoolScheduleDao.delete(schoolCodeValue, from, to) // 1度削除
        schoolSchedules.forEach { schoolScheduleDao.create(SchoolScheduleEntity.fromSchoolSchedule(it, schoolId)) }

        lastMonthVisitDateDao.delete(schoolId.value, targetYearAndMonth.value)
        val lastMonthVisitDateEntity = LastMonthVisitDateEntity.of(schoolId, targetYearAndMonth, lastMonthVisitDate)
        lastMonthVisitDateDao.create(lastMonthVisitDateEntity)
    }

    override fun getSchoolSchedules(
        schoolCodes: List<School.SchoolCode>,
        targetYearAndMonth: Schedule.TargetYearAndMonth
    ): Pair<List<SchoolSchedule>, Map<School.SchoolCode, Schedule.ScheduleDate?>> {
        val yearMonth = YearMonth.parse(targetYearAndMonth.value)
        val from = yearMonth.atDay(1)
        val to = yearMonth.atDay(from.lengthOfMonth())

        val schoolCodeValues = schoolCodes.map { it.value }.toList()

        val schoolSchedules = schoolScheduleDao.findBySchoolCodesAndTargetDate(schoolCodeValues, from, to)
            .map { it.toSchoolSchedule() }.toList()
        val scheduleDateMap = lastMonthVisitDateDao.findBySchoolCodesAndTargetDate(
            schoolCodeValues, targetYearAndMonth.value)
            .map { School.SchoolCode(it.schoolCode!!) to it.lastMonthVisitDate?.let {
                lastMonthVisitDate -> Schedule.ScheduleDate(lastMonthVisitDate) } }
            .toMap()
        return schoolSchedules to scheduleDateMap
    }
}
