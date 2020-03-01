package net.jp.vss.visitscheduler.controller.schedules

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.whenever
import java.time.LocalDate
import net.jp.vss.visitscheduler.IntegrationHelper
import net.jp.vss.visitscheduler.domain.schedules.PrivateSchedule
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.infrastructure.schedules.JdbcPrivateScheduleRepository
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResult
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.WebApplicationContext

/**
 * SavePrivateScheduleApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class SavePrivateScheduleApiIntegrationTest {

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
    private lateinit var jdbcPrivateScheduleRepo: JdbcPrivateScheduleRepository

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
    fun testSavePrivateSchedule() {
        // setup
        val request = SavePrivateScheduleApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        mockMvc.perform(MockMvcRequestBuilders.post(PrivateScheduleIntegrationHelper.SAVE_PRIVATE_SCHEDULE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(MockMvcResultHandlers.print())
            // verify
            .andExpect(MockMvcResultMatchers.status().isOk)

        // 永続化していること
        val createdSchool = jdbcPrivateScheduleRepo.getPrivateSchedules(User.UserCode(dummyUser!!.userCode),
            Schedule.TargetYearAndMonth(request.targetYearAndMonth!!))
        assertThat(createdSchool).hasSize(2)
        assertThat(createdSchool[0])
            .returns(Schedule.ScheduleDate(LocalDate.parse("2019-12-01")), PrivateSchedule::targetDate)
            .returns(PrivateSchedule.PrivateScheduleDetail("メモ1"), PrivateSchedule::privateScheduleDetail)
        assertThat(createdSchool[1])
            .returns(Schedule.ScheduleDate(LocalDate.parse("2019-12-03")), PrivateSchedule::targetDate)
            .returns(PrivateSchedule.PrivateScheduleDetail("メモ2"), PrivateSchedule::privateScheduleDetail)
    }

    @Test
    fun testSavePrivateSchedule_InvalidParameter_400() {
        // setup
        val request = SavePrivateScheduleApiParameterFixtures.create().copy(targetYearAndMonth = null)
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(MockMvcRequestBuilders.post(
            PrivateScheduleIntegrationHelper.SAVE_PRIVATE_SCHEDULE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as MethodArgumentNotValidException
        assertThat(exception.bindingResult.allErrors).hasSize(1)
        assertThat(exception.bindingResult.fieldErrors[0].field).isEqualTo("targetYearAndMonth")
        assertThat(exception.bindingResult.fieldErrors[0].defaultMessage).isEqualTo("must not be null")
    }
}
