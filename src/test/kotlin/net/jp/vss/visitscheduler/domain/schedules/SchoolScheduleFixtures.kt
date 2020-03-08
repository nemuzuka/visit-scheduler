package net.jp.vss.visitscheduler.domain.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.ResourceAttributesFixtures
import net.jp.vss.visitscheduler.domain.schools.School

/**
 * SchoolSchedule の Fixture.
 */
class SchoolScheduleFixtures {
    companion object {
        fun create() = SchoolSchedule(
            Schedule.ScheduleDate(LocalDate.parse("2020-02-21")),
            School.SchoolCode("SCHOOL-0001"),
            SchoolSchedule.SchoolScheduleDetail("ナニカ", SchoolSchedule.Priority.DONT_COME),
            ResourceAttributesFixtures.create())
    }
}
