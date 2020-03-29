package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleRepository
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedules
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedulesRepository
import net.jp.vss.visitscheduler.domain.schools.School
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * ListSchoolScheduleUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class ListSchoolScheduleUseCaseImplTest {
    @Mock
    private lateinit var schoolScheduleRepo: SchoolScheduleRepository

    @Mock
    private lateinit var visitSchedulesRepo: VisitSchedulesRepository

    @InjectMocks
    private lateinit var sut: ListSchoolScheduleUseCaseImpl

    @Test
    fun testGetSchoolSchedule() {
        // setup
        val schoolCode = "SCHOOL-0001"
        val schoolSchedule = SchoolScheduleFixtures.create()
        whenever(schoolScheduleRepo.getSchoolSchedules(any(), any())).thenReturn(
            listOf(schoolSchedule) to
                mapOf(School.SchoolCode(schoolCode) to Schedule.ScheduleDate(LocalDate.parse("2018-11-30"))))
        val targetYearAndMonth = "2018-12"

        // execution
        val actual = sut.getSchoolSchedule(schoolCode, targetYearAndMonth)

        // verify
        val expected = SchoolScheduleUseCaseResult(
            listOf(
                SchoolScheduleUseCaseResult.SchoolScheduleEntry(
                    schoolSchedule.targetDate.date.dayOfMonth,
                    schoolSchedule.schoolScheduleDetail.memo,
                    schoolSchedule.schoolScheduleDetail.priority.name)),
            "2018-11-30")
        assertThat(actual).isEqualTo(expected)

        verify(schoolScheduleRepo).getSchoolSchedules(listOf(School.SchoolCode(schoolCode)),
            Schedule.TargetYearAndMonth(targetYearAndMonth))
    }

    @Test
    @DisplayName("先月の訪問日は明示的に未設定にした")
    fun testGetSchoolSchedule_SetNullValue() {
        // setup
        val schoolCode = "SCHOOL-0001"
        val schoolSchedule = SchoolScheduleFixtures.create()
        whenever(schoolScheduleRepo.getSchoolSchedules(any(), any())).thenReturn(
            listOf(schoolSchedule) to
                mapOf(School.SchoolCode(schoolCode) to null))
        val targetYearAndMonth = "2018-12"

        // execution
        val actual = sut.getSchoolSchedule(schoolCode, targetYearAndMonth)

        // verify
        val expected = SchoolScheduleUseCaseResult(
            listOf(
                SchoolScheduleUseCaseResult.SchoolScheduleEntry(
                    schoolSchedule.targetDate.date.dayOfMonth,
                    schoolSchedule.schoolScheduleDetail.memo,
                    schoolSchedule.schoolScheduleDetail.priority.name)),
            null)
        assertThat(actual).isEqualTo(expected)

        // 呼び出しは1回だけ
        verify(schoolScheduleRepo).getSchoolSchedules(listOf(School.SchoolCode(schoolCode)),
            Schedule.TargetYearAndMonth(targetYearAndMonth))
    }

    @Test
    @DisplayName("先月の訪問日は未設定で先月の訪問日自体は存在する")
    fun testGetSchoolSchedule_NullValue_ExistsVisitDateInLastMonth() {
        // setup
        val schoolCode = "SCHOOL-0001"
        val schoolSchedule = SchoolScheduleFixtures.create()
        whenever(schoolScheduleRepo.getSchoolSchedules(any(), any())).thenReturn(listOf(schoolSchedule) to mapOf())

        val visitSchedules = VisitSchedules(
            listOf(
                VisitSchedules.VisitSchedule(Schedule.ScheduleDate(LocalDate.parse("2018-11-28")),
                    School.SchoolCode(schoolCode))))
        whenever(visitSchedulesRepo.getVisitSchedules(any(), any())).thenReturn(visitSchedules)

        val targetYearAndMonth = "2018-12"

        // execution
        val actual = sut.getSchoolSchedule(schoolCode, targetYearAndMonth)

        // verify
        val expected = SchoolScheduleUseCaseResult(
            listOf(
                SchoolScheduleUseCaseResult.SchoolScheduleEntry(
                    schoolSchedule.targetDate.date.dayOfMonth,
                    schoolSchedule.schoolScheduleDetail.memo,
                    schoolSchedule.schoolScheduleDetail.priority.name)),
            "2018-11-28") // 2回目の問い合わせの結果から取得
        assertThat(actual).isEqualTo(expected)

        // 対象年月を変更して呼び出し
        verify(schoolScheduleRepo).getSchoolSchedules(listOf(School.SchoolCode(schoolCode)),
            Schedule.TargetYearAndMonth(targetYearAndMonth))
        verify(visitSchedulesRepo).getVisitSchedules(listOf(School.SchoolCode(schoolCode)),
            Schedule.TargetYearAndMonth("2018-11")) // 先月分の呼び出し
    }

    @Test
    @DisplayName("先月の訪問日は未設定で先月の訪問日自体は存在しない")
    fun testGetSchoolSchedule_NullValue_NotExistsVisitDateInLastMonth() {
        // setup
        val schoolCode = "SCHOOL-0001"
        val schoolSchedule1 = SchoolScheduleFixtures.create()
        whenever(schoolScheduleRepo.getSchoolSchedules(any(), any())).thenReturn(listOf(schoolSchedule1) to mapOf())
        whenever(visitSchedulesRepo.getVisitSchedules(any(), any())).thenReturn(VisitSchedules(listOf())) // 先月の訪問実績無し
        val targetYearAndMonth = "2018-12"

        // execution
        val actual = sut.getSchoolSchedule(schoolCode, targetYearAndMonth)

        // verify
        val expected = SchoolScheduleUseCaseResult(
            listOf(
                SchoolScheduleUseCaseResult.SchoolScheduleEntry(
                    schoolSchedule1.targetDate.date.dayOfMonth,
                    schoolSchedule1.schoolScheduleDetail.memo,
                    schoolSchedule1.schoolScheduleDetail.priority.name)),
            null) // 先月の訪問実績が無いので未設定
        assertThat(actual).isEqualTo(expected)

        // 対象年月を変更して呼び出し
        verify(schoolScheduleRepo).getSchoolSchedules(listOf(School.SchoolCode(schoolCode)),
            Schedule.TargetYearAndMonth(targetYearAndMonth))
        verify(visitSchedulesRepo).getVisitSchedules(listOf(School.SchoolCode(schoolCode)),
            Schedule.TargetYearAndMonth("2018-11")) // 先月分の呼び出し
    }
}
