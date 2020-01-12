package net.jp.vss.visitscheduler.controller.schools

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.controller.exceptions.HttpConflictException
import net.jp.vss.visitscheduler.controller.exceptions.HttpNotFoundException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException
import net.jp.vss.visitscheduler.usecase.schools.SchoolUseCaseResult
import net.jp.vss.visitscheduler.usecase.schools.UpdateSchoolUseCase
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * UpdateSchool の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property updateSchoolUseCase UpdateSchool の UseCase
 */
@RestController
@RequestMapping("/api/schools")
@Validated
class UpdateSchoolApiController(
    private val getUserUseCase: GetUserUseCase,
    private val updateSchoolUseCase: UpdateSchoolUseCase
) {
    companion object {
        private val log = LoggerFactory.getLogger(UpdateSchoolApiController::class.java)
    }

    /**
     * UpdateSchool.
     *
     * @param schoolCode 学校コード
     * @param parameter パラメータ
     * @param version 排他制御用 version
     * @return レスポンス
     */
    @PostMapping(value = ["/{school_code}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateSchool(
        @NotNull
        @PathVariable("school_code")
        @Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        schoolCode: String,

        @Validated
        @RequestBody
        parameter: UpdateSchoolApiParameter,

        @RequestParam("version")
        version: Long?
    ): ResponseEntity<SchoolUseCaseResult> {

        try {
            val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val principal = authentication.principal
            val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

            val result = updateSchoolUseCase.updateSchool(
                parameter.toParameter(schoolCode, version, user!!.userCode))
            return ResponseEntity.ok(result)
        } catch (e: UnmatchVersionException) {
            // version 相違
            log.info("Invalid School({}) version", schoolCode)
            throw HttpConflictException(e.message!!, e)
        } catch (e: NotFoundException) {
            log.info("NotFound {}", e.message)
            throw HttpNotFoundException(e.message!!, e)
        }
    }
}
