package net.jp.vss.visitscheduler.controller.schedules

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.usecase.schedules.ListSchoolScheduleUseCase
import net.jp.vss.visitscheduler.usecase.schedules.SchoolScheduleUseCaseResult
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * ListSchoolSchedule の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property listSchoolScheduleUseCase ListSchoolScheduleUseCase の UseCase
 */
@RestController
@RequestMapping("/api/school-schedules")
@Validated
class ListSchoolScheduleApiController(
    private val getUserUseCase: GetUserUseCase,
    private val listSchoolScheduleUseCase: ListSchoolScheduleUseCase
) {

    /**
     * ListSchoolSchedule.
     *
     * @return レスポンス
     */
    @GetMapping("/_by-school/{school_code}")
    fun listSchoolSchedule(
        @NotNull
        @PathVariable("school_code")
        @Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        schoolCode: String?,

        @NotNull
        @Pattern(regexp = "^[0-9]{4}-[0-9]{2}\$")
        @RequestParam("target_year_and_month")
        targetYearAndMonth: String?
    ): ResponseEntity<SchoolScheduleUseCaseResult> {
        return ResponseEntity.ok(listSchoolScheduleUseCase.getSchoolSchedule(schoolCode!!, targetYearAndMonth!!))
    }
}
