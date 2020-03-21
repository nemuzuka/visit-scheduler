package net.jp.vss.visitscheduler.usecase.schedules

/**
 * Schedule を計算する UseCase.
 */
interface CalculateUseCase {

    /**
     * スケジュール算出.
     *
     * @param parameter パラメータ
     * @return 結果
     * @throws IllegalArgumentException パラメータが不正
     * @throws IllegalStateException スケジュール算出できる状態ではない
     * @throws CalculateException 計算できなかった
     */
    fun calculateSchedule(parameter: CalculateUseCaseParameter): CalculateUseCaseResult

    class CalculateException(message: String) : RuntimeException(message)
}
