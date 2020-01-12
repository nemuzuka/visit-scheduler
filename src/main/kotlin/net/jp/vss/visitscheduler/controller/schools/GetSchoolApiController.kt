package net.jp.vss.visitscheduler.controller.schools

import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.controller.exceptions.HttpNotFoundException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.usecase.schools.GetSchoolUseCase
import net.jp.vss.visitscheduler.usecase.schools.SchoolUseCaseResult
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
 * GetSchool の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property getSchoolUseCase GetSchool の UseCase
 */
@RestController
@RequestMapping("/api/schools")
@Validated
class GetSchoolApiController(
    private val getUserUseCase: GetUserUseCase,
    private val getSchoolUseCase: GetSchoolUseCase
) {

    companion object {
        private val log = LoggerFactory.getLogger(GetSchoolApiController::class.java)
    }

    /**
     * GetSchool.
     *
     * @param schoolCode パラメータ
     * @return レスポンス
     */
    @GetMapping("/{school_code}")
    fun getSchool(
        @PathVariable("school_code")
        @Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        schoolCode: String
    ): ResponseEntity<SchoolUseCaseResult> {

        try {
            val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val principal = authentication.principal
            val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

            return ResponseEntity.ok(getSchoolUseCase.getSchool(schoolCode, user!!.userCode))
        } catch (e: NotFoundException) {
            log.info("NotFound {}", e.message)
            throw HttpNotFoundException(e.message!!, e)
        }
    }

    /**
     * GetNewSchool.
     *
     * School 登録用の情報を取得します
     * @return レスポンス
     */
    @GetMapping("/_new")
    fun getNewSchool(): ResponseEntity<SchoolUseCaseResult> {
        return ResponseEntity.ok(SchoolUseCaseResult.newSchoolUseCaseResult())
    }
}
