package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.Version

/**
 * Schedule Entity.
 *
 * @property scheduleId スケジュール識別子
 * @property scheduleCode スケジュールコード
 * @property userId ユーザ識別子
 * @property targetStartDate 対象年月日(日付は1日固定)
 * @property attributes 付帯情報
 * @property createUserCode 生成ユーザコード
 * @property createAt 生成日時
 * @property lastUpdateUserCode 最終更新ユーザコード
 * @property lastUpdateAt 最終更新日時
 * @property versionNo バージョン
 */
@Entity(immutable = true)
@Table(name = "schedules")
data class ScheduleEntity(
    @Id
    @Column(name = "schedule_id")
    val scheduleId: String,

    @Column(name = "schedule_code")
    val scheduleCode: String,

    @Column(name = "user_id")
    val userId: String,

    @Column(name = "target_start_date")
    val targetStartDate: LocalDate,

    @Column(name = "attributes")
    val attributes: String?,

    @Column(name = "create_user_code")
    val createUserCode: String,

    @Column(name = "create_at")
    val createAt: Long,

    @Column(name = "last_update_user_code")
    val lastUpdateUserCode: String,

    @Column(name = "last_update_at")
    val lastUpdateAt: Long,

    @Column(name = "version_no")
    @Version
    val versionNo: Long,

    /**
     * user_code.
     *
     * <p>SELECT の時のみ設定する想定.</p>
     */
    @Column(name = "user_code", insertable = false, updatable = false)
    val userCode: String?
) {
    companion object {
        fun fromSchedule(schedule: Schedule, scheduleId: String?, userId: User.UserId): ScheduleEntity {
            val scheduleDetail = schedule.scheduleDetail
            val resourceAttributes = schedule.resourceAttributes
            return ScheduleEntity(
                scheduleId = scheduleId ?: UUID.randomUUID().toString(),
                scheduleCode = schedule.scheduleCode.value,
                userId = userId.value,
                targetStartDate = YearMonth.parse(schedule.targetYearAndMonth.value).atDay(1),
                attributes = scheduleDetail.attributes?.value,
                createUserCode = resourceAttributes.createUserCode,
                createAt = resourceAttributes.createAt,
                lastUpdateUserCode = resourceAttributes.lastUpdateUserCode,
                lastUpdateAt = resourceAttributes.lastUpdateAt,
                versionNo = resourceAttributes.version,
                userCode = null
            )
        }
    }

    fun toSchedule(): Schedule {
        val scheduleCode = Schedule.ScheduleCode(scheduleCode)
        val userCode = User.UserCode(userCode!!)
        val targetYearAndMonth = Schedule.TargetYearAndMonth(targetStartDate.format(
            DateTimeFormatter.ofPattern("yyyy-MM")))
        val scheduleDetail = Schedule.ScheduleDetail(attributes = Attributes.of(attributes))
        val resourceAttributes = ResourceAttributes(createUserCode = createUserCode,
            createAt = createAt,
            lastUpdateUserCode = lastUpdateUserCode,
            lastUpdateAt = lastUpdateAt,
            version = versionNo)
        return Schedule(scheduleCode = scheduleCode, userCode = userCode, targetYearAndMonth = targetYearAndMonth,
            scheduleDetail = scheduleDetail, resourceAttributes = resourceAttributes)
    }
}
