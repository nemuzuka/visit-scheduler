package net.jp.vss.visitscheduler.usecase.schedules

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * ScheduleUseCaseResult のテスト.
 */
class ScheduleUseCaseResultTest {

    companion object {
        val objectMapper = ObjectMapper()
    }
    @Test
    fun testJsonString() {
        // setup
        val sut = ScheduleUseCaseResultFixtures.create()

        // execution
        val actual = objectMapper.writeValueAsString(sut)

        // verify
        val expected = """
            |{
                |"create_user_code":"${sut.resourceAttributesResult.createUserCode}",
                |"create_at":${sut.resourceAttributesResult.createAt},
                |"last_update_user_code":"${sut.resourceAttributesResult.lastUpdateUserCode}",
                |"last_update_at":${sut.resourceAttributesResult.lastUpdateAt},
                |"version":${sut.resourceAttributesResult.version},
                |"schedule_code":"${sut.scheduleCode}",
                |"target_year_and_month":"${sut.targetYearAndMonth}",
                |"attributes":{
                    |"hoge":"hige",
                    |"fuga":{
                        |"neko":"nyan"
                    |}
                |}
            |}
        """.trimMargin().replace("\n", "")
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    /**
     * null を含むプロパティの JSON 化.
     */
    @Test
    fun testJsonString_NullValue() {
        // setup
        val sut = ScheduleUseCaseResultFixtures.create().copy(attributes = null)

        // execution
        val actual = objectMapper.writeValueAsString(sut)

        // verify
        val expected = """
            |{
                |"create_user_code":"${sut.resourceAttributesResult.createUserCode}",
                |"create_at":${sut.resourceAttributesResult.createAt},
                |"last_update_user_code":"${sut.resourceAttributesResult.lastUpdateUserCode}",
                |"last_update_at":${sut.resourceAttributesResult.lastUpdateAt},
                |"version":${sut.resourceAttributesResult.version},
                |"schedule_code":"${sut.scheduleCode}",
                |"target_year_and_month":"${sut.targetYearAndMonth}",
                |"attributes":null
            |}
        """.trimMargin().replace("\n", "")
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
