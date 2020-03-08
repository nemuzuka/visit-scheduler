package net.jp.vss.visitscheduler.usecase.schedules

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleFixtures
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleRepository
import net.jp.vss.visitscheduler.domain.schools.School
import org.assertj.core.api.Assertions.assertThat
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

    @InjectMocks
    private lateinit var sut: ListSchoolScheduleUseCaseImpl

    @Test
    fun testGetSchoolSchedule() {
        // setup
        val schoolSchedule = SchoolScheduleFixtures.create()
        whenever(schoolScheduleRepo.getSchoolSchedules(any(), any())).thenReturn(listOf(schoolSchedule))
        val schoolCode = "SCHOOL-0001"
        val targetYearAndMonth = "2018-12"

        // execution
        val actual = sut.getSchoolSchedule(schoolCode, targetYearAndMonth)

        // verify
        assertThat(actual).isEqualTo(listOf(SchoolScheduleUseCaseResult.of(schoolSchedule)))
        verify(schoolScheduleRepo).getSchoolSchedules(listOf(School.SchoolCode(schoolCode)),
            Schedule.TargetYearAndMonth(targetYearAndMonth))
    }
}
