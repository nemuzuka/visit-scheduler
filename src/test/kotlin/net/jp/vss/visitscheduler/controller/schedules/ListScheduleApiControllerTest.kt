package net.jp.vss.visitscheduler.controller.schedules

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schedules.ScheduleFixtures
import net.jp.vss.visitscheduler.usecase.schedules.ListScheduleUseCase
import net.jp.vss.visitscheduler.usecase.schedules.ScheduleUseCaseResult
import net.jp.vss.visitscheduler.usecase.users.GetUserUseCase
import net.jp.vss.visitscheduler.usecase.users.UserUseCaseResultFixtures
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers
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
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * ListScheduleApiController のテスト.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class ListScheduleApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var getUserUseCase: GetUserUseCase

    @MockBean
    private lateinit var listScheduleUseCase: ListScheduleUseCase

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
    fun testListSchedule() {
        // setup
        val authorizedClientRegistrationId = "google"
        val principalName = "abcd-000A-0001"
        whenever(oAuth2AuthenticationToken.authorizedClientRegistrationId).thenReturn(authorizedClientRegistrationId)
        whenever(principal.name).thenReturn(principalName)
        whenever(oAuth2AuthenticationToken.principal).thenReturn(principal)
        whenever(oAuth2AuthenticationToken.isAuthenticated).thenReturn(true)

        val user = UserUseCaseResultFixtures.create()
        whenever(getUserUseCase.getUser(anyString(), anyString())).thenReturn(user)

        val schedule1 = ScheduleFixtures.create().copy(scheduleCode = Schedule.ScheduleCode("SCHEDUE-001"))
        val schedule2 = ScheduleFixtures.create().copy(scheduleCode = Schedule.ScheduleCode("SCHEDUE-002"))
        whenever(listScheduleUseCase.allSchedules(anyString()))
            .thenReturn(listOf(ScheduleUseCaseResult.of(schedule1), ScheduleUseCaseResult.of(schedule2)))

        // execution
        mockMvc.perform(get("/api/schedules"))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.elements", Matchers.hasSize<Int>(2)))
            .andExpect(jsonPath("$.elements[0].schedule_code").value(`is`(schedule1.scheduleCode.value)))
            .andExpect(jsonPath("$.elements[1].schedule_code").value(`is`(schedule2.scheduleCode.value)))

        verify(listScheduleUseCase).allSchedules(user.userCode)
    }
}
