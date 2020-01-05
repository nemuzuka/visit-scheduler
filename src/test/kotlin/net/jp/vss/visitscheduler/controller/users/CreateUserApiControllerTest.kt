package net.jp.vss.visitscheduler.controller.users

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.users.UserFixtures
import net.jp.vss.visitscheduler.usecase.users.CreateUserUseCase
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * CreateUserApiController のテスト.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class CreateUserApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var createUserUseCase: CreateUserUseCase

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
    fun testCreateUser() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principalName = "abcd-000A-0001"
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(authorizedClientRegistrationId)
        whenever(principal.name).thenReturn(principalName)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)

        val createdUser = UserFixtures.create()
        whenever(createUserUseCase.createUser(any())).thenReturn(UserUseCaseResult.of(createdUser))

        val parameter = CreateUserApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(content))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("user_code").value(`is`(createdUser.userCode.value)))

        verify(createUserUseCase).createUser(parameter.toParameter(authorizedClientRegistrationId, principalName))
    }

    @Test
    @WithMockUser
    fun testCreateUser_ConflictUserCode_409() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principalName = "abcd-000A-0001"
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(authorizedClientRegistrationId)
        whenever(principal.name).thenReturn(principalName)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)

        val exception = DuplicateException("dummy")
        whenever(createUserUseCase.createUser(any())).thenThrow(exception)

        val parameter = CreateUserApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(content))
            // verify
            .andExpect(status().isConflict)
    }

    /**
     * CSRF Token 未設定.
     */
    @Test
    @WithMockUser
    fun testCreateUser_NotCsrfToken_403() {
        // setup
        val parameter = CreateUserApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            // verify
            .andExpect(status().isForbidden)
    }
}
