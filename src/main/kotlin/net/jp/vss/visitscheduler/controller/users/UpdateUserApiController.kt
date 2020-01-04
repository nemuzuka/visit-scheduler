package net.jp.vss.visitscheduler.controller.users

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import net.jp.vss.visitscheduler.controller.exceptions.HttpNotFoundException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.usecase.users.UpdateUserUseCase
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * UpdateUser の APIController.
 *
 * @property updateUserUseCase UpdateUser の UseCase
 */
@RestController
@RequestMapping("/api/users")
@Validated
class UpdateUserApiController(
    private val updateUserUseCase: UpdateUserUseCase
) {

    companion object {
        private val log = LoggerFactory.getLogger(UpdateUserApiController::class.java)
    }

    /**
     * UpdateUser.
     *
     * @param userCode タスクコード
     * @param parameter パラメータ
     * @return レスポンス
     */
    @PostMapping(value = ["/{user_code}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUser(
        @NotNull
        @PathVariable("user_code")
        @Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
        userCode: String,

        @Validated
        @RequestBody
        parameter: UpdateUserApiParameter
    ): ResponseEntity<UserUseCaseResult> {

        try {
            val result = updateUserUseCase.updateUser(parameter.toParameter(userCode))
            return ResponseEntity.ok(result)
        } catch (e: NotFoundException) {
            log.info("NotFound {}", e.message)
            throw HttpNotFoundException(e.message!!, e)
        }
    }
}
