package net.jp.vss.visitscheduler.usecase.schedules

/**
 * CreateScheduleUseCaseParameter の Fixture.
 */
class CreateScheduleUseCaseParameterFixtures {
    companion object {
        fun create() = CreateScheduleUseCaseParameter("SCHEDULE_0001",
        "2019-01", """{"age":30}""", "USER_0011",
            listOf(
                SchoolCodeAndCalculationTarget("SCHOOL_001", true),
                SchoolCodeAndCalculationTarget("SCHOOL_002", false)))
    }
}
