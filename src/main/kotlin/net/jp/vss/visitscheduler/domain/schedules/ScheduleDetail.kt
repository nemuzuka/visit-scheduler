package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.schools.School

/**
 * スケジュール詳細.
 *
 * @property schedule スケジュール
 * @property privateSchedules 当該月に紐づく個人スケジュール
 * @property schoolWithSchedules 当該月に紐づく学校のスケジュール
 */
data class ScheduleDetail(
    val schedule: Schedule,
    val privateSchedules: PrivateSchedules,
    val schoolWithSchedules: SchoolWithSchedules
) {

    /**
     * 当該月に紐づく個人スケジュール
     *
     * @property privateSchedules 個人スケジュールリスト
     */
    data class PrivateSchedules(
        val privateSchedules: List<PrivateSchedule>
    )

    /**
     * 学校とスケジュールの組み合わせ.
     *
     * <p>
     * 並び順は、
     * - 当該スケジュールで指定した学校の並び順
     *
     * ですが、ユーザに紐づく学校の中で並び順に含まれない物は、計算対象外にしてリストの最後に追加します。
     * </p>
     *
     * @property schoolWithSchedules 学校とスケジュールリスト
     */
    data class SchoolWithSchedules(
        val schoolWithSchedules: List<SchoolWithSchedule>
    )

    /**
     * 学校とスケジュール.
     *
     * @property school 学校
     * @property calculationTarget 計算対象の場合、true
     * @property schedules スケジュールリスト(未設定の場合、null)
     */
    data class SchoolWithSchedule(
        val school: School,
        val calculationTarget: Boolean,
        val schedules: List<SchoolSchedule>?
    )
}
