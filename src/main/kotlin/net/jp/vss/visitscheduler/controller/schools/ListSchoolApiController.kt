package net.jp.vss.visitscheduler.controller.schools

import net.jp.vss.visitscheduler.controller.ListResponse
import net.jp.vss.visitscheduler.usecase.schools.ListSchoolUseCase
import net.jp.vss.visitscheduler.usecase.schools.SchoolUseCaseResult
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * ListSchool の APIController.
 *
 * @property getUserUseCase GetUserUseCase の UseCase
 * @property listSchoolUseCase ListSchool の UseCase
 */
@RestController
@RequestMapping("/api/schools")
@Validated
class ListSchoolApiController(
    private val getUserUseCase: GetUserUseCase,
    private val listSchoolUseCase: ListSchoolUseCase
) {

    /**
     * ListSchool.
     *
     * @return レスポンス
     */
    @GetMapping
    fun listSchool(): ResponseEntity<ListResponse<SchoolUseCaseResult>> {
        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val principal = authentication.principal
        val user = getUserUseCase.getUser(authentication.authorizedClientRegistrationId, principal.name)

        return ResponseEntity.ok(ListResponse(listSchoolUseCase.allSchools(user!!.userCode)))
    }
}
