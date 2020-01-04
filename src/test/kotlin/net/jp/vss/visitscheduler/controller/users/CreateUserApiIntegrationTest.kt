package net.jp.vss.visitscheduler.controller.users

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.controller.exceptions.HttpConflictException
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.users.JdbcUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.WebApplicationContext

/**
 * CreateUserApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class CreateUserApiIntegrationTest {

    companion object {
        const val PATH = "/api/users"
        const val AUTORIZED_CLIENT_REGISTRATION_ID = "google"
        const val PRINCIPAL_NAME = "abcd-000A-0001"
    }

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var jdbcUserRepo: JdbcUserRepository

    @Autowired
    private lateinit var userIntegrationHelper: UserIntegrationHelper

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var oAuth2AuthenticationToken: OAuth2AuthenticationToken

    @Mock
    private lateinit var principal: OAuth2User

    private var beforeAuthentication: Authentication? = null

    @BeforeEach
    fun setUp() {
        flyway.clean()
        flyway.migrate()

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()

        beforeAuthentication = SecurityContextHolder.getContext().authentication

        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(AUTORIZED_CLIENT_REGISTRATION_ID)
        whenever(principal.name).thenReturn(PRINCIPAL_NAME)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)
        SecurityContextHolder.getContext().authentication = oAuth2AuthenticationToken
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.getContext().authentication = beforeAuthentication
    }

    @Test
    fun testCreateUser() {
        // setup
        val request = CreateUserApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        mockMvc.perform(post(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.user_code", equalTo(request.userCode)))
            .andExpect(jsonPath("$.user_name", equalTo(request.userName)))

        // 永続化していること
        val createdUser = jdbcUserRepo.getUserOrNull(User.UserCode(request.userCode!!))!!
        assertThat(createdUser)
            .returns(User.UserCode(request.userCode!!), User::userCode)
    }

    @Test
    fun testCreateUser_InvalidParameter_400() {
        // setup
        val request = CreateUserApiParameterFixtures.create().copy(userCode = null)
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as MethodArgumentNotValidException
        assertThat(exception.bindingResult.allErrors).hasSize(1)
        assertThat(exception.bindingResult.fieldErrors[0].field).isEqualTo("userCode")
        assertThat(exception.bindingResult.fieldErrors[0].defaultMessage).isEqualTo("must not be null")
    }

    @Test
    fun testCreateUser_ExsitsUserCode_409() {
        // setup
        val request = CreateUserApiParameterFixtures.create()
        userIntegrationHelper.createUser(mockMvc, request)

        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.CONFLICT.value())
        val exception = actual.resolvedException as HttpConflictException
        assertThat(exception.message).isEqualTo("User(${request.userCode}) は既に存在しています")
    }
}
