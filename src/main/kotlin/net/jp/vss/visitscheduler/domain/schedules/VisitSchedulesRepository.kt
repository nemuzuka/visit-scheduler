package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User

/**
 * VisitSchedules の Repository.
 */
interface VisitSchedulesRepository {

    /**
     * 永続化.
     *
     * <p>
     * 学校コード、対象年月の範囲内の訪問日を削除した後で登録します。
     * </p>
     * @param userCode ユーザコード
     * @param targetYearAndMonth 対象年月
     * @param visitSchedules 学校訪問日
     */
    fun save(
        userCode: User.UserCode,
        targetYearAndMonth: Schedule.TargetYearAndMonth,
        visitSchedules: VisitSchedules
    )

    /**
     * 取得.
     *
     * @param schoolCodes 学校コードList
     * @param targetYearAndMonth 対象年月
     * @return 該当レコード
     */
    fun getVisitSchedules(
        schoolCodes: List<School.SchoolCode>,
        targetYearAndMonth: Schedule.TargetYearAndMonth
    ): VisitSchedules
}
