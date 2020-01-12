package net.jp.vss.visitscheduler.controller.schools

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Size
import net.jp.vss.visitscheduler.constrains.JsonStringConstrains
import net.jp.vss.visitscheduler.usecase.schools.UpdateSchoolUseCaseParameter

/**
 * UpdateSchoolController のパラメータ.
 */
data class UpdateSchoolApiParameter(

    @field:Size(max = 256)
    @field:JsonProperty("name")
    val name: String? = null,

    @field:Size(max = 1024)
    @field:JsonProperty("memo")
    val memo: String? = null,

    @JsonStringConstrains
    @field:JsonProperty("attributes")
    val attributes: String? = null
) {
    /**
     * UpdateSchoolUseCaseParameter に変換.
     *
     * @param schoolCode 学校コード
     * @param version version
     * @param updateUserCode 更新ユーザコード
     * @return 生成 UpdateSchoolUseCaseParameter
     */
    fun toParameter(schoolCode: String, version: Long?, updateUserCode: String): UpdateSchoolUseCaseParameter =
        UpdateSchoolUseCaseParameter(
            schoolCode = schoolCode,
            memo = this.memo,
            name = this.name,
            attributes = attributes,
            version = version,
            updateUserCode = updateUserCode)
}
