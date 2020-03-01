package net.jp.vss.visitscheduler.controller.schedules

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.controller.ListResponse
import net.jp.vss.visitscheduler.usecase.schedules.ListPrivateScheduleUseCase
import net.jp.vss.visitscheduler.usecase.schedules.PrivateScheduleUseCaseResult
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * ListPrivateSchedule の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property listPrivateScheduleUseCase ListPrivateScheduleUseCase の UseCase
 */
@RestController
@RequestMapping("/api/private-schedules")
@Validated
class ListPrivateScheduleApiController(
    private val getUserUseCase: GetUserUseCase,
    private val listPrivateScheduleUseCase: ListPrivateScheduleUseCase
) {

    /**
     * ListPrivateSchedule.
     *
     * @return レスポンス
     */
    @GetMapping
    fun listPrivateSchedule(
        @NotNull
        @Pattern(regexp = "^[0-9]{4}-[0-9]{2}\$")
        @RequestParam("target_year_and_month")
        targetYearAndMonth: String?
    ): ResponseEntity<ListResponse<PrivateScheduleUseCaseResult>> {
        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val principal = authentication.principal
        val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

        return ResponseEntity.ok(ListResponse(
            listPrivateScheduleUseCase.getPrivateSchedule(user!!.userCode, targetYearAndMonth!!)))
    }
}
