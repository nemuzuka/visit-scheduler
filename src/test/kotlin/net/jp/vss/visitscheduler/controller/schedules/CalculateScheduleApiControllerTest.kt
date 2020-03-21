package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import java.lang.IllegalStateException
import net.jp.vss.visitscheduler.usecase.schedules.CalculateUseCase
import net.jp.vss.visitscheduler.usecase.schedules.CalculateUseCaseResultFixtures
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * CalculateScheduleApiController のテスト.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class CalculateScheduleApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var calculateUseCase: CalculateUseCase

    @Test
    @WithMockUser
    fun testCalculateSchedule() {
        // setup
        val calculateResult = CalculateUseCaseResultFixtures.create()
        whenever(calculateUseCase.calculateSchedule(any())).thenReturn(calculateResult)

        val parameter = CalculateScheduleApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/schedules/_calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(content))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.visit_schedules", hasSize<Int>(1)))
            .andExpect(jsonPath("$.visit_schedules[0].school_code").value(`is`("school_001")))
            .andExpect(jsonPath("$.visit_schedules[0].visit_day").value(`is`(5)))
    }

    @Test
    @WithMockUser
    fun testCalculateSchedule_ThrowISE_400() {
        // setup
        val exception = IllegalStateException("dummy")
        whenever(calculateUseCase.calculateSchedule(any())).thenThrow(exception)

        val parameter = CalculateScheduleApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/schedules/_calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .content(content))
            // verify
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser
    fun testCalculateSchedule_ThrowIAE_400() {
        // setup
        val exception = IllegalArgumentException("dummy")
        whenever(calculateUseCase.calculateSchedule(any())).thenThrow(exception)

        val parameter = CalculateScheduleApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/schedules/_calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .content(content))
            // verify
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser
    fun testCalculateSchedule_ThrowCalculateException_400() {
        // setup
        val exception = CalculateUseCase.CalculateException("dummy")
        whenever(calculateUseCase.calculateSchedule(any())).thenThrow(exception)

        val parameter = CalculateScheduleApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/schedules/_calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .content(content))
            // verify
            .andExpect(status().isBadRequest)
    }

    /**
     * CSRF Token 未設定.
     */
    @Test
    @WithMockUser
    fun testCalculateSchedule_NotCsrfToken_403() {
        // setup
        val parameter = CreateScheduleApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/schedules/_calculate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            // verify
            .andExpect(status().isForbidden)
    }
}
