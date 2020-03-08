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
     * @throws DuplicateException 既に存在する
     * @throws IllegalStateException 指定した学校が存在しない
     */
    fun save(
        userCode: User.UserCode,
        schoolCode: School.SchoolCode,
        targetYearAndMonth: Schedule.TargetYearAndMonth,
        schoolSchedules: List<SchoolSchedule>
    )

    /**
     * 取得.
     *
     * @param schoolCodes 学校コードリスト
     * @param targetYearAndMonth 対象年月
     * @return 該当レコード
     */
    fun getSchoolSchedules(
        schoolCodes: List<School.SchoolCode>,
        targetYearAndMonth: Schedule.TargetYearAndMonth
    ): List<SchoolSchedule>
}
