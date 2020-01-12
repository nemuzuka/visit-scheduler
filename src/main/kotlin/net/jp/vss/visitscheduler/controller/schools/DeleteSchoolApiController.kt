package net.jp.vss.visitscheduler.controller.schools

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.controller.exceptions.HttpConflictException
import net.jp.vss.visitscheduler.controller.exceptions.HttpNotFoundException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException
import net.jp.vss.visitscheduler.usecase.schools.DeleteSchoolUseCase
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * DeleteSchool の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property deleteSchoolUseCase DeleteSchool の UseCase
 */
@RestController
@RequestMapping("/api/schools")
@Validated
class DeleteSchoolApiController(
    private val getUserUseCase: GetUserUseCase,
    private val deleteSchoolUseCase: DeleteSchoolUseCase
) {
    companion object {
        private val log = LoggerFactory.getLogger(DeleteSchoolApiController::class.java)
    }

    /**
     * DeleteSchool.
     *
     * @param schoolCode 学校コード
     * @param version 排他制御用 version
     * @return レスポンス
     */
    @DeleteMapping(value = ["/{school_code}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteSchool(
        @NotNull
        @PathVariable("school_code")
        @Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        schoolCode: String,

        @RequestParam("version")
        version: Long?
    ): ResponseEntity<Void> {

        try {
            val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val principal = authentication.principal
            val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

            deleteSchoolUseCase.deleteSchool(schoolCode, user!!.userCode, version)
            return ResponseEntity.noContent().build()
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
