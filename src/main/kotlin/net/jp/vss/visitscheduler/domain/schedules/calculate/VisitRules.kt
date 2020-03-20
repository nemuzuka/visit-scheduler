package net.jp.vss.visitscheduler.domain.schedules.calculate

/**
 * 訪問設定.
 *
 * @property daysToWaitSinceLastVisit 先月の最終訪問日から次回訪問日に空ける日数
 * @property daysToWaitSinceVisitForTargetMonth 当月の初回訪問日から次回訪問日に空ける日数
 */
data class VisitRules(
    val daysToWaitSinceLastVisit: Int,
    val daysToWaitSinceVisitForTargetMonth: Int
) {
    /**
     * 日数変更.
     *
     * <p>
     * 差分を加算した訪問設定を返却します。
     * </p>
     * @param delta 差分
     * @return 変更後訪問設定
     */
    fun updateDays(delta: Int) = copy(daysToWaitSinceLastVisit = daysToWaitSinceLastVisit + delta,
        daysToWaitSinceVisitForTargetMonth = daysToWaitSinceVisitForTargetMonth + delta)
}
