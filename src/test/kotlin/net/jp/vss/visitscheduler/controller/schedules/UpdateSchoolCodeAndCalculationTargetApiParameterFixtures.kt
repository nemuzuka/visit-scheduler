package net.jp.vss.visitscheduler.controller.schedules

/**
 * UpdateSchoolCodeAndCalculationTargetApiParameter の Fixture.
 */
class UpdateSchoolCodeAndCalculationTargetApiParameterFixtures {
    companion object {
        fun create(): UpdateSchoolCodeAndCalculationTargetApiParameter =
            UpdateSchoolCodeAndCalculationTargetApiParameter(
                schoolCodeAndCalculationTargets = listOf(
                    SchoolCodeAndCalculationTarget("SCHOOL_001", true),
                    SchoolCodeAndCalculationTarget("SCHOOL_002", false)))
    }
}
