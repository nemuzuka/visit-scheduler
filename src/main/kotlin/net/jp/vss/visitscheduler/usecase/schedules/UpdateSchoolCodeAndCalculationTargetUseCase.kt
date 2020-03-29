package net.jp.vss.visitscheduler.usecase.schedules

/**
 * SchoolCodeAndCalculationTarget を Update する UseCase.
 */
interface UpdateSchoolCodeAndCalculationTargetUseCase {

    /**
     * 更新.
     *
     * @param parameter パラメータ
     * @throws IllegalStateException パラメータ不正
     */
    fun update(parameter: UpdateSchoolCodeAndCalculationTargetUseCaseParameter)
}
