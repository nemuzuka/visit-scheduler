package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.schedules.PrivateSchedule
import net.jp.vss.visitscheduler.domain.schedules.PrivateScheduleRepository
import net.jp.vss.visitscheduler.domain.schedules.Schedule
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
 * CreatePrivateScheduleUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class SavePrivateScheduleUseCaseImplTest {

    @Mock
    private lateinit var privateScheduleRepo: PrivateScheduleRepository

    @InjectMocks
    private lateinit var sut: SavePrivateScheduleUseCaseImpl

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
    fun testCreatePrivateSchedule() {
        // setup
        doNothing().whenever(privateScheduleRepo).save(any(), any(), any())

        val input = SavePrivateScheduleUseCaseParameterFixtures.create()

        // execution
        sut.savePrivateSchedule(input)

        // verify
        argumentCaptor< User.UserCode, Schedule.TargetYearAndMonth, List<PrivateSchedule>>().let {
            (userCodeCaptor, targetYearAndMonthCaptor, privateSchedulesCaptor) ->

            verify(privateScheduleRepo).save(userCodeCaptor.capture(), targetYearAndMonthCaptor.capture(),
                privateSchedulesCaptor.capture())

            val capturedUserCode = userCodeCaptor.firstValue
            assertThat(capturedUserCode).isEqualTo(User.UserCode(input.createUserCode))

            val capturedTargetYearAndMonthCaptor = targetYearAndMonthCaptor.firstValue
            assertThat(capturedTargetYearAndMonthCaptor).isEqualTo(Schedule.TargetYearAndMonth(input.targetDateString))

            val capturedPrivateSchedules = privateSchedulesCaptor.firstValue
            assertThat(capturedPrivateSchedules).isEqualTo(input.toPrivateSchedules())
        }
    }
}
