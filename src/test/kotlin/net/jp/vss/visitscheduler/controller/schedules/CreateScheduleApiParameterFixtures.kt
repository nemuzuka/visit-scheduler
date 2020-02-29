package net.jp.vss.visitscheduler.controller.schedules

/**
 * CreateScheduleApiParameter の Fixture.
 */
class CreateScheduleApiParameterFixtures {

    companion object {
        fun create(): CreateScheduleApiParameter = CreateScheduleApiParameter(
                scheduleCode = "SCHEDULE_0001",
                targetYearAndMonth = "2019-12",
                attributes = """{"hoge":"hage"}""",
                schoolCodeAndCalculationTargets = listOf(
                    CreateScheduleApiParameter.SchoolCodeAndCalculationTarget("SCHOOL_001", true),
                    CreateScheduleApiParameter.SchoolCodeAndCalculationTarget("SCHOOL_002", false)))
    }
}
