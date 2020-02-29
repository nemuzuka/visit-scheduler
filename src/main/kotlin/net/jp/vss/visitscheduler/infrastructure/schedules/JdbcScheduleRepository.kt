package net.jp.vss.visitscheduler.infrastructure.schedules

import kotlin.streams.toList
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.ScheduleRepository
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.schools.SchoolDao
import net.jp.vss.visitscheduler.infrastructure.users.UserDao
import org.seasar.doma.jdbc.SelectOptions
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする ScheduleRepository の実装.
 */
@Repository
class JdbcScheduleRepository(
    private val scheduleDao: ScheduleDao,
    private val scheduleSchoolConnectionDao: ScheduleSchoolConnectionDao,
    private val schoolDao: SchoolDao,
    private val userDao: UserDao
) : ScheduleRepository {
    companion object {
        private val log = LoggerFactory.getLogger(JdbcScheduleRepository::class.java)
    }

    override fun create(
        schedule: Schedule,
        schoolCodeAndCalculationTargets: Schedule.SchoolCodeAndCalculationTargets
    ): Schedule {
        val userCode = schedule.userCode
        val userEntity = userDao.findByUserCode(userCode.value, SelectOptions.get())
            ?: throw IllegalStateException("User(${userCode.value}) は存在しません")
        val scheduleEntity = ScheduleEntity.fromSchedule(schedule, null, User.UserId(userEntity.userId))

        try {
            scheduleDao.create(scheduleEntity)
        } catch (e: DuplicateKeyException) {
            log.info("Duplicate key: {}", e.message, e)
            val message = "Schedule(${schedule.scheduleCode.value}) は既に存在しています"
            throw DuplicateException(message)
        }
        createScheduleSchoolConnection(scheduleEntity.scheduleId,
            schoolCodeAndCalculationTargets.schoolCodeAndCalculationTargets, userCode)
        return getSchedule(schedule.scheduleCode, schedule.userCode)
    }

    private fun createScheduleSchoolConnection(
        scheduleId: String,
        schoolCodeAndCalculationTargets: List<Schedule.SchoolCodeAndCalculationTarget>,
        userCode: User.UserCode
    ) {
        val schoolCodeValues = schoolCodeAndCalculationTargets.stream().map { it.schoolCode.value }.toList()
        val schoolCodeIdMap = schoolDao.findBySchoolCodesAndUserCode(schoolCodeValues, userCode.value)
            .associateBy({ it.schoolCode }, { it.schoolId })
        schoolCodeAndCalculationTargets.forEachIndexed { index, schoolCodeAndCalculationTarget ->
            // school_code -> school_id に変換し、永続化
            val schoolCode = schoolCodeAndCalculationTarget.schoolCode.value
            val schoolId = schoolCodeIdMap[schoolCode]
                ?: throw IllegalStateException("School($schoolCode) は存在しません")
            val calculationTarget = schoolCodeAndCalculationTarget.calculationTarget
            val scheduleSchoolConnection =
                ScheduleSchoolConnectionEntity.buildForCreate(scheduleId, School.SchoolId(schoolId),
                    index, calculationTarget)
            scheduleSchoolConnectionDao.create(scheduleSchoolConnection)
        }
    }

    override fun getSchedule(scheduleCode: Schedule.ScheduleCode, userCode: User.UserCode): Schedule =
        getSchedule(scheduleCode, userCode, false)

    private fun getSchedule(scheduleCode: Schedule.ScheduleCode, userCode: User.UserCode, isLock: Boolean): Schedule =
        scheduleDao.findByScheduleCodeAndUserCode(scheduleCode.value, userCode.value,
            if (isLock) SelectOptions.get().forUpdate() else SelectOptions.get())?.toSchedule()
            ?: throw NotFoundException("Schedule(${scheduleCode.value}) は存在しません")

    override fun lockSchedule(scheduleCode: Schedule.ScheduleCode, userCode: User.UserCode): Schedule =
        getSchedule(scheduleCode, userCode, true)
}
