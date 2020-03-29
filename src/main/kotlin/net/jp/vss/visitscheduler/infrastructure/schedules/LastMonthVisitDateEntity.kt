package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import java.util.UUID
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table

/**
 * last_month_visit_dates Entity.
 *
 * @property lastMonthVisitDateId 先月の最終訪問日識別子
 * @property schoolId 学校識別子
 * @property lastMonthVisitDate 先月の最終訪問日
 * @property schoolCode 学校コード
 */
@Entity(immutable = true)
@Table(name = "last_month_visit_dates")
data class LastMonthVisitDateEntity(
    @Id
    @Column(name = "last_month_visit_date_id")
    val lastMonthVisitDateId: String,

    @Column(name = "school_id")
    val schoolId: String,

    @Column(name = "target_year_and_month")
    val targetYearAndMonth: String,

    @Column(name = "last_month_visit_date")
    val lastMonthVisitDate: LocalDate?,

    /**
     * school_code.
     *
     * <p>SELECT の時のみ設定する想定.</p>
     */
    @Column(name = "school_code", insertable = false, updatable = false)
    val schoolCode: String?
) {
    companion object {
        fun of(
            schoolId: School.SchoolId,
            targetYearAndMonth: Schedule.TargetYearAndMonth,
            lastMonthVisitDate: Schedule.ScheduleDate?
        ): LastMonthVisitDateEntity {
            return LastMonthVisitDateEntity(
                lastMonthVisitDateId = UUID.randomUUID().toString(),
                schoolId = schoolId.value,
                targetYearAndMonth = targetYearAndMonth.value,
                lastMonthVisitDate = lastMonthVisitDate?.date,
                schoolCode = null
            )
        }
    }
}
