package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributesFixtures
import net.jp.vss.visitscheduler.domain.users.User

/**
 * Schedule の Fixture.
 */
class ScheduleFixtures {
    companion object {
        fun create() = create("SCHEDULE-0001", "USER-0001")

        fun create(scheduleCodeValue: String, userCodeValue: String): Schedule {
            val scheduleCode = Schedule.ScheduleCode(scheduleCodeValue)
            val userCode = User.UserCode(userCodeValue)
            val targetYearAndMonth = Schedule.TargetYearAndMonth("2019-12")
            val scheduleDetail = Schedule.ScheduleDetail(attributes = Attributes("""{"attri":"bute"}"""))
            val resourceAttributes = ResourceAttributesFixtures.create()
            return Schedule(scheduleCode = scheduleCode, userCode = userCode,
                scheduleDetail = scheduleDetail, targetYearAndMonth = targetYearAndMonth,
                resourceAttributes = resourceAttributes)
        }
    }
}
