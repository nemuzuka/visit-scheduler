package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schedules.ScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.ScheduleRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * ListScheduleUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class ListScheduleUseCaseImplTest {
    @Mock
    private lateinit var scheduleRepo: ScheduleRepository

    @InjectMocks
    private lateinit var sut: ListScheduleUseCaseImpl

    @Test
    fun testAllSchedules() {
        // setup
        val schedule = ScheduleFixtures.create()
        whenever(scheduleRepo.allSchedules(any())).thenReturn(listOf(schedule))
        val userCode = "USER-0001"

        // execution
        val actual = sut.allSchedules(userCode)

        // verify
        assertThat(actual).isEqualTo(listOf(ScheduleUseCaseResult.of(schedule)))
        verify(scheduleRepo).allSchedules(User.UserCode(userCode))
    }
}
