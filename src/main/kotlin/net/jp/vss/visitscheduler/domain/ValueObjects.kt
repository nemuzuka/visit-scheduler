package net.jp.vss.visitscheduler.domain

import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException

/**
 * 付帯情報値オブジェクト.
 *
 * @property value JSON 文字列
 */
data class Attributes(val value: String) {
    companion object {
        fun of(value: String?): Attributes? = if (value != null) Attributes(value) else null
    }
}

/**
 * リソース付帯情報値オブジェクト.
 *
 * @property createUserCode 登録ユーザコード
 * @property createAt 登録日時
 * @property lastUpdateUserCode 最終更新ユーザコード
 * @property lastUpdateAt 最終更新日時
 * @property version version
 */
data class ResourceAttributes(
    val createUserCode: String,
    val createAt: Long,
    val lastUpdateUserCode: String,
    val lastUpdateAt: Long,
    val version: Long
) {
    companion object {
        /**
         * 登録時のインスタンス生成.
         *
         * @param createUserCode 登録ユーザコード
         * @return 登録時のインスタンス
         */
        fun buildForCreate(createUserCode: String): ResourceAttributes =
            ResourceAttributes(createUserCode = createUserCode,
                createAt = DatetimeUtils.now(),
                lastUpdateUserCode = createUserCode,
                lastUpdateAt = DatetimeUtils.now(),
                version = 0L)
    }

    /**
     * 更新時のインスタンス生成.
     *
     * @param updateUserCode 更新ユーザコード
     * @return 更新時のインスタンス
     */
    fun buildForUpdate(updateUserCode: String): ResourceAttributes =
            this.copy(lastUpdateUserCode = updateUserCode,
            lastUpdateAt = DatetimeUtils.now())

    /**
     * version 比較.
     *
     * 比較対象 version が null でない場合、本インスタンスの version と比較します
     * @param version 比較対象 version
     * @throws UnmatchVersionException 比較した結果、version が異なる時
     */
    fun validateVersion(version: Long?) =
        version ?. let { if (it != this.version) throw UnmatchVersionException("指定した version が不正です") }
}
