package net.jp.vss.visitscheduler.controller.schools

import net.jp.vss.visitscheduler.controller.exceptions.HttpConflictException
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.usecase.schools.CreateSchoolUseCase
import net.jp.vss.visitscheduler.usecase.schools.SchoolUseCaseResult
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
 * CreateSchool の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property createSchoolUseCase CreateSchool の UseCase
 */
@RestController
@RequestMapping("/api/schools")
@Validated
class CreateSchoolApiController(
    private val getUserUseCase: GetUserUseCase,
    private val createSchoolUseCase: CreateSchoolUseCase
) {
    companion object {
        private val log = LoggerFactory.getLogger(CreateSchoolApiController::class.java)
    }

    /**
     * CreateSchool.
     *
     * @param parameter パラメータ
     * @return レスポンス
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(
        @Validated
        @RequestBody
        parameter: CreateSchoolApiParameter
    ): ResponseEntity<SchoolUseCaseResult> {

        try {

            val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
            val principal = authentication.principal
            val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

            val result = createSchoolUseCase.createSchool(
                parameter.toParameter(user!!.userCode))
            return ResponseEntity.ok(result)
        } catch (e: DuplicateException) {
            // 既に user_code が存在した場合
            log.info("Conclift Parameter({}) {}", parameter, e.message)
            throw HttpConflictException(e.message!!, e)
        }
    }
}
