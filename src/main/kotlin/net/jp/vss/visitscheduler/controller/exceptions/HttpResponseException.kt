package net.jp.vss.visitscheduler.controller.exceptions

/**
 * エラー時のレスポンスを表す Exception.
 *
 * @param message メッセージ
 * @param cause Throwable
 */
open class HttpResponseException(message: String, cause: Throwable) : RuntimeException(message, cause)
