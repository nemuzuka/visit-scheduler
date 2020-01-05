package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException

/**
 * School を削除する UseCase.
 */
interface DeleteSchoolUseCase {

    /**
     * School 削除.
     *
     * @param schoolCode school_code
     * @param userCode ログインユーザのユーザコード
     * @param version 更新前 version
     * @throws NotFoundException 指定したSchoolが存在しない
     */
    fun deleteSchool(schoolCode: String, userCode: String, version: Long?)
}
