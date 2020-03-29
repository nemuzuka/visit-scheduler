package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.ScheduleRepository
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * UpdateSchoolCodeAndCalculationTargetUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class UpdateSchoolCodeAndCalculationTargetUseCaseImplTest {

    @Mock
    private lateinit var scheduleRepo: ScheduleRepository

    @InjectMocks
    private lateinit var sut: UpdateSchoolCodeAndCalculationTargetUseCaseImpl

    @Test
    fun testUpdate() {
        // setup
        val parameter = UpdateSchoolCodeAndCalculationTargetUseCaseParameter(
            Schedule.ScheduleCode("SCHE-001"),
            User.UserCode("U-0001"),
            listOf(SchoolCodeAndCalculationTarget("SCHOOL-001", true)))
        doNothing().whenever(scheduleRepo).updateSchoolCodeAndCalculationTargets(any(), any(), any())

        // execution
        sut.update(parameter)

        // verify
        argumentCaptor<Schedule.ScheduleCode, User.UserCode, Schedule.SchoolCodeAndCalculationTargets>().let {
            (scheduleCodeCaptor, userCodeCaptor, schoolCodeAndCalculationTargetsCaptor) ->

            verify(scheduleRepo).updateSchoolCodeAndCalculationTargets(scheduleCodeCaptor.capture(),
                userCodeCaptor.capture(), schoolCodeAndCalculationTargetsCaptor.capture())

            assertThat(scheduleCodeCaptor.firstValue).isEqualTo(parameter.scheduleCode)
            assertThat(userCodeCaptor.firstValue).isEqualTo(parameter.userCode)
            assertThat(schoolCodeAndCalculationTargetsCaptor.firstValue)
                .isEqualTo(Schedule.SchoolCodeAndCalculationTargets(
                    listOf(Schedule.SchoolCodeAndCalculationTarget(
                        School.SchoolCode(parameter.schoolCodeAndCalculationTargets[0].schoolCodeValue),
                        parameter.schoolCodeAndCalculationTargets[0].calculationTarget))))
        }
    }
}
