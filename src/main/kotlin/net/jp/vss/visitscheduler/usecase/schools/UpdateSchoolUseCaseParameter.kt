package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.schools.School

/**
 * UpdateSchoolUseCase のパラメータ.
 *
 * null を設定した項目は更新対象外です。
 *
 * - version を指定すると、School の version と比較して、合致した時のみ更新を行います
 *
 * @property schoolCode 学校コード
 * @property name 学校名
 * @property memo メモ
 * @property attributes 付帯情報(JSON 文字列)
 * @property version 更新前 version
 * @property updateUserCode 更新ユーザコード
 */
data class UpdateSchoolUseCaseParameter(
    val schoolCode: String,
    val name: String?,
    val memo: String?,
    val attributes: String?,
    val version: Long?,
    val updateUserCode: String
) {
    /**
     * 更新対象 School 生成.
     *
     * パラメータを反映した School を生成します
     *
     * @param school 更新元 School
     * @return 更新対象 School
     */
    fun buildUpdateSchool(school: School): School {

        val baseSchoolDetail = school.schoolDetail

        val schoolDetail = baseSchoolDetail.copy(
            name = name?.let { it } ?: baseSchoolDetail.name,
            memo = memo?.let { it } ?: baseSchoolDetail.memo,
            attributes = attributes?.let { Attributes(it) } ?: baseSchoolDetail.attributes
        )
        return school.copy(
            schoolDetail = schoolDetail,
            resourceAttributes = school.resourceAttributes.buildForUpdate(updateUserCode))
    }
}
