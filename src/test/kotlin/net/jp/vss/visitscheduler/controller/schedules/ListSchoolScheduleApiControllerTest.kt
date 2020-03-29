package net.jp.vss.visitscheduler.controller.schedules

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.SchoolScheduleFixtures
import net.jp.vss.visitscheduler.usecase.schedules.ListSchoolScheduleUseCase
import net.jp.vss.visitscheduler.usecase.schedules.SchoolScheduleUseCaseResult
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * ListSchoolScheduleApiController のテスト.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class ListSchoolScheduleApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var listSchoolScheduleUseCase: ListSchoolScheduleUseCase

    @Test
    @WithMockUser
    fun testListSchoolSchedule() {
        // setup
        val schoolSchedule = SchoolScheduleFixtures.create()
        whenever(listSchoolScheduleUseCase.getSchoolSchedule(anyString(), anyString()))
            .thenReturn(SchoolScheduleUseCaseResult.of(listOf(schoolSchedule),
                Schedule.ScheduleDate(LocalDate.parse("2019-12-28"))))
        val targetYearAndMonth = "2019-12"
        val schoolCode = "SCHOOL_0001"

        // execution
        mockMvc.perform(
            get("/api/school-schedules/_by-school/{schoolCode}?target_year_and_month={targetYearAndMonth}",
                schoolCode, targetYearAndMonth))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.school_schedules", hasSize<Int>(1)))
            .andExpect(jsonPath("$.school_schedules[0].target_day").value(`is`(21)))
            .andExpect(jsonPath("$.last_month_visit_date").value(`is`("2019-12-28")))

        verify(listSchoolScheduleUseCase).getSchoolSchedule(schoolCode, targetYearAndMonth)
    }
}
