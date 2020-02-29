package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User

/**
 * スケジュール
 *
 * @property scheduleCode スケジュールコード
 * @property userCode ユーザコード
 * @property targetYearAndMonth 対象年月
 * @property scheduleDetail スケジュール詳細
 * @property resourceAttributes リソース詳細情報
 */
data class Schedule(
    val scheduleCode: ScheduleCode,
    val userCode: User.UserCode,
    val targetYearAndMonth: TargetYearAndMonth,
    val scheduleDetail: ScheduleDetail,
    val resourceAttributes: ResourceAttributes
) {

    companion object {
        /**
         * 登録時のインスタンス生成.
         *
         * @param scheduleCodeValue スケジュールコード文字列
         * @param userCode ユーザコード
         * @param targetDateString 対象年月文字列
         * @param attributeJsonString 付帯情報
         * @param createUserCode 登録ユーザコード
         * @return 登録時の Schedule
         */
        fun buildForCreate(
            scheduleCodeValue: String,
            userCode: User.UserCode,
            targetDateString: String,
            attributeJsonString: String?,
            createUserCode: String
        ): Schedule {
            val scheduleCode = ScheduleCode(scheduleCodeValue)
            val scheduleDetail = ScheduleDetail(attributes = Attributes.of(attributeJsonString))
            val resourceAttributes = ResourceAttributes.buildForCreate(createUserCode)
            return Schedule(scheduleCode = scheduleCode, userCode = userCode,
                targetYearAndMonth = TargetYearAndMonth(targetDateString),
                scheduleDetail = scheduleDetail, resourceAttributes = resourceAttributes)
        }
    }

    /**
     * version 比較.
     *
     * 比較対象 version が null でない場合、本インスタンスの version と比較します
     * @param version 比較対象 version
     * @throws UnmatchVersionException 比較した結果、version が異なる時
     */
    fun validateVersion(version: Long?) = this.resourceAttributes.validateVersion(version)

    /**
     * スケジュールコード値オブジェクト.
     *
     * @property value 値
     */
    data class ScheduleCode(val value: String)

    /**
     * スケジュール対象年月値オブジェクト.
     *
     * @property value 値(yyyy-MM 形式)
     */
    data class TargetYearAndMonth(val value: String)

    /**
     * スケジュール詳細値オブジェクト.
     *
     * @property attributes 付帯情報
     */
    data class ScheduleDetail(
        val attributes: Attributes?
    )

    /**
     * スケジュールに関連する school_code と 計算対象の組み合わせ要素.
     *
     * @property schoolCode school_code の値
     * @property calculationTarget 計算対象の場合、true
     */
    data class SchoolCodeAndCalculationTarget(
        val schoolCode: School.SchoolCode,
        val calculationTarget: Boolean
    )

    /**
     * スケジュールに関連する school_code と 計算対象の組み合わせ.
     *
     * @property schoolCodeAndCalculationTargets スケジュールに関連する school_code と 計算対象の組み合わせ要素リスト
     */
    data class SchoolCodeAndCalculationTargets(
        val schoolCodeAndCalculationTargets: List<SchoolCodeAndCalculationTarget>
    )
}
