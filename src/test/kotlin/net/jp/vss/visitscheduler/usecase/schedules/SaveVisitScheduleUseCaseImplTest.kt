package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedules
import net.jp.vss.visitscheduler.domain.schedules.VisitSchedulesRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * SaveVisitScheduleUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class SaveVisitScheduleUseCaseImplTest {

    @Mock
    private lateinit var visitSchedulesRepo: VisitSchedulesRepository

    @InjectMocks
    private lateinit var sut: SaveVisitScheduleUseCaseImpl

    @Test
    fun testSaveVisitSchedules() {
        // setup
        doNothing().whenever(visitSchedulesRepo).save(any(), any(), any())

        val input = SaveVisitScheduleUseCaseParameterFixtures.create()

        // execution
        sut.saveVisitSchedules(input)

        // verify
        argumentCaptor< User.UserCode, Schedule.TargetYearAndMonth, VisitSchedules>().let {
            (userCodeCaptor, targetYearAndMonthCaptor, visitSchedulesCaptor) ->

            verify(visitSchedulesRepo).save(userCodeCaptor.capture(), targetYearAndMonthCaptor.capture(),
                visitSchedulesCaptor.capture())

            val capturedUserCode = userCodeCaptor.firstValue
            assertThat(capturedUserCode).isEqualTo(input.userCode)

            val capturedTargetYearAndMonthCaptor = targetYearAndMonthCaptor.firstValue
            assertThat(capturedTargetYearAndMonthCaptor).isEqualTo(Schedule.TargetYearAndMonth(input.targetDateString))

            val capturedVisitSchedules = visitSchedulesCaptor.firstValue
            assertThat(capturedVisitSchedules).isEqualTo(input.toVisitSchedules())
        }
    }
}
