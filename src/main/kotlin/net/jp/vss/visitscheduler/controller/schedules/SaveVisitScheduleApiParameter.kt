package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.usecase.schedules.SaveVisitScheduleUseCaseParameter

/**
 * SaveVisitScheduleApiController のパラメータ.
 */
data class SaveVisitScheduleApiParameter(
    @field:NotNull
    @field:Pattern(regexp = "^[0-9]{4}-[0-9]{2}\$")
    @field:JsonProperty("target_year_and_month")
    val targetYearAndMonth: String? = null,

    @field:NotNull
    @field:JsonProperty("visit_day_and_school_codes")
    @field:Valid
    val visitDayAndSchoolCodes: List<VisitDayAndSchoolCodeParameter>? = null
) {

    fun toParameter(userCodeValue: String): SaveVisitScheduleUseCaseParameter {
        return SaveVisitScheduleUseCaseParameter(targetYearAndMonth!!, User.UserCode(userCodeValue),
            visitDayAndSchoolCodes!!.map {
                SaveVisitScheduleUseCaseParameter.VisitDayAndSchoolCode(School.SchoolCode(it.schoolCode!!),
                    it.visitDay!!) }.toList()
        )
    }

    data class VisitDayAndSchoolCodeParameter(
        @field:NotNull
        @field:Min(1)
        @field:Max(31)
        @field:JsonProperty("visit_day")
        val visitDay: Int? = null,

        @field:NotNull
        @field:Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        @field:JsonProperty("school_code")
        val schoolCode: String? = null
    )
}
