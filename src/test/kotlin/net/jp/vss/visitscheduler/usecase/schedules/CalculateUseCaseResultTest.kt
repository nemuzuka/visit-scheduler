package net.jp.vss.visitscheduler.usecase.schedules

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

/**
 * CalculateUseCaseResult のテスト.
 */
class CalculateUseCaseResultTest {

    companion object {
        val objectMapper = ObjectMapper()
    }

    @Test
    fun testJsonString() {
        // setup
        val sut = CalculateUseCaseResultFixtures.create()

        // execution
        val actual = objectMapper.writeValueAsString(sut)

        // verify
        val expected = """
            |{
            |   "visit_schedules":[
            |       {
            |           "visit_day": ${sut.visitSchedules[0].day},
            |           "school_code": ${sut.visitSchedules[0].schoolCode}
            |       }
            |   ]
            |}
        """.trimMargin()
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT)
    }
}
