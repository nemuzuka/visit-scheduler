package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolSchedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleRepository
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * SaveSchoolScheduleUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class SaveSchoolScheduleUseCaseImplTest {

    @Mock
    private lateinit var schoolScheduleRepo: SchoolScheduleRepository

    @InjectMocks
    private lateinit var sut: SaveSchoolScheduleUseCaseImpl

    companion object {
        const val NOW = 1546268400002L
    }

    @BeforeEach
    fun setUp() {
        DatetimeUtils.setDummyDatetimeResource(NOW)
    }

    @AfterEach
    fun tearDown() {
        DatetimeUtils.clearDummyDatetimeResource()
    }

    @Test
    fun testCreateSchoolSchedule() {
        // setup
        doNothing().whenever(schoolScheduleRepo).save(any(), any(), any(), any(), any())

        val input = SaveSchoolScheduleUseCaseParameterFixtures.create()

        // execution
        sut.saveSchoolSchedule(input)

        // verify
        argumentCaptor< User.UserCode, School.SchoolCode, Schedule.TargetYearAndMonth,
            List<SchoolSchedule>, Schedule.ScheduleDate>().let {
            (userCodeCaptor, schoolCodeCaptor, targetYearAndMonthCaptor, schoolSchedulesCaptor, scheduleDateCaptor) ->

            verify(schoolScheduleRepo).save(userCodeCaptor.capture(), schoolCodeCaptor.capture(),
                targetYearAndMonthCaptor.capture(), schoolSchedulesCaptor.capture(), scheduleDateCaptor.capture())

            val capturedUserCode = userCodeCaptor.firstValue
            assertThat(capturedUserCode).isEqualTo(User.UserCode(input.createUserCode))

            val capturedSchoolCode = schoolCodeCaptor.firstValue
            assertThat(capturedSchoolCode).isEqualTo(School.SchoolCode(input.schoolCode))

            val capturedTargetYearAndMonthCaptor = targetYearAndMonthCaptor.firstValue
            assertThat(capturedTargetYearAndMonthCaptor).isEqualTo(Schedule.TargetYearAndMonth(input.targetDateString))

            val capturedSchoolSchedules = schoolSchedulesCaptor.firstValue
            assertThat(capturedSchoolSchedules).isEqualTo(input.toSchoolSchedules())

            val capturedLastMonthVisitDate = scheduleDateCaptor.firstValue
            assertThat(capturedLastMonthVisitDate).isEqualTo(input.lastMonthVisitDate)
        }
    }
}
