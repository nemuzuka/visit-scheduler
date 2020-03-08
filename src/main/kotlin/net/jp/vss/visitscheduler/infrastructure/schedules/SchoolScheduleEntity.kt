package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import java.util.UUID
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule
import net.jp.vss.visitscheduler.domain.schools.School
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.Version

/**
 * school_schedules Entity.
 *
 * @property schoolScheduleId 学校スケジュール識別子
 * @property schoolId 学校識別子
 * @property targetDate 対象日付
 * @property memo メモ
 * @property priority 優先度
 * @property createUserCode 生成ユーザコード
 * @property createAt 生成日時
 * @property lastUpdateUserCode 最終更新ユーザコード
 * @property lastUpdateAt 最終更新日時
 * @property versionNo バージョン
 */
@Entity(immutable = true)
@Table(name = "school_schedules")
data class SchoolScheduleEntity(
    @Id
    @Column(name = "school_schedule_id")
    val schoolScheduleId: String,

    @Column(name = "school_id")
    val schoolId: String,

    @Column(name = "target_date")
    val targetDate: LocalDate,

    @Column(name = "memo")
    val memo: String?,

    @Column(name = "priority")
    val priority: String,

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
     * school_code.
     *
     * <p>SELECT の時のみ設定する想定.</p>
     */
    @Column(name = "school_code", insertable = false, updatable = false)
    val schoolCode: String?

) {
    companion object {
        fun fromSchoolSchedule(schooleSchedule: SchoolSchedule, schoolId: School.SchoolId): SchoolScheduleEntity {
            val schoolScheduleDetail = schooleSchedule.schoolScheduleDetail
            val resourceAttributes = schooleSchedule.resourceAttributes
            return SchoolScheduleEntity(
                schoolScheduleId = UUID.randomUUID().toString(),
                schoolId = schoolId.value,
                targetDate = schooleSchedule.targetDate.date,
                memo = schoolScheduleDetail.memo,
                priority = schoolScheduleDetail.priority.name,
                createUserCode = resourceAttributes.createUserCode,
                createAt = resourceAttributes.createAt,
                lastUpdateUserCode = resourceAttributes.lastUpdateUserCode,
                lastUpdateAt = resourceAttributes.lastUpdateAt,
                versionNo = resourceAttributes.version,
                schoolCode = null
            )
        }
    }

    fun toSchoolSchedule(): SchoolSchedule {
        val resourceAttributes = ResourceAttributes(createUserCode = createUserCode,
            createAt = createAt,
            lastUpdateUserCode = lastUpdateUserCode,
            lastUpdateAt = lastUpdateAt,
            version = versionNo)
        return SchoolSchedule(
            targetDate = Schedule.ScheduleDate(targetDate),
            schoolCode = School.SchoolCode(schoolCode!!),
            schoolScheduleDetail =
            SchoolSchedule.SchoolScheduleDetail(memo = memo, priority = SchoolSchedule.Priority.valueOf(priority)),
            resourceAttributes = resourceAttributes)
    }
}
