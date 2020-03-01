package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.YearMonth
import net.jp.vss.visitscheduler.domain.schedules.PrivateSchedule
import net.jp.vss.visitscheduler.domain.schedules.PrivateScheduleRepository
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.users.UserDao
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする PrivateScheduleRepository の実装.
 */
@Repository
class JdbcPrivateScheduleRepository(
    private val privateScheduleDao: PrivateScheduleDao,
    private val userDao: UserDao
) : PrivateScheduleRepository {
    override fun save(
        userCode: User.UserCode,
        targetYearAndMonth: Schedule.TargetYearAndMonth,
        privateSchedules: List<PrivateSchedule>
    ) {

        val userCodeValue = userCode.value
        val userEntity = userDao.findByUserCode(userCodeValue, SelectOptions.get())
            ?: throw IllegalStateException("User(${userCode.value}) は存在しません")
        val userId = User.UserId(userEntity.userId)

        val yearMonth = YearMonth.parse(targetYearAndMonth.value)
        val from = yearMonth.atDay(1)
        val to = yearMonth.atDay(from.lengthOfMonth())

        privateScheduleDao.delete(userCodeValue, from, to) // 1度削除

        privateSchedules.forEach { privateScheduleDao.create(PrivateScheduleEntity.fromPrivateSchedule(it, userId)) }
    }

    override fun getPrivateSchedules(
        userCode: User.UserCode,
        targetYearAndMonth: Schedule.TargetYearAndMonth
    ): List<PrivateSchedule> {
        val yearMonth = YearMonth.parse(targetYearAndMonth.value)
        val from = yearMonth.atDay(1)
        val to = yearMonth.atDay(from.lengthOfMonth())

        return privateScheduleDao.findByUserCodeAndTargetDate(userCode.value, from, to)
            .map { it.toPrivateSchedule() }.toList()
    }
}
