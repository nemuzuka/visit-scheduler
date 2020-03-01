package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.users.User

/**
 * 個人スケジュール
 *
 * @property targetDate 対象日。この日は予定を埋めません
 * @property privateScheduleDetail 個人スケジュール詳細
 * @property resourceAttributes リソース詳細情報
 */
data class PrivateSchedule(
    val targetDate: Schedule.ScheduleDate,
    val privateScheduleDetail: PrivateScheduleDetail,
    val resourceAttributes: ResourceAttributes
) {

    companion object {
        /**
         * 登録時のインスタンス生成.
         *
         * @param userCode ユーザコード
         * @param targetYearAndMonth 対象年月
         * @param targetDayAndMemos 個人スケジュールの対象日とメモの組み合わせ要素リスト
         * @return 生成インスタンスリスト
         */
        fun of(
            userCode: User.UserCode,
            targetYearAndMonth: Schedule.TargetYearAndMonth,
            targetDayAndMemos: TargetDayAndMemos
        ): List<PrivateSchedule> {
            val resourceAttributes = ResourceAttributes.buildForCreate(userCode.value)
            return targetDayAndMemos.targetDayAndMemos.map {
                val targetDate = Schedule.ScheduleDate.of(targetYearAndMonth, it.targetDay)
                val privateScheduleDetail = PrivateScheduleDetail(memo = it.memo)
                PrivateSchedule(targetDate = targetDate,
                    privateScheduleDetail = privateScheduleDetail, resourceAttributes = resourceAttributes)
            }.toList()
        }
    }

    /**
     * 個人スケジュール詳細値オブジェクト.
     *
     * @property memo メモ
     */
    data class PrivateScheduleDetail(
        val memo: String?
    )

    /**
     * 個人スケジュールの対象日とメモの組み合わせ要素.
     *
     * @property targetDay 対象日(対象年月の1日から月末まで有効)
     * @property memo メモ
     */
    data class TargetDayAndMemo(
        val targetDay: Int,
        val memo: String?
    )

    /**
     * 個人スケジュールの対象日とメモの組み合わせ.
     *
     * @property targetDayAndMemos 個人スケジュールの対象日とメモの組み合わせ要素リスト
     */
    data class TargetDayAndMemos(val targetDayAndMemos: List<TargetDayAndMemo>)
}
