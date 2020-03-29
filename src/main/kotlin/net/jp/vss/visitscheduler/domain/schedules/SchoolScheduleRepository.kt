package net.jp.vss.visitscheduler.domain.schedules

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User

/**
 * SchoolSchedule のリポジトリ.
 */
interface SchoolScheduleRepository {

    /**
     * 永続化.
     *
     * <p>
     * 学校コード、対象年月の範囲内のスケジュールを削除した後で登録します。
     * </p>
     * @param userCode ユーザコード
     * @param schoolCode 学校コード
     * @param targetYearAndMonth 対象年月
     * @param schoolSchedules 対象 SchoolSchedule リスト
     * @param lastMonthVisitDate 先月の最終訪問日
     * @throws DuplicateException 既に存在する
     * @throws IllegalStateException 指定した学校が存在しない
     */
    fun save(
        userCode: User.UserCode,
        schoolCode: School.SchoolCode,
        targetYearAndMonth: Schedule.TargetYearAndMonth,
        schoolSchedules: List<SchoolSchedule>,
        lastMonthVisitDate: Schedule.ScheduleDate?
    )

    /**
     * 取得.
     *
     * <p>
     * 戻り値の先月の最終訪問日 Map に未設定の SchoolCode は、ユーザから未設定という意味を持ちます
     * </p>
     * @param schoolCodes 学校コードリスト
     * @param targetYearAndMonth 対象年月
     * @return 該当レコード(first: SchoolSchedule リスト, second: 先月の最終訪問日 Map)
     */
    fun getSchoolSchedules(
        schoolCodes: List<School.SchoolCode>,
        targetYearAndMonth: Schedule.TargetYearAndMonth
    ): Pair<List<SchoolSchedule>, Map<School.SchoolCode, Schedule.ScheduleDate?>>
}
