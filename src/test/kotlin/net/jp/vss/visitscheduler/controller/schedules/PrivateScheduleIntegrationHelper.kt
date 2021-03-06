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
 * PrivateSchedule の IntegrationTest のヘルパー.
 */
@Component
class PrivateScheduleIntegrationHelper {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val SAVE_PRIVATE_SCHEDULE_PATH = "/api/private-schedules"
        const val LIST_PRIVATE_SCHEDULE_PATH = "/api/private-schedules"
    }

    /**
     * SavePrivateSchedule 呼び出し.
     *
     * @param mockMvc MockMvc
     * @param parameter パラメータ
     * @return レスポンス
     */
    fun savePrivateSchedule(mockMvc: MockMvc, parameter: SavePrivateScheduleApiParameter): MvcResult {

        val content = objectMapper.writeValueAsString(parameter)
        val response = mockMvc.perform(MockMvcRequestBuilders.post(SAVE_PRIVATE_SCHEDULE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andReturn()
        assertThat(response.response.status).isEqualTo(HttpStatus.OK.value())
        return response
    }
}
