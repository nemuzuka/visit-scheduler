package net.jp.vss.visitscheduler.controller.schedules

/** SaveVisitScheduleApiParameter の Fixtures. */
class SaveVisitScheduleApiParameterFixtures {
    companion object {
        fun create(): SaveVisitScheduleApiParameter {
            val visitDayAndSchoolCodeParameter =
                SaveVisitScheduleApiParameter.VisitDayAndSchoolCodeParameter(21, "SCHOOL_0001")
            return SaveVisitScheduleApiParameter("2020-03", listOf(visitDayAndSchoolCodeParameter))
        }
    }
}
