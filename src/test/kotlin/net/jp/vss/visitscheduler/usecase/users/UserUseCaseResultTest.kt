package net.jp.vss.visitscheduler.usecase.users

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * UserUseCaseResult のテスト.
 */
class UserUseCaseResultTest {

    companion object {
        val objectMapper = ObjectMapper()
    }

    @Test
    fun testJsonString() {
        // setup
        val sut = UserUseCaseResultFixtures.create()

        // execution
        val actual = objectMapper.writeValueAsString(sut)

        // verify
        val expected = """
            |{
                |"user_code":"${sut.userCode}",
                |"user_name":"${sut.userName}"
            |}
        """.trimMargin().replace("\n", "")
        assertThat(actual).isEqualTo(expected)
    }
}
