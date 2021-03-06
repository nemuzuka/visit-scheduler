package net.jp.vss.visitscheduler.controller.schools

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.usecase.schools.CreateSchoolUseCase
import net.jp.vss.visitscheduler.usecase.schools.SchoolUseCaseResultFixtures
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResultFixtures
import org.hamcrest.CoreMatchers.`is`
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * CreateSchoolApiController のテスト.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class CreateSchoolApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var getUserUseCase: GetUserUseCase

    @MockBean
    private lateinit var createSchoolUseCase: CreateSchoolUseCase

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
    fun testCreateSchool() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principalName = "abcd-000A-0001"
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(authorizedClientRegistrationId)
        whenever(principal.name).thenReturn(principalName)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)

        val user = UserUseCaseResultFixtures.create()
        whenever(getUserUseCase.getUser(anyString(), anyString())).thenReturn(user)

        val createdSchool = SchoolUseCaseResultFixtures.create()
        whenever(createSchoolUseCase.createSchool(any())).thenReturn(createdSchool)

        val parameter = CreateSchoolApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/schools")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(content))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("school_code").value(`is`(createdSchool.schoolCode)))

        verify(getUserUseCase).getUser(authorizedClientRegistrationId, principalName)
        verify(createSchoolUseCase).createSchool(parameter.toParameter(user.userCode))
    }

    @Test
    @WithMockUser
    fun testCreateSchool_ConflictSchoolCode_409() {
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
        whenever(createSchoolUseCase.createSchool(any())).thenThrow(exception)

        val parameter = CreateSchoolApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/schools")
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
        val parameter = CreateSchoolApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/schools")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            // verify
            .andExpect(status().isForbidden)
    }
}
