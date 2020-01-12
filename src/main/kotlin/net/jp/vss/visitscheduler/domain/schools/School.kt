package net.jp.vss.visitscheduler.domain.schools

import java.util.UUID
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException
import net.jp.vss.visitscheduler.domain.users.User

/**
 * 学校.
 *
 * @property schoolId 学校識別子
 * @property schoolCode 学校コード
 * @property userCode ユーザコード
 * @property schoolDetail 学校詳細
 * @property resourceAttributes リソース詳細情報
 */
data class School(
    val schoolId: SchoolId,
    val schoolCode: SchoolCode,
    val userCode: User.UserCode,
    val schoolDetail: SchoolDetail,
    val resourceAttributes: ResourceAttributes
) {

    companion object {
        /**
         * 登録時のインスタンス生成.
         *
         * @param schoolCodeValue 学校コード文字列
         * @param userCode ユーザコード
         * @param name 学校名
         * @param memo メモ
         * @param attributeJsonString 付帯情報
         * @param createUserCode 登録ユーザコード
         * @return 登録時の School
         */
        fun buildForCreate(
            schoolCodeValue: String,
            userCode: User.UserCode,
            name: String,
            memo: String?,
            attributeJsonString: String?,
            createUserCode: String
        ): School {
            val schoolId = SchoolId(UUID.randomUUID().toString())
            val schoolCode = SchoolCode(schoolCodeValue)
            val schoolDetail = SchoolDetail(name = name,
                    memo = memo,
                    attributes = Attributes.of(attributeJsonString))
            val resourceAttributes = ResourceAttributes.buildForCreate(createUserCode)
            return School(schoolId = schoolId, schoolCode = schoolCode, userCode = userCode,
                schoolDetail = schoolDetail, resourceAttributes = resourceAttributes)
        }
    }

    /**
     * version 比較.
     *
     * 比較対象 version が null でない場合、本インスタンスの version と比較します
     * @param version 比較対象 version
     * @throws UnmatchVersionException 比較した結果、version が異なる時
     */
    fun validateVersion(version: Long?) = this.resourceAttributes.validateVersion(version)

    /**
     * 学校識別子値オブジェクト.
     *
     * @property value 値
     */
    data class SchoolId(val value: String)

    /**
     * 学校コード値オブジェクト.
     *
     * @property value
     */
    data class SchoolCode(val value: String)

    /**
     * 学校詳細値オブジェクト.
     *
     * @property name 学校名
     * @property memo メモ
     * @property attributes 付帯情報
     */
    data class SchoolDetail(
        val name: String,
        val memo: String?,
        val attributes: Attributes?
    )
}
