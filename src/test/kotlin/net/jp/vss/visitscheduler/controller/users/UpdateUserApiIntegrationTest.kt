package net.jp.vss.visitscheduler.controller.users

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.whenever
import javax.validation.ConstraintViolationException
import net.jp.vss.visitscheduler.controller.exceptions.HttpNotFoundException
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
import org.slf4j.LoggerFactory
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.WebApplicationContext

/**
 * UpdateUserApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class UpdateUserApiIntegrationTest {

    companion object {
        const val PATH = "/api/users/{user_code}"
        private val log = LoggerFactory.getLogger(UpdateUserApiIntegrationTest::class.java)
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
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.getContext().authentication = beforeAuthentication
    }

    @Test
    fun testUpdateUser() {
        // setup
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId)
            .thenReturn(CreateUserApiIntegrationTest.AUTORIZED_CLIENT_REGISTRATION_ID)
        whenever(principal.name).thenReturn(CreateUserApiIntegrationTest.PRINCIPAL_NAME)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)
        SecurityContextHolder.getContext().authentication = oAuth2AuthenticationToken

        val createRequest = CreateUserApiParameterFixtures.create()
        userIntegrationHelper.createUser(mockMvc, createRequest)
        val userCode = createRequest.userCode

        val request = UpdateUserApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        mockMvc.perform(post(PATH, userCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            // verify
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.user_code", equalTo(userCode)))
            .andExpect(jsonPath("$.user_name", equalTo(request.userName)))

        // 永続化していること
        val updatedUser = jdbcUserRepo.lockUser(User.UserCode(userCode!!))
        assertThat(updatedUser.userDetail)
            .returns(request.userName, User.UserDetail::userName)
    }

    @Test
    fun testUpdateUser_NotFoundUser_404() {
        // setup
        val request = UpdateUserApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH, "absent_user_code")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        val exception = actual.resolvedException as HttpNotFoundException
        assertThat(exception.message).isEqualTo("User(absent_user_code) は存在しません")
    }

    @Test
    fun testUpdateUser_InvalidJsonParameter_400() {
        // setup
        val request = UpdateUserApiParameterFixtures.create().copy(userName = null)
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH, "USER_A_0001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as MethodArgumentNotValidException
        assertThat(exception.bindingResult.allErrors).hasSize(1)
        assertThat(exception.bindingResult.fieldErrors[0].field).isEqualTo("userName")
        assertThat(exception.bindingResult.fieldErrors[0].defaultMessage).isEqualTo("must not be null")
    }

    @Test
    fun testUpdateUser_InvalidPathParameter_400() {
        // setup
        val request = UpdateUserApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH, "_USER_001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as ConstraintViolationException
        assertThat(exception.message).isEqualTo("updateUser.userCode: must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }
}
