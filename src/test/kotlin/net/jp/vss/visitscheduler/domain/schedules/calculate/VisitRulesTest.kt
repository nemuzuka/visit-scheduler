package net.jp.vss.visitscheduler.domain.schedules.calculate

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/** VisitRules のテスト. */
class VisitRulesTest {

    @Test
    fun testUpdateDays() {
        // setup
        val sut = VisitRules(7, 14)

        // exercise
        val actual = sut.updateDays(-2)

        // verify
        assertThat(actual).isEqualTo(VisitRules(7 - 2, 14 - 2))
    }
}
