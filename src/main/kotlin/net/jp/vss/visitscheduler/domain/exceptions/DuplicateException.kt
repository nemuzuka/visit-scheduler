package net.jp.vss.visitscheduler.domain.exceptions

/**
 * 既に存在する場合の Exception.
 *
 * @param message メッセージ
 */
class DuplicateException(message: String) : RuntimeException(message)
