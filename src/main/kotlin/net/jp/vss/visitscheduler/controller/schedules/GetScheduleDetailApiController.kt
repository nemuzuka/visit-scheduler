package net.jp.vss.visitscheduler.controller.schedules

import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.controller.exceptions.HttpNotFoundException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.usecase.schedules.GetScheduleDetailUseCase
import net.jp.vss.visitscheduler.usecase.schedules.ScheduleDetailUseCaseResult
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * GetScheduleDetail の APIController.
 *
 * @property getScheduleDetailUseCase GetScheduleDetailUseCase の UseCase
 * @property getUserUseCase GetUserUseCase の UseCase
 */
@RestController
@RequestMapping("/api/schedules")
@Validated
class GetScheduleDetailApiController(
    private val getUserUseCase: GetUserUseCase,
    private val getScheduleDetailUseCase: GetScheduleDetailUseCase
) {

    companion object {
        private val log = LoggerFactory.getLogger(GetScheduleDetailApiController::class.java)
    }

    /**
     * GetSchedule.
     *
     * @param scheduleCode パラメータ
     * @return レスポンス
     */
    @GetMapping("/{schedule_code}")
    fun getSchedule(
        @PathVariable("schedule_code")
        @Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        scheduleCode: String
    ): ResponseEntity<ScheduleDetailUseCaseResult> {

        try {
            val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val principal = authentication.principal
            val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

            return ResponseEntity.ok(getScheduleDetailUseCase.getScheduleDetail(scheduleCode, user!!.userCode))
        } catch (e: NotFoundException) {
            log.info("NotFound {}", e.message)
            throw HttpNotFoundException(e.message!!, e)
        }
    }
}
