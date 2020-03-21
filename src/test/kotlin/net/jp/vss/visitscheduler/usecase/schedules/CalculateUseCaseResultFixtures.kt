package net.jp.vss.visitscheduler.usecase.schedules

/** CalculateUseCaseResult の Fixtures. */
class CalculateUseCaseResultFixtures {
    companion object {
        fun create(): CalculateUseCaseResult {
            val visitDayAndSchool = CalculateUseCaseResult.VisitDayAndSchool(5, "school_001")
            return CalculateUseCaseResult(listOf(visitDayAndSchool))
        }
    }
}
