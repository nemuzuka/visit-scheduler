package net.jp.vss.visitscheduler.controller.users

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.users.UserFixtures
import net.jp.vss.visitscheduler.usecase.users.UpdateUserUseCase
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.hamcrest.CoreMatchers.`is`
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
 * UpdateUserApiController のテスト.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class UpdateUserApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var updateUserUseCase: UpdateUserUseCase

    @Test
    @WithMockUser
    fun testUpdateUser() {
        // setup
        val updatedUser = UserFixtures.create()
        whenever(updateUserUseCase.updateUser(any())).thenReturn(UserUseCaseResult.of(updatedUser))

        val parameter = UpdateUserApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)
        val userCode = "USER_00001"

        // execution
        mockMvc.perform(post("/api/users/{userCode}", userCode)
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(content))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("user_code").value(`is`(updatedUser.userCode.value)))

        verify(updateUserUseCase).updateUser(parameter.toParameter(userCode))
    }

    @Test
    @WithMockUser
    fun testUpdateUser_NotFoundUser_404() {
        // setup
        val exception = NotFoundException("dummy")
        whenever(updateUserUseCase.updateUser(any())).thenThrow(exception)

        val parameter = UpdateUserApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/users/dummy_0001")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(content))
            // verify
            .andExpect(status().isNotFound)
    }
}
