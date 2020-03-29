package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User

/**
 * SaveSchoolScheduleUseCase のパラメータ.
 *
 * @param targetDateString 対象年月文字列(yyyy-MM)
 * @property createUserCode 登録ユーザコード
 * @property schoolCode 登録対象学校コード
 * @property targetDayAndMemos 個人スケジュールの対象日とメモの組み合わせ要素リスト
 * @property lastMonthVisitDate 先月の最終訪問日
 */
data class SaveSchoolScheduleUseCaseParameter(
    val targetDateString: String,
    val createUserCode: String,
    val schoolCode: String,
    val targetDayAndMemos: List<TargetDayAndMemo>,
    val lastMonthVisitDate: Schedule.ScheduleDate?
) {

    /**
     * SchoolSchedule リスト生成.
     *
     * @return SchoolSchedule リスト
     */
    fun toSchoolSchedules(): List<SchoolSchedule> {
        val targetDayAndMemos = SchoolSchedule.TargetDayAndMemos(targetDayAndMemos.map {
            it.toTargetDayAndMemo()
        }.toList())
        return SchoolSchedule.of(User.UserCode(createUserCode),
            School.SchoolCode(schoolCode),
            Schedule.TargetYearAndMonth(targetDateString), targetDayAndMemos)
    }

    /**
     * 学校スケジュールの対象日とメモの組み合わせ要素.
     *
     * @property targetDay 対象日(対象年月の1日から月末まで有効)
     * @property memo メモ
     * @property priority 優先度
     */
    data class TargetDayAndMemo(
        val targetDay: Int,
        val memo: String?,
        val priority: SchoolSchedule.Priority
    ) {
        fun toTargetDayAndMemo(): SchoolSchedule.TargetDayAndMemo {
            return SchoolSchedule.TargetDayAndMemo(targetDay = targetDay,
                memo = memo, priority = priority)
        }
    }
}
