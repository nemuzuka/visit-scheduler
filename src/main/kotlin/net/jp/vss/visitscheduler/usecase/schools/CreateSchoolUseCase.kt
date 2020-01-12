package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException

/**
 * School を登録する UseCase.
 */
interface CreateSchoolUseCase {

    /**
     * School 登録.
     *
     * @param parameter パラメータ
     * @return 登録結果
     * @throws DuplicateException 既に存在する
     */
    fun createSchool(parameter: CreateSchoolUseCaseParameter): SchoolUseCaseResult
}
