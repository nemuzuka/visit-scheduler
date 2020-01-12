package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException

/**
 * School を取得する UseCase.
 */
interface GetSchoolUseCase {

    /**
     * School 取得.
     *
     * @param schoolCode school_code
     * @param userCode ログインユーザのユーザコード
     * @return 対象 School
     * @throws NotFoundException 指定したSchoolが存在しない
     */
    fun getSchool(schoolCode: String, userCode: String): SchoolUseCaseResult
}
