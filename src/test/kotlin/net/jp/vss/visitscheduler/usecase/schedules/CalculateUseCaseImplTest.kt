package net.jp.vss.visitscheduler.usecase.schedules

import net.jp.vss.visitscheduler.domain.schedules.calculate.VisitRules
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * CalculateUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class CalculateUseCaseImplTest {

    @InjectMocks
    private lateinit var sut: CalculateUseCaseImpl

    @Test
    @DisplayName("calculateSchedule のテスト.規定回数以内に振り分けが終わった")
    fun testCalculateSchedule() {
        // setup
        val parameter = CalculateUseCaseParameterFixtures.create()

        // execution
        val actual = sut.calculateSchedule(parameter)

        // verify
        val visitDayAndSchool1 = CalculateUseCaseResult.VisitDayAndSchool(4, "school_0001")
        val visitDayAndSchool2 = CalculateUseCaseResult.VisitDayAndSchool(19, "school_0001")
        val expected = CalculateUseCaseResult(listOf(visitDayAndSchool1, visitDayAndSchool2))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @DisplayName("calculateSchedule のテスト.規定回数を超えても終わらなかった")
    fun testCalculateSchedule_OverTryCount() {
        // setup
        val parameter = CalculateUseCaseParameterFixtures.create()
            .copy(visitRules = VisitRules(30, 30), tryCount = 1) // ありえない設定

        // execution
        val actual = catchThrowable { sut.calculateSchedule(parameter) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(CalculateUseCase.CalculateException::class.java) { e ->
            assertThat(e.message).isEqualTo("スケジュールを計算できませんでした。") }
    }
}
