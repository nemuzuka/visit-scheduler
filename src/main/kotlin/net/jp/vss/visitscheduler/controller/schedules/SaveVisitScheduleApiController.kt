package net.jp.vss.visitscheduler.controller.schedules

import net.jp.vss.visitscheduler.usecase.schedules.SaveVisitScheduleUseCase
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * SaveVisitSchedule の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property saveVisitScheduleUseCase SaveVisitScheduleUseCase の UseCase
 */
@RestController
@RequestMapping("/api/visit-schedules")
@Validated
class SaveVisitScheduleApiController(
    private val getUserUseCase: GetUserUseCase,
    private val saveVisitScheduleUseCase: SaveVisitScheduleUseCase
) {
    /**
     * SaveVisitSchedule.
     *
     * @param parameter パラメータ
     * @return レスポンス
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun savePrivateSchedule(
        @Validated
        @RequestBody
        parameter: SaveVisitScheduleApiParameter
    ): ResponseEntity<String> {
        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val principal = authentication.principal
        val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

        val result = saveVisitScheduleUseCase.saveVisitSchedules(
            parameter.toParameter(user!!.userCode))
        return ResponseEntity.ok("")
    }
}
