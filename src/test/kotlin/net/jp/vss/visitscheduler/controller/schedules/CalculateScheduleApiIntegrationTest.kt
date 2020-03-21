package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.databind.ObjectMapper
import net.jp.vss.visitscheduler.controller.exceptions.HttpBadRequestException
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.WebApplicationContext

/**
 * CalculateScheduleApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class CalculateScheduleApiIntegrationTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()
    }

    @Test
    fun testCalculateSchedule() {
        // setup
        val request = CalculateScheduleApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        mockMvc.perform(post("/api/schedules/_calculate")
                .contentType(APPLICATION_JSON)
                .content(content))
            .andDo(print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.visit_schedules", hasSize<Int>(2)))
            .andExpect(jsonPath("$.visit_schedules[0].school_code", equalTo("SCHOOL-001")))
            .andExpect(jsonPath("$.visit_schedules[0].visit_day", equalTo(3)))
            .andExpect(jsonPath("$.visit_schedules[1].school_code", equalTo("SCHOOL-001")))
            .andExpect(jsonPath("$.visit_schedules[1].visit_day", equalTo(20)))
    }

    @Test
    fun testCalculateSchedule_InvalidParameter_400() {
        // setup
        val request = CalculateScheduleApiParameterFixtures.create()
            .copy(targetYearAndMonth = null)
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post("/api/schedules/_calculate")
                .contentType(APPLICATION_JSON)
                .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as MethodArgumentNotValidException
        assertThat(exception.bindingResult.allErrors).hasSize(1)
        assertThat(exception.bindingResult.fieldErrors[0].field).isEqualTo("targetYearAndMonth")
        assertThat(exception.bindingResult.fieldErrors[0].defaultMessage).isEqualTo("must not be null")
    }

    @Test
    fun testCalculateSchedule_CannotCalculate_400() {
        // setup
        val requestVisitRules = CalculateScheduleApiParameter.RequestVisitRules(30, 30)
        val request = CalculateScheduleApiParameterFixtures.create()
            .copy(requestVisitRules = requestVisitRules, tryCount = 1)
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post("/api/schedules/_calculate")
                .contentType(APPLICATION_JSON)
                .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as HttpBadRequestException
        assertThat(exception.message).isEqualTo("スケジュールを計算できませんでした。")
    }
}
