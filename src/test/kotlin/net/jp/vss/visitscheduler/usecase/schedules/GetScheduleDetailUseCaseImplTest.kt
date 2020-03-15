package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.ScheduleDetailFixtures
import net.jp.vss.visitscheduler.domain.schedules.ScheduleDetailRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * GetScheduleDetailUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class GetScheduleDetailUseCaseImplTest {

    @Mock
    private lateinit var scheduleDetailRepo: ScheduleDetailRepository

    @InjectMocks
    private lateinit var sut: GetScheduleDetailUseCaseImpl

    @Test
    fun testGetScheduleDetail() {
        // setup
        val scheduleDetail = ScheduleDetailFixtures.create()
        whenever(scheduleDetailRepo.getScheduleDetail(any(), any())).thenReturn(scheduleDetail)
        val scheduleCode = "SHEDULE_001"
        val userCode = "USER_001"

        // execution
        val actual = sut.getScheduleDetail(scheduleCode, userCode)

        // verify
        assertThat(actual).isEqualTo(ScheduleDetailUseCaseResult.of(scheduleDetail))

        verify(scheduleDetailRepo).getScheduleDetail(Schedule.ScheduleCode(scheduleCode), User.UserCode(userCode))
    }
}
