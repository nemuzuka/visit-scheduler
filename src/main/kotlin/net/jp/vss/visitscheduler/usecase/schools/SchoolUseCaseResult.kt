package net.jp.vss.visitscheduler.usecase.schools

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue
import com.fasterxml.jackson.annotation.JsonUnwrapped
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.usecase.ResourceAttributesResult

/**
 * SchoolUseCase の結果.
 *
 * JSON 化することを想定しています。
 * @property schoolCode 学校コード
 * @property name 学校名
 * @property memo メモ
 * @property attributes 付帯情報
 * @property resourceAttributesResult リソース付帯情報
 */
data class SchoolUseCaseResult(
    @field:JsonProperty("school_code")
    val schoolCode: String,

    @field:JsonProperty("name")
    val name: String,

    @field:JsonProperty("memo")
    val memo: String?,

    @field:JsonRawValue
    @field:JsonProperty("attributes")
    val attributes: String?,

    @field:JsonUnwrapped
    val resourceAttributesResult: ResourceAttributesResult
) {
    companion object {
        /**
         * School からのインスタンス生成.
         *
         * @param school 対象 School
         * @return 生成インスタンス
         */
        fun of(school: School): SchoolUseCaseResult {
            val schoolDetail = school.schoolDetail
            val attributes = schoolDetail.attributes
            return SchoolUseCaseResult(
                schoolCode = school.schoolCode.value,
                name = schoolDetail.name,
                memo = schoolDetail.memo,
                attributes = attributes?.value,
                resourceAttributesResult = ResourceAttributesResult.of(school.resourceAttributes))
        }

        fun newSchoolUseCaseResult(): SchoolUseCaseResult {
            return SchoolUseCaseResult(schoolCode = "", name = "",
                memo = null,
                attributes = null,
                resourceAttributesResult = ResourceAttributesResult.empty())
        }
    }
}
