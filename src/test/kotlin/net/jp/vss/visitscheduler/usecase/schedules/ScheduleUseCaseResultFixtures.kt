package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.usecase.ResourceAttributesResultFixtures

/**
 * ScheduleUseCaseResult の Fixture.
 */
class ScheduleUseCaseResultFixtures {
    companion object {
        fun create() = ScheduleUseCaseResult(
            scheduleCode = "SCHEDULE_0001",
            targetYearAndMonth = "2020-08",
            attributes = """{"hoge":"hige","fuga":{"neko":"nyan"}}""",
            resourceAttributesResult = ResourceAttributesResultFixtures.create()
        )
    }
}
