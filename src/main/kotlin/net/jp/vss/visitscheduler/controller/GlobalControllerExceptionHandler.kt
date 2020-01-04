package net.jp.vss.visitscheduler.controller

import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * 共通の Controller のハンドラ.
 */
@ControllerAdvice
class GlobalControllerExceptionHandler {

    /**
     * ConstraintViolationException を throw する時は 400
     *
     * @param response HttpServletResponse
     * @param exception 対象例外
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(response: HttpServletResponse, exception: Exception) {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "Constraint Violation Exception:${exception.message}")
    }
}
