package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.usecase.schedules.UpdateSchoolCodeAndCalculationTargetUseCaseParameter

data class UpdateSchoolCodeAndCalculationTargetApiParameter(
    @field:NotNull
    @field:JsonProperty("school_code_and_calculation_targets")
    @field:Valid
    val schoolCodeAndCalculationTargets: List<SchoolCodeAndCalculationTarget>? = null
) {
    /**
     * UpdateSchoolCodeAndCalculationTargetUseCaseParameter に変換.
     *
     * @param userCode User.user_code
     * @return 生成 UpdateSchoolCodeAndCalculationTargetUseCaseParameter
     */
    fun toParameter(scheduleCode: String, userCode: String): UpdateSchoolCodeAndCalculationTargetUseCaseParameter =
        UpdateSchoolCodeAndCalculationTargetUseCaseParameter(
            scheduleCode = Schedule.ScheduleCode(scheduleCode),
            userCode = User.UserCode(userCode),
            schoolCodeAndCalculationTargets = schoolCodeAndCalculationTargets!!.map {
                it.toSchoolCodeAndCalculationTarget()
            }.toList())
}
