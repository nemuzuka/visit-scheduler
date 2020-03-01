package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java.lang.IllegalStateException
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.usecase.schedules.SavePrivateScheduleUseCase
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResultFixtures
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * SavePrivateScheduleApiController のテスト.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class SavePrivateScheduleApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var getUserUseCase: GetUserUseCase

    @MockBean
    private lateinit var savePrivateScheduleUseCase: SavePrivateScheduleUseCase

    @Mock
    private lateinit var oAuth2AuthenticationToken: OAuth2AuthenticationToken

    @Mock
    private lateinit var principal: OAuth2User

    private lateinit var beforeAuthentication: Authentication

    @BeforeEach
    fun setUp() {
        beforeAuthentication = SecurityContextHolder.getContext().authentication
        SecurityContextHolder.getContext().authentication = oAuth2AuthenticationToken
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.getContext().authentication = beforeAuthentication
    }

    @Test
    @WithMockUser
    fun testSavePrivateSchedule() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principalName = "abcd-000A-0001"
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(authorizedClientRegistrationId)
        whenever(principal.name).thenReturn(principalName)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)

        val user = UserUseCaseResultFixtures.create()
        whenever(getUserUseCase.getUser(anyString(), anyString())).thenReturn(user)

        doNothing().whenever(savePrivateScheduleUseCase).savePrivateSchedule(any())

        val parameter = SavePrivateScheduleApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/private-schedules")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(content))
            // verify
            .andExpect(status().isOk)

        verify(getUserUseCase).getUser(authorizedClientRegistrationId, principalName)
        verify(savePrivateScheduleUseCase).savePrivateSchedule(parameter.toParameter(user.userCode))
    }

    @Test
    @WithMockUser
    fun testSavePrivateSchedule_ConflictScheduleCode_409() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principalName = "abcd-000A-0001"
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(authorizedClientRegistrationId)
        whenever(principal.name).thenReturn(principalName)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)

        val user = UserUseCaseResultFixtures.create()
        whenever(getUserUseCase.getUser(anyString(), anyString())).thenReturn(user)

        val exception = DuplicateException("dummy")
        whenever(savePrivateScheduleUseCase.savePrivateSchedule(any())).thenThrow(exception)

        val parameter = SavePrivateScheduleApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/private-schedules")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(content))
            // verify
            .andExpect(status().isConflict)
    }

    @Test
    @WithMockUser
    fun testSavePrivateSchedule_BadRequest_400() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principalName = "abcd-000A-0001"
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(authorizedClientRegistrationId)
        whenever(principal.name).thenReturn(principalName)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)

        val user = UserUseCaseResultFixtures.create()
        whenever(getUserUseCase.getUser(anyString(), anyString())).thenReturn(user)

        val exception = IllegalStateException("dummy")
        whenever(savePrivateScheduleUseCase.savePrivateSchedule(any())).thenThrow(exception)

        val parameter = SavePrivateScheduleApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/private-schedules")
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
    fun testSavePrivateSchedule_NotCsrfToken_403() {
        // setup
        val parameter = SavePrivateScheduleApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/private-schedules")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            // verify
            .andExpect(status().isForbidden)
    }
}
