package net.jp.vss.visitscheduler.usecase.schools

/**
 * School 一覧を取得する UseCase.
 */
interface ListSchoolUseCase {

    /**
     * 全ての School 取得.
     *
     * @param userCode 対象ユーザコード
     * @return 対象 School List
     */
    fun allSchools(userCode: String): List<SchoolUseCaseResult>
}
