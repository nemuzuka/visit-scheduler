package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedules
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User

/**
 * SaveVisitScheduleUseCase のパラメータ.
 *
 * @param targetDateString 対象年月文字列(yyyy-MM)
 * @property userCode 登録ユーザコード
 * @property visitDayAndSchoolCodes 学校毎の訪問日リスト
 */
data class SaveVisitScheduleUseCaseParameter(
    val targetDateString: String,
    val userCode: User.UserCode,
    val visitDayAndSchoolCodes: List<VisitDayAndSchoolCode>
) {

    /**
     * SchoolSchedule リスト生成.
     *
     * @return SchoolSchedule リスト
     */
    fun toVisitSchedules(): VisitSchedules {
        val targetYearAndMonth = Schedule.TargetYearAndMonth(targetDateString)
        return VisitSchedules(visitDayAndSchoolCodes.map {
            VisitSchedules.VisitSchedule(Schedule.ScheduleDate.of(targetYearAndMonth, it.visitDay), it.schoolCode)
        }.toList())
    }

    /**
     * 学校毎の訪問日.
     *
     * @property visitDay 訪問日(対象年月の1日から月末まで有効)
     * @property schoolCode 学校コード
     */
    data class VisitDayAndSchoolCode(
        val schoolCode: School.SchoolCode,
        val visitDay: Int
    )
}
