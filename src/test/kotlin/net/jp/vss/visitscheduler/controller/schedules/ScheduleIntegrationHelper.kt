package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

/**
 * Schedule の IntegrationTest のヘルパー.
 */
@Component
class ScheduleIntegrationHelper {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val CREATE_SCHEDULE_PATH = "/api/schedules"
        const val GET_SCHEDULE_PATH = "/api/schedules/{schedule_code}"
        const val LIST_SCHEDULE_PATH = "/api/schedules"
        const val UPDATE_SCHEDULE_PATH = "/api/schedules/{schedule_code}"
    }

    /**
     * CreateSchedule 呼び出し.
     *
     * @param mockMvc MockMvc
     * @param parameter パラメータ
     * @return レスポンス
     */
    fun createSchedule(mockMvc: MockMvc, parameter: CreateScheduleApiParameter): MvcResult {

        val content = objectMapper.writeValueAsString(parameter)
        val response = mockMvc.perform(MockMvcRequestBuilders.post(CREATE_SCHEDULE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andReturn()
        assertThat(response.response.status).isEqualTo(HttpStatus.OK.value())
        return response
    }
}
