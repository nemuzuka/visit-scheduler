package net.jp.vss.visitscheduler.controller.schedules

import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.IntegrationHelper
import net.jp.vss.visitscheduler.controller.schools.CreateSchoolApiParameterFixtures
import net.jp.vss.visitscheduler.controller.schools.SchoolIntegrationHelper
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * ListSchoolScheduleApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class ListSchoolScheduleApiIntegrationTest {

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
    private lateinit var schoolScheduleIntegrationHelper: SchoolScheduleIntegrationHelper

    @Autowired
    private lateinit var integrationHelper: IntegrationHelper

    @Autowired
    private lateinit var schoolIntegrationHelper: SchoolIntegrationHelper

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
    fun testListSchoolSchedule() {
        // setup
        schoolIntegrationHelper.createSchool(mockMvc,
            CreateSchoolApiParameterFixtures.create().copy(schoolCode = "SCHOOL_0001"))
        val saveParameter = SaveSchoolScheduleApiParameterFixtures.create()
        schoolScheduleIntegrationHelper.saveSchoolSchedule(mockMvc, saveParameter)

        // execution
        mockMvc.perform(MockMvcRequestBuilders.get(SchoolScheduleIntegrationHelper.LIST_SCHOOL_SCHEDULE_PATH,
            "SCHOOL_0001", saveParameter.targetYearAndMonth!!))
            .andDo(MockMvcResultHandlers.print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.school_schedules", hasSize<Int>(2)))
            .andExpect(jsonPath("$.school_schedules[0].target_day").value(`is`(1)))
            .andExpect(jsonPath("$.school_schedules[0].memo").value(`is`("メモ1")))
            .andExpect(jsonPath("$.school_schedules[0].priority").value(`is`("DONT_COME")))
            .andExpect(jsonPath("$.school_schedules[1].target_day").value(`is`(3)))
            .andExpect(jsonPath("$.school_schedules[1].memo").value(`is`("メモ2")))
            .andExpect(jsonPath("$.school_schedules[1].priority").value(`is`("POSSIBLE")))
            .andExpect(jsonPath("$.last_month_visit_date").value(`is`(saveParameter.lastMonthVisitDate!!)))
    }

    @Test
    fun testListSchoolSchedule_InvalidParameter_400() {
        // execution
        mockMvc.perform(MockMvcRequestBuilders.get(
            SchoolScheduleIntegrationHelper.LIST_SCHOOL_SCHEDULE_PATH, "xxx", "a"))
            .andDo(MockMvcResultHandlers.print())
            // verify
            .andExpect(status().isBadRequest)
    }
}
