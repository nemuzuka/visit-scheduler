package net.jp.vss.visitscheduler.domain.schedules

import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.ResourceAttributesFixtures

/**
 * PrivateSchedule の Fixture.
 */
class PrivateScheduleFixtures {
    companion object {
        fun create() = PrivateSchedule(
            Schedule.ScheduleDate(LocalDate.parse("2020-02-21")),
            PrivateSchedule.PrivateScheduleDetail("授業参観"),
            ResourceAttributesFixtures.create())
    }
}
