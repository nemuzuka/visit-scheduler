package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.ScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.ScheduleRepository
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
 * CreateScheduleUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class CreateScheduleUseCaseImplTest {

    @Mock
    private lateinit var scheduleRepo: ScheduleRepository

    @InjectMocks
    private lateinit var sut: CreateScheduleUseCaseImpl

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
    fun testCreateSchedule() {
        // setup
        val createdSchedule = ScheduleFixtures.create()
        whenever(scheduleRepo.create(any(), any())).thenReturn(createdSchedule)

        val input = CreateScheduleUseCaseParameterFixtures.create()

        // execution
        val actual = sut.createSchedule(input)

        // verify
        assertThat(actual).isEqualTo(ScheduleUseCaseResult.of(createdSchedule))

        // org.mockito.ArgumentCaptor を使用する代わり
        argumentCaptor< Schedule, Schedule.SchoolCodeAndCalculationTargets>().let {
            (scheduleCaptor, schoolCodeAndCalculationTargetCaptor) ->

            verify(scheduleRepo).create(scheduleCaptor.capture(), schoolCodeAndCalculationTargetCaptor.capture())

            val capturedSchedule = scheduleCaptor.firstValue // getValue と同意
            val expectedSchedule = Schedule(scheduleCode = Schedule.ScheduleCode(input.scheduleCode),
                userCode = User.UserCode(input.createUserCode),
                scheduleDetail = Schedule.ScheduleDetail(attributes = Attributes(input.attributes!!)),
                targetYearAndMonth = Schedule.TargetYearAndMonth(input.targetDateString),
                resourceAttributes = ResourceAttributes(createUserCode = input.createUserCode,
                    createAt = NOW,
                    lastUpdateUserCode = input.createUserCode,
                    lastUpdateAt = NOW,
                    version = 0L))
            assertThat(capturedSchedule).isEqualTo(expectedSchedule)

            val capturedSchoolCodeAndCalculationTargets = schoolCodeAndCalculationTargetCaptor.firstValue
            val expectedSchoolCodeAndCalculationTargets = Schedule.SchoolCodeAndCalculationTargets(
                listOf(
                    Schedule.SchoolCodeAndCalculationTarget(
                        schoolCode = School.SchoolCode(input.schoolCodeAndCalculationTargets[0].schoolCodeValue),
                        calculationTarget = input.schoolCodeAndCalculationTargets[0].calculationTarget),
                    Schedule.SchoolCodeAndCalculationTarget(
                        schoolCode = School.SchoolCode(input.schoolCodeAndCalculationTargets[1].schoolCodeValue),
                        calculationTarget = input.schoolCodeAndCalculationTargets[1].calculationTarget))
            )
            assertThat(capturedSchoolCodeAndCalculationTargets).isEqualTo(expectedSchoolCodeAndCalculationTargets)
        }
    }
}
