package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException

/**
 * School を更新する UseCase.
 */
interface UpdateSchoolUseCase {

    /**
     * School 更新.
     *
     * null の項目は更新対象としません。
     * @param parameter パラメータ
     * @return 更新結果
     * @throws NotFoundException 更新対象の School が存在しない
     * @throws UnmatchVersionException version 指定時、更新対象の School の version と合致しない
     */
    fun updateSchool(parameter: UpdateSchoolUseCaseParameter): SchoolUseCaseResult
}
