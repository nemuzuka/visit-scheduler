package net.jp.vss.visitscheduler.infrastructure.schedules

import com.google.common.annotations.VisibleForTesting
import kotlin.streams.toList
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.schedules.PrivateSchedule
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.ScheduleDetail
import net.jp.vss.visitscheduler.domain.schedules.ScheduleDetailRepository
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedules
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolDao
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolEntity
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする ScheduleDetailRepository の実装
 */
@Repository
class JdbcScheduleDetailRepository(
    private val scheduleDao: ScheduleDao,
    private val privateScheduleRepo: JdbcPrivateScheduleRepository,
    private val schoolScheduleRepository: JdbcSchoolScheduleRepository,
    private val scheduleSchoolConnectionDao: ScheduleSchoolConnectionDao,
    private val schoolDao: SchoolDao,
    private val visitSchedulesRepo: JdbcVisitSchedulesRepository
) : ScheduleDetailRepository {
    override fun getScheduleDetail(scheduleCode: Schedule.ScheduleCode, userCode: User.UserCode): ScheduleDetail {

        val scheduleEntity = scheduleDao.findByScheduleCodeAndUserCode(
            scheduleCode.value, userCode.value, SelectOptions.get())
            ?: throw NotFoundException("Schedule(${scheduleCode.value}) は存在しません")
        val schedule = scheduleEntity.toSchedule()
        val privateSchedules = privateScheduleRepo.getPrivateSchedules(userCode, schedule.targetYearAndMonth)

        // user に紐づく計算対象の school 一覧を取得する
        val schools = schoolDao.findAll(userCode = userCode.value)
        val schoolCodes = schools.map { School.SchoolCode(it.schoolCode) }.toList()

        // schedule_code が合致する schedule_school_connections を取得する
        val scheduleSchoolConnections = scheduleSchoolConnectionDao.findByScheduleCode(scheduleCode.value)

        // school 毎のスケジュールを取得する
        val schoolSchedules = schoolScheduleRepository.getSchoolSchedules(schoolCodes, schedule.targetYearAndMonth)

        // school の訪問スケジュールを取得する
        val visitSchedules = visitSchedulesRepo.getVisitSchedules(schoolCodes, schedule.targetYearAndMonth)

        return buildScheduleDetail(schedule, privateSchedules, schools, scheduleSchoolConnections, schoolSchedules, visitSchedules)
    }

    @VisibleForTesting
    internal fun buildScheduleDetail(
        schedule: Schedule,
        privateSchedules: List<PrivateSchedule>,
        schools: List<SchoolEntity>,
        scheduleSchoolConnections: List<ScheduleSchoolConnectionEntity>,
        schoolSchedules: List<SchoolSchedule>,
        visitSchedules: VisitSchedules
    ): ScheduleDetail {

        val schoolScheduleMap = schoolSchedules.groupBy { it.schoolCode }
        val scheduleSchoolConnectionMap = scheduleSchoolConnections.map { it.schoolId to it }.toMap()

        val schoolWithSchedules = schools.stream().map {
            val school = it.toSchool()
            val calculationTarget = scheduleSchoolConnectionMap[it.schoolId]?.calculationTarget ?: false
            val schedules = schoolScheduleMap[school.schoolCode]
            ScheduleDetail.SchoolWithSchedule(school, calculationTarget, schedules)
        }.toList()
        val schoolWithScheduleMap = schoolWithSchedules.map { it.school.schoolId to it }.toMap()
        val sortedSchoolIds = scheduleSchoolConnections.map { School.SchoolId(it.schoolId) }.toList()
        val sortedSchoolWithSchedules = sortedSchoolIds.map { schoolWithScheduleMap[it] ?: error("Invalid data") }

        val allSchoolIds = schools.map { School.SchoolId(it.schoolId) }.toList()
        val notTargetSchoolCodes = allSchoolIds.subtract(sortedSchoolIds)
        if (notTargetSchoolCodes.isEmpty()) {
            // 全ての学校が含まれていた場合、その時点で終了
            return ScheduleDetail(schedule, ScheduleDetail.PrivateSchedules(privateSchedules),
                ScheduleDetail.SchoolWithSchedules(sortedSchoolWithSchedules),
                visitSchedules)
        }

        // 未設定の学校を追加する
        val addSchoolWithSchedules = notTargetSchoolCodes.map { schoolWithScheduleMap[it] ?: error("Invalid data") }
        return ScheduleDetail(schedule, ScheduleDetail.PrivateSchedules(privateSchedules),
            ScheduleDetail.SchoolWithSchedules(sortedSchoolWithSchedules + addSchoolWithSchedules),
            visitSchedules)
    }
}
