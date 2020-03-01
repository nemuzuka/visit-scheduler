package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schedules.PrivateScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.PrivateScheduleRepository
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * ListPrivateScheduleUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class ListPrivateScheduleUseCaseImplTest {
    @Mock
    private lateinit var privateScheduleRepo: PrivateScheduleRepository

    @InjectMocks
    private lateinit var sut: ListPrivateScheduleUseCaseImpl

    @Test
    fun testGetPrivateSchedule() {
        // setup
        val privateSchedule = PrivateScheduleFixtures.create()
        whenever(privateScheduleRepo.getPrivateSchedules(any(), any())).thenReturn(listOf(privateSchedule))
        val userCode = "USER-0001"
        val targetYearAndMonth = "2018-12"

        // execution
        val actual = sut.getPrivateSchedule(userCode, targetYearAndMonth)

        // verify
        assertThat(actual).isEqualTo(listOf(PrivateScheduleUseCaseResult.of(privateSchedule)))
        verify(privateScheduleRepo).getPrivateSchedules(User.UserCode(userCode),
            Schedule.TargetYearAndMonth(targetYearAndMonth))
    }
}
