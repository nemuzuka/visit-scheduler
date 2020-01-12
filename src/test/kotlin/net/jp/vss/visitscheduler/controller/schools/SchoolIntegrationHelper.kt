package net.jp.vss.visitscheduler.controller.schools

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
 * School の IntegrationTest のヘルパー.
 */
@Component
class SchoolIntegrationHelper {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val CREATE_SCHOOL_PATH = "/api/schools"
        const val GET_SCHOOL_PATH = "/api/schools/{school_code}"
        const val LIST_SCHOOL_PATH = "/api/schools"
        const val UPDATE_SCHOOL_PATH = "/api/schools/{school_code}"
    }

    /**
     * CreateSchool 呼び出し.
     *
     * @param mockMvc MockMvc
     * @param parameter パラメータ
     * @return レスポンス
     */
    fun createSchool(mockMvc: MockMvc, parameter: CreateSchoolApiParameter): MvcResult {

        val content = objectMapper.writeValueAsString(parameter)
        val response = mockMvc.perform(MockMvcRequestBuilders.post(CREATE_SCHOOL_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andReturn()
        assertThat(response.response.status).isEqualTo(HttpStatus.OK.value())
        return response
    }
}
