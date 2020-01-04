package net.jp.vss.visitscheduler.controller.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 409 を表す.
 *
 * @param message メッセージ
 * @param cause Throwable
 */
@ResponseStatus(HttpStatus.CONFLICT)
class HttpConflictException(message: String, cause: Throwable) : HttpResponseException(message, cause)
