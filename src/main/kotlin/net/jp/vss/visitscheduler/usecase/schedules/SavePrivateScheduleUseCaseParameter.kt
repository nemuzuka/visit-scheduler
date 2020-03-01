package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.PrivateSchedule
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User

/**
 * SavePrivateScheduleUseCase のパラメータ.
 *
 * @param targetDateString 対象年月文字列(yyyy-MM)
 * @property createUserCode 登録ユーザコード
 * @property targetDayAndMemos 個人スケジュールの対象日とメモの組み合わせ要素リスト
 */
data class SavePrivateScheduleUseCaseParameter(
    val targetDateString: String,
    val createUserCode: String,
    val targetDayAndMemos: List<TargetDayAndMemo>
) {

    /**
     * PrivateSchedule リスト生成.
     *
     * @return PrivateSchedule リスト
     */
    fun toPrivateSchedules(): List<PrivateSchedule> {
        val targetDayAndMemos = PrivateSchedule.TargetDayAndMemos(targetDayAndMemos.map {
            it.toTargetDayAndMemo()
        }.toList())
        return PrivateSchedule.of(User.UserCode(createUserCode),
            Schedule.TargetYearAndMonth(targetDateString), targetDayAndMemos)
    }

    /**
     * 個人スケジュールの対象日とメモの組み合わせ要素.
     *
     * @property targetDay 対象日(対象年月の1日から月末まで有効)
     * @property memo メモ
     */
    data class TargetDayAndMemo(
        val targetDay: Int,
        val memo: String?
    ) {
        fun toTargetDayAndMemo(): PrivateSchedule.TargetDayAndMemo {
            return PrivateSchedule.TargetDayAndMemo(targetDay = targetDay,
                memo = memo)
        }
    }
}
