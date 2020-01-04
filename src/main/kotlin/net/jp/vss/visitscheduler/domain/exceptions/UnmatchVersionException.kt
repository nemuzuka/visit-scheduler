package net.jp.vss.visitscheduler.domain.exceptions

/**
 * version 指定時に該当レコードが存在しない時の Exception.
 *
 * @param message メッセージ
 */
class UnmatchVersionException(message: String) : RuntimeException(message)
