package net.jp.vss.visitscheduler.infrastructure.schedules

import java.util.UUID
import net.jp.vss.visitscheduler.domain.schools.School
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table

/**
 * ScheduleSchoolConnection Entity.
 *
 * @property scheduleSchoolConnectionId schedule_school_connection_id
 * @property scheduleId schedule_id
 * @property schoolId school_id
 * @property connectionIndex 並び順
 * @property calculationTarget 計算対象の場合、true
 */
@Entity(immutable = true)
@Table(name = "schedule_school_connections")
data class ScheduleSchoolConnectionEntity(
    @Id
    @Column(name = "schedule_school_connection_id")
    val scheduleSchoolConnectionId: String,

    @Column(name = "schedule_id")
    val scheduleId: String,

    @Column(name = "school_id")
    val schoolId: String,

    @Column(name = "connection_index")
    val connectionIndex: Int,

    @Column(name = "calculation_target")
    val calculationTarget: Boolean

) {

    companion object {
        fun buildForCreate(
            scheduleId: String,
            schoolId: School.SchoolId,
            index: Int,
            calculationTarget: Boolean
        ): ScheduleSchoolConnectionEntity {
            return ScheduleSchoolConnectionEntity(
                scheduleSchoolConnectionId = UUID.randomUUID().toString(),
                scheduleId = scheduleId,
                schoolId = schoolId.value,
                connectionIndex = index,
                calculationTarget = calculationTarget)
        }
    }
}
