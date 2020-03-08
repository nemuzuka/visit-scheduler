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
 * SchoolSchedule の IntegrationTest のヘルパー.
 */
@Component
class SchoolScheduleIntegrationHelper {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val SAVE_SCHOOL_SCHEDULE_PATH = "/api/school-schedules"
        const val LIST_SCHOOL_SCHEDULE_PATH =
            "/api/school-schedules/_by-school/{school_code}?target_year_and_month={target_year_and_month}"
    }

    /**
     * SaveSchoolSchedule 呼び出し.
     *
     * @param mockMvc MockMvc
     * @param parameter パラメータ
     * @return レスポンス
     */
    fun savePrivateSchedule(mockMvc: MockMvc, parameter: SaveSchoolScheduleApiParameter): MvcResult {

        val content = objectMapper.writeValueAsString(parameter)
        val response = mockMvc.perform(MockMvcRequestBuilders.post(SAVE_SCHOOL_SCHEDULE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andReturn()
        assertThat(response.response.status).isEqualTo(HttpStatus.OK.value())
        return response
    }
}
