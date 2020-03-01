package net.jp.vss.visitscheduler.controller.schedules

import net.jp.vss.visitscheduler.controller.ListResponse
import net.jp.vss.visitscheduler.usecase.schedules.ListScheduleUseCase
import net.jp.vss.visitscheduler.usecase.schedules.ScheduleUseCaseResult
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * ListSchedule の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property listScheduleUseCase ListSchedule の UseCase
 */
@RestController
@RequestMapping("/api/schedules")
@Validated
class ListScheduleApiController(
    private val getUserUseCase: GetUserUseCase,
    private val listScheduleUseCase: ListScheduleUseCase
) {

    /**
     * ListSchedule.
     *
     * @return レスポンス
     */
    @GetMapping
    fun listSchool(): ResponseEntity<ListResponse<ScheduleUseCaseResult>> {
        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val principal = authentication.principal
        val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

        return ResponseEntity.ok(ListResponse(listScheduleUseCase.allSchedules(user!!.userCode)))
    }
}
