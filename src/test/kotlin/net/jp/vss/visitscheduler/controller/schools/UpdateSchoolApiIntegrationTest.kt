package net.jp.vss.visitscheduler.controller.schools

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.whenever
import javax.validation.ConstraintViolationException
import net.jp.vss.visitscheduler.IntegrationHelper
import net.jp.vss.visitscheduler.controller.exceptions.HttpConflictException
import net.jp.vss.visitscheduler.controller.exceptions.HttpNotFoundException
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.schools.JdbcSchoolRepository
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.WebApplicationContext

/**
 * UpdateSchoolApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class UpdateSchoolApiIntegrationTest {

    companion object {
        const val AUTORIZED_CLIENT_REGISTRATION_ID = "google"
        const val PRINCIPAL_NAME = "abcd-000A-0001"
        const val USER_CODE = "USER-0001"
    }

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var schoolIntegrationHelper: SchoolIntegrationHelper

    @Autowired
    private lateinit var integrationHelper: IntegrationHelper

    @Autowired
    private lateinit var jdbcSchoolRepo: JdbcSchoolRepository

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var oAuth2AuthenticationToken: OAuth2AuthenticationToken

    @Mock
    private lateinit var principal: OAuth2User

    private var beforeAuthentication: Authentication? = null

    private var dummyUser: UserUseCaseResult? = null

    @BeforeEach
    fun setUp() {
        flyway.clean()
        flyway.migrate()

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()

        beforeAuthentication = SecurityContextHolder.getContext().authentication

        dummyUser = integrationHelper.createUser(USER_CODE, AUTORIZED_CLIENT_REGISTRATION_ID, PRINCIPAL_NAME)

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
    fun testUpdateSchool() {
        // setup
        val createRequest = CreateSchoolApiParameterFixtures.create()
        schoolIntegrationHelper.createSchool(mockMvc, createRequest)

        val request = UpdateSchoolApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        val path = "${SchoolIntegrationHelper.UPDATE_SCHOOL_PATH}?version=0"
        mockMvc.perform(post(path, createRequest.schoolCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(MockMvcResultHandlers.print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.school_code", Matchers.equalTo(createRequest.schoolCode)))
            .andExpect(jsonPath("$.name", Matchers.equalTo(request.name)))
            .andExpect(jsonPath("$.memo", Matchers.equalTo(request.memo)))
            .andExpect(jsonPath("$.attributes.hoge", Matchers.equalTo("super_hoge")))
            .andExpect(jsonPath("$.version", Matchers.equalTo(1)))

        // 永続化していること
        val updatedSchool = jdbcSchoolRepo.getSchool(School.SchoolCode(createRequest.schoolCode!!),
            User.UserCode(dummyUser!!.userCode))
        assertThat(updatedSchool.schoolDetail)
            .returns(request.name, School.SchoolDetail::name)
            .returns(request.memo, School.SchoolDetail::memo)
            .returns(Attributes(request.attributes!!), School.SchoolDetail::attributes)
        assertThat(updatedSchool.resourceAttributes)
            .returns(1L, ResourceAttributes::version)
    }

    @Test
    @DisplayName("null 可能なプロパティが全て null")
    fun testUpdateSchool_NullProperties() {
        // setup
        val createRequest = CreateSchoolApiParameterFixtures.create()
        schoolIntegrationHelper.createSchool(mockMvc, createRequest)

        val request = UpdateSchoolApiParameter()
        val content = objectMapper.writeValueAsString(request)

        // execution
        mockMvc.perform(post(SchoolIntegrationHelper.UPDATE_SCHOOL_PATH, createRequest.schoolCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(MockMvcResultHandlers.print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.school_code", Matchers.equalTo(createRequest.schoolCode)))
            .andExpect(jsonPath("$.name", Matchers.equalTo(createRequest.name)))
            .andExpect(jsonPath("$.memo", Matchers.equalTo(createRequest.memo)))
            .andExpect(jsonPath("$.attributes.hoge", Matchers.equalTo("hage")))
            .andExpect(jsonPath("$.version", Matchers.equalTo(1)))

        // 永続化していること
        val updatedSchool = jdbcSchoolRepo.getSchool(School.SchoolCode(createRequest.schoolCode!!),
            User.UserCode(dummyUser!!.userCode))
        assertThat(updatedSchool.schoolDetail)
            .returns(createRequest.name, School.SchoolDetail::name)
            .returns(createRequest.memo, School.SchoolDetail::memo)
            .returns(Attributes(createRequest.attributes!!), School.SchoolDetail::attributes)
        assertThat(updatedSchool.resourceAttributes)
            .returns(1L, ResourceAttributes::version)
    }

    @Test
    fun testUpdateSchool_NotFoundSchool_404() {
        // setup
        val request = UpdateSchoolApiParameter()
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(SchoolIntegrationHelper.UPDATE_SCHOOL_PATH, "absent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        val exception = actual.resolvedException as HttpNotFoundException
        assertThat(exception.message).isEqualTo("School(absent) は存在しません")
    }

    @Test
    fun testUpdateSchool_InvalidVersion_409() {
        // setup
        val createRequest = CreateSchoolApiParameterFixtures.create()
        schoolIntegrationHelper.createSchool(mockMvc, createRequest)

        val request = UpdateSchoolApiParameter()
        val content = objectMapper.writeValueAsString(request)

        // execution
        val path = "${SchoolIntegrationHelper.UPDATE_SCHOOL_PATH}?version=1" // invalid version
        val actual = mockMvc.perform(post(path, createRequest.schoolCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.CONFLICT.value())
        val exception = actual.resolvedException as HttpConflictException
        assertThat(exception.message).isEqualTo("指定した version が不正です")
    }

    @Test
    fun testUpdateSchool_InvalidJsonParameter_400() {
        // setup
        val createRequest = CreateSchoolApiParameterFixtures.create()
        schoolIntegrationHelper.createSchool(mockMvc, createRequest)

        val request = UpdateSchoolApiParameter().copy(attributes = """{hoge:hage}""")
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(SchoolIntegrationHelper.UPDATE_SCHOOL_PATH, createRequest.schoolCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as MethodArgumentNotValidException
        assertThat(exception.bindingResult.allErrors).hasSize(1)
        assertThat(exception.bindingResult.fieldErrors[0].field).isEqualTo("attributes")
        assertThat(exception.bindingResult.fieldErrors[0].defaultMessage).isEqualTo("must match json string format")
    }

    @Test
    fun testUpdateSchool_InvalidPathParameter_400() {
        // setup
        val request = UpdateSchoolApiParameter()
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(SchoolIntegrationHelper.UPDATE_SCHOOL_PATH, "_SCHOOL_001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as ConstraintViolationException
        assertThat(exception.message)
            .isEqualTo("updateSchool.schoolCode: must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }
}
