package net.jp.vss.visitscheduler.controller.schedules

import net.jp.vss.visitscheduler.controller.exceptions.HttpBadRequestException
import net.jp.vss.visitscheduler.controller.exceptions.HttpConflictException
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.usecase.schedules.SaveSchoolScheduleUseCase
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.slf4j.LoggerFactory
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
 * SaveSchoolSchedule の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property saveSchoolScheduleUseCase SaveSchoolScheduleUseCase の UseCase
 */
@RestController
@RequestMapping("/api/school-schedules")
@Validated
class SaveSchoolScheduleApiController(
    private val getUserUseCase: GetUserUseCase,
    private val saveSchoolScheduleUseCase: SaveSchoolScheduleUseCase
) {
    companion object {
        private val log = LoggerFactory.getLogger(SaveSchoolScheduleApiController::class.java)
    }

    /**
     * SaveSchoolSchedule.
     *
     * @param parameter パラメータ
     * @return レスポンス
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun saveSchoolSchedule(
        @Validated
        @RequestBody
        parameter: SaveSchoolScheduleApiParameter
    ): ResponseEntity<String> {
        try {
            val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val principal = authentication.principal
            val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

            saveSchoolScheduleUseCase.saveSchoolSchedule(
                parameter.toParameter(user!!.userCode))
            return ResponseEntity.ok("")
        } catch (e: DuplicateException) {
            // 既に user_code が存在した場合
            log.info("Conclift Parameter({}) {}", parameter, e.message)
            throw HttpConflictException(e.message!!, e)
        } catch (e: IllegalStateException) {
            // user_code に紐づかない school_code を指定した場合
            log.info("BadRequest Parameter({}) {}", parameter, e.message)
            throw HttpBadRequestException(e.message!!, e)
        }
    }
}
