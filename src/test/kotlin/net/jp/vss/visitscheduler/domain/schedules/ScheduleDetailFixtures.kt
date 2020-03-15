package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.schools.SchoolFixtures

/**
 * ScheduleDetail の Fixture.
 */
class ScheduleDetailFixtures {
    companion object {
        fun create(): ScheduleDetail {
            val schedule = ScheduleFixtures.create()
            val privateSchedules = ScheduleDetail.PrivateSchedules(listOf(PrivateScheduleFixtures.create()))
            val schoolWithSchedule = ScheduleDetail.SchoolWithSchedule(
                SchoolFixtures.create(),
                false,
                listOf(SchoolScheduleFixtures.create()))
            return ScheduleDetail(schedule, privateSchedules,
                ScheduleDetail.SchoolWithSchedules(listOf(schoolWithSchedule)))
        }
    }
}
