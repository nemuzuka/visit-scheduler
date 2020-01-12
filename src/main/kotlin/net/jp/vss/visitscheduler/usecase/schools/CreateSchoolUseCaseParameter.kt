package net.jp.vss.visitscheduler.usecase.schools

/**
 * CreateSchoolUseCase のパラメータ.
 *
 * @property schoolCode 学校コード
 * @property name 学校名
 * @property memo メモ
 * @property attributes 付帯情報(JSON 文字列)
 * @property createUserCode 登録ユーザコード
 */
data class CreateSchoolUseCaseParameter(
    val schoolCode: String,
    val name: String,
    val memo: String?,
    val attributes: String?,
    val createUserCode: String
)
