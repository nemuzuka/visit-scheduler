package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import java.util.UUID
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedules
import net.jp.vss.visitscheduler.domain.schools.School
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table

@Entity(immutable = true)
@Table(name = "visit_schedules")
data class VisitSchedulesEntity(

    @Id
    @Column(name = "visit_schedule_id")
    val visitScheduleId: String,

    @Column(name = "school_id")
    val schoolId: String,

    @Column(name = "visit_date")
    val visitDate: LocalDate,

    /**
     * school_code.
     *
     * <p>SELECT の時のみ設定する想定.</p>
     */
    @Column(name = "school_code", insertable = false, updatable = false)
    val schoolCode: String?
) {

    companion object {
        fun fromVisitSchedules(visitSchedules: VisitSchedules, schoolIdMap: Map<School.SchoolCode, School.SchoolId>) =
            visitSchedules.visitSchedules.map { visitSchedule ->
                VisitSchedulesEntity(
                    visitScheduleId = UUID.randomUUID().toString(),
                    schoolId = schoolIdMap.getValue(visitSchedule.schoolCode).value,
                    visitDate = visitSchedule.visitDate.date,
                    schoolCode = null)
            }.toList()

        fun toVisitSchedules(visitSchedulesEntities: List<VisitSchedulesEntity>): VisitSchedules =
            VisitSchedules(
                visitSchedulesEntities.map {
                    VisitSchedules.VisitSchedule(Schedule.ScheduleDate(it.visitDate),
                        School.SchoolCode(it.schoolCode!!)) }
            )
    }
}
