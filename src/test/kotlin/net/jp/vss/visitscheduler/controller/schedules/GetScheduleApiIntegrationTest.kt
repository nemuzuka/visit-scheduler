package net.jp.vss.visitscheduler.controller.schedules

import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.IntegrationHelper
import net.jp.vss.visitscheduler.controller.exceptions.HttpNotFoundException
import net.jp.vss.visitscheduler.controller.schools.CreateSchoolApiParameterFixtures
import net.jp.vss.visitscheduler.controller.schools.SchoolIntegrationHelper
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.assertj.core.api.Assertions
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * GetScheduleDetailApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class GetScheduleApiIntegrationTest {

    companion object {
        const val AUTORIZED_CLIENT_REGISTRATION_ID = "google"
        const val PRINCIPAL_NAME = "abcd-000A-0001"
        const val USER_CODE = "USER-0001"
    }

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var schoolIntegrationHelper: SchoolIntegrationHelper

    @Autowired
    private lateinit var scheduleIntegrationHelper: ScheduleIntegrationHelper

    @Autowired
    private lateinit var integrationHelper: IntegrationHelper

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
    fun testGetSchedule() {
        // setup
        val createSchoolRequest1 = CreateSchoolApiParameterFixtures.create().copy(schoolCode = "SCHOOL_002")
        schoolIntegrationHelper.createSchool(mockMvc, createSchoolRequest1)
        val createSchoolRequest2 = CreateSchoolApiParameterFixtures.create().copy(schoolCode = "SCHOOL_001")
        schoolIntegrationHelper.createSchool(mockMvc, createSchoolRequest2)

        val createScheduleRequest = CreateScheduleApiParameterFixtures.create()
        scheduleIntegrationHelper.createSchedule(mockMvc, createScheduleRequest)

        // execution
        mockMvc.perform(MockMvcRequestBuilders.get(ScheduleIntegrationHelper.GET_SCHEDULE_PATH,
                createScheduleRequest.scheduleCode))
            .andDo(MockMvcResultHandlers.print())
            // verify
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.schedule_code", equalTo(createScheduleRequest.scheduleCode)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.school_with_schedules", hasSize<Int>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.school_with_schedules[0].school.school_code",
                equalTo(createSchoolRequest2.schoolCode))) // CreateSchedule 時の設定で、SCHOOL_001, SCHOOL_002 の順番
            .andExpect(MockMvcResultMatchers.jsonPath("$.school_with_schedules[1].school.school_code",
                equalTo(createSchoolRequest1.schoolCode)))
    }

    @Test
    fun testGetSchedule_NotFoundSchedule_404() {
        // execution
        val actual = mockMvc.perform(MockMvcRequestBuilders.get(ScheduleIntegrationHelper.GET_SCHEDULE_PATH,
                "absent_code"))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        // verify
        Assertions.assertThat(actual.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        val exception = actual.resolvedException as HttpNotFoundException
        Assertions.assertThat(exception.message).isEqualTo("Schedule(absent_code) は存在しません")
    }
}
