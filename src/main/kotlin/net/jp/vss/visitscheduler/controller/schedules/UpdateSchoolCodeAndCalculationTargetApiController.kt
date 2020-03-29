package net.jp.vss.visitscheduler.controller.schedules

import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.controller.exceptions.HttpBadRequestException
import net.jp.vss.visitscheduler.controller.exceptions.HttpNotFoundException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.usecase.schedules.UpdateSchoolCodeAndCalculationTargetUseCase
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * UpdateSchoolCodeAndCalculationTarget の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property updateSchoolCodeAndCalculationTargetUseCase UpdateSchoolCodeAndCalculationTarget の UseCase
 */
@RestController
@RequestMapping("/api/schedules")
@Validated
class UpdateSchoolCodeAndCalculationTargetApiController(
    private val getUserUseCase: GetUserUseCase,
    private val updateSchoolCodeAndCalculationTargetUseCase: UpdateSchoolCodeAndCalculationTargetUseCase
) {

    companion object {
        private val log = LoggerFactory.getLogger(UpdateSchoolCodeAndCalculationTargetApiController::class.java)
    }

    /**
     * CreateSchedule.
     *
     * @param parameter パラメータ
     * @return レスポンス
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE],
        path = ["/{schedule_code}/_update-school-code-and-calculation-target"])
    fun updateSchoolCodeAndCalculationTarget(
        @PathVariable("schedule_code")
        @Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        scheduleCode: String,

        @Validated
        @RequestBody
        parameter: UpdateSchoolCodeAndCalculationTargetApiParameter
    ): ResponseEntity<String> {

        try {
            val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val principal = authentication.principal
            val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

            updateSchoolCodeAndCalculationTargetUseCase.update(parameter.toParameter(scheduleCode, user!!.userCode))
            return ResponseEntity.ok("")
        } catch (e: IllegalStateException) {
            // user_code に紐づかない school_code を指定した場合
            log.info("BadRequest Parameter({}) {}", parameter, e.message)
            throw HttpBadRequestException(e.message!!, e)
        } catch (e: NotFoundException) {
            // schedule_code に関連するデータが存在しない場合
            log.info("Conclift Parameter({}) {}", parameter, e.message)
            throw HttpNotFoundException(e.message!!, e)
        }
    }
}
