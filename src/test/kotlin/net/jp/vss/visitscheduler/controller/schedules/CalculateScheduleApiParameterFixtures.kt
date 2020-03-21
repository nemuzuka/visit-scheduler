package net.jp.vss.visitscheduler.controller.schedules

import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule

/** CalculateScheduleApiParameter の Fixtures. */
class CalculateScheduleApiParameterFixtures {
    companion object {
        fun create(): CalculateScheduleApiParameter {
            val targetDay = CalculateScheduleApiParameter.TargetDay(1)
            val dayWithPriority = CalculateScheduleApiParameter.DayWithPriority(3, SchoolSchedule.Priority.ABSOLUTELY)
            val schoolResuestedSchedule = CalculateScheduleApiParameter.SchoolRequestedSchedule(
                "SCHOOL-001",
                28,
                listOf(dayWithPriority))
            return CalculateScheduleApiParameter("2020-01",
                listOf(targetDay),
                listOf(schoolResuestedSchedule),
                CalculateScheduleApiParameter.RequestVisitRules(15, 16),
            10)
        }
    }
}
