package net.jp.vss.visitscheduler.controller.users

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
 * User の IntegrationTest のヘルパー.
 */
@Component
class UserIntegrationHelper {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val CREATE_USER_PATH = "/api/users"
    }

    /**
     * CreateUser 呼び出し.
     *
     * @param mockMvc MockMvc
     * @param parameter パラメータ
     * @return レスポンス
     */
    fun createUser(mockMvc: MockMvc, parameter: CreateUserApiParameter): MvcResult {

        val content = objectMapper.writeValueAsString(parameter)
        val response = mockMvc.perform(MockMvcRequestBuilders.post(CREATE_USER_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andReturn()
        assertThat(response.response.status).isEqualTo(HttpStatus.OK.value())
        return response
    }
}
