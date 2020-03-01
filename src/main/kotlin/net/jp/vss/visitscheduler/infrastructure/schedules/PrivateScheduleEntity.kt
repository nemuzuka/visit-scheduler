package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import java.util.UUID
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.schedules.PrivateSchedule
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.Version

/**
 * private_schedules Entity.
 *
 * @property privateScheduleId 個人スケジュール識別子
 * @property userId ユーザ識別子
 * @property targetDate 対象日付
 * @property memo メモ
 * @property createUserCode 生成ユーザコード
 * @property createAt 生成日時
 * @property lastUpdateUserCode 最終更新ユーザコード
 * @property lastUpdateAt 最終更新日時
 * @property versionNo バージョン
 */
@Entity(immutable = true)
@Table(name = "private_schedules")
data class PrivateScheduleEntity(
    @Id
    @Column(name = "private_schedule_id")
    val privateScheduleId: String,

    @Column(name = "user_id")
    val userId: String,

    @Column(name = "target_date")
    val targetDate: LocalDate,

    @Column(name = "memo")
    val memo: String?,

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
    val versionNo: Long
) {
    companion object {
        fun fromPrivateSchedule(privateSchedule: PrivateSchedule, userId: User.UserId): PrivateScheduleEntity {
            val privateScheduleDetail = privateSchedule.privateScheduleDetail
            val resourceAttributes = privateSchedule.resourceAttributes
            return PrivateScheduleEntity(
                privateScheduleId = UUID.randomUUID().toString(),
                userId = userId.value,
                targetDate = privateSchedule.targetDate.date,
                memo = privateScheduleDetail.memo,
                createUserCode = resourceAttributes.createUserCode,
                createAt = resourceAttributes.createAt,
                lastUpdateUserCode = resourceAttributes.lastUpdateUserCode,
                lastUpdateAt = resourceAttributes.lastUpdateAt,
                versionNo = resourceAttributes.version
            )
        }
    }

    fun toPrivateSchedule(): PrivateSchedule {
        val resourceAttributes = ResourceAttributes(createUserCode = createUserCode,
            createAt = createAt,
            lastUpdateUserCode = lastUpdateUserCode,
            lastUpdateAt = lastUpdateAt,
            version = versionNo)
        return PrivateSchedule(
            targetDate = Schedule.ScheduleDate(targetDate),
            privateScheduleDetail = PrivateSchedule.PrivateScheduleDetail(memo = memo),
            resourceAttributes = resourceAttributes)
    }
}
