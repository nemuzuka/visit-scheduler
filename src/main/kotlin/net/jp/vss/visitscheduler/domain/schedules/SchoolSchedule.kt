package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User

/**
 * 学校スケジュール
 *
 * @property targetDate 対象日
 * @property schoolCode 学校コード
 * @property schoolScheduleDetail 学校スケジュール詳細
 * @property resourceAttributes リソース詳細情報
 */
data class SchoolSchedule(
    val targetDate: Schedule.ScheduleDate,
    val schoolCode: School.SchoolCode,
    val schoolScheduleDetail: SchoolScheduleDetail,
    val resourceAttributes: ResourceAttributes
) {

    companion object {
        /**
         * 登録時のインスタンス生成.
         *
         * @param userCode ユーザコード
         * @param schoolCode 学校コード
         * @param targetYearAndMonth 対象年月
         * @param targetDayAndMemos 学校スケジュールの対象日とメモの組み合わせ要素リスト
         * @return 生成インスタンスリスト
         */
        fun of(
            userCode: User.UserCode,
            schoolCode: School.SchoolCode,
            targetYearAndMonth: Schedule.TargetYearAndMonth,
            targetDayAndMemos: TargetDayAndMemos
        ): List<SchoolSchedule> {
            val resourceAttributes = ResourceAttributes.buildForCreate(userCode.value)
            return targetDayAndMemos.targetDayAndMemos.map {
                val targetDate = Schedule.ScheduleDate.of(targetYearAndMonth, it.targetDay)
                val schoolScheduleDetail = SchoolScheduleDetail(memo = it.memo, priority = it.priority)
                SchoolSchedule(targetDate = targetDate, schoolCode = schoolCode,
                    schoolScheduleDetail = schoolScheduleDetail, resourceAttributes = resourceAttributes)
            }.toList()
        }
    }

    /**
     * 学校スケジュール詳細値オブジェクト.
     *
     * @property memo メモ
     * @property priority 優先度
     */
    data class SchoolScheduleDetail(
        val memo: String?,
        val priority: Priority
    )

    /**
     * 学校スケジュールの対象日とメモの組み合わせ要素.
     *
     * @property targetDay 対象日(対象年月の1日から月末まで有効)
     * @property priority 優先度
     * @property memo メモ
     */
    data class TargetDayAndMemo(
        val targetDay: Int,
        val priority: Priority,
        val memo: String?
    )

    /**
     * スケジュール優先度.
     */
    enum class Priority {
        /** この日は NG. */
        DONT_COME,

        /** 可能ならこの日. */
        POSSIBLE,

        /** 絶対この日. */
        ABSOLUTELY
    }

    /**
     * 学校スケジュールの対象日とメモの組み合わせ.
     *
     * @property targetDayAndMemos 学校スケジュールの対象日とメモの組み合わせ要素リスト
     */
    data class TargetDayAndMemos(val targetDayAndMemos: List<TargetDayAndMemo>)
}
