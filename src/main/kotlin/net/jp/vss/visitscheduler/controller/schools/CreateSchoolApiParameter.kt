package net.jp.vss.visitscheduler.controller.schools

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import net.jp.vss.visitscheduler.constrains.JsonStringConstrains
import net.jp.vss.visitscheduler.usecase.schools.CreateSchoolUseCaseParameter

/**
 * CreateSchoolApiController のパラメータ.
 */
data class CreateSchoolApiParameter(

    @field:NotNull
    @field:Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
    @field:Size(max = 128)
    @field:JsonProperty("school_code")
    val schoolCode: String? = null,

    @field:NotNull
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
     * CreateSchoolUseCaseParameter に変換.
     *
     * @param userCode User.user_code
     * @return 生成 CreateUserUseCaseParameter
     */
    fun toParameter(userCode: String): CreateSchoolUseCaseParameter =
        CreateSchoolUseCaseParameter(
            schoolCode = schoolCode!!,
            name = name!!,
            memo = memo,
            attributes = attributes,
            createUserCode = userCode)
}
