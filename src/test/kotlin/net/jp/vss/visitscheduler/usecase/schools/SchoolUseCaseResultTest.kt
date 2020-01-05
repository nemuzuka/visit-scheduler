package net.jp.vss.visitscheduler.usecase.schools

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * SchoolUseCaseResult のテスト.
 */
class SchoolUseCaseResultTest {

    companion object {
        val objectMapper = ObjectMapper()
    }

    @Test
    fun testJsonString() {
        // setup
        val sut = SchoolUseCaseResultFixtures.create()

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
                |"school_code":"${sut.schoolCode}",
                |"name":"${sut.name}",
                |"memo":"${sut.memo}",
                |"attributes":{
                    |"hoge":"hige",
                    |"fuga":{
                        |"neko":"nyan"
                    |}
                |}
            |}
        """.trimMargin().replace("\n", "")
        assertThat(actual).isEqualTo(expected)
    }

    /**
     * null を含むプロパティの JSON 化.
     */
    @Test
    fun testJsonString_NullValue() {
        // setup
        val sut = SchoolUseCaseResultFixtures.create()
            .copy(memo = null, attributes = null)

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
                |"school_code":"${sut.schoolCode}",
                |"name":"${sut.name}",
                |"memo":null,
                |"attributes":null
            |}
        """.trimMargin().replace("\n", "")
        assertThat(actual).isEqualTo(expected)
    }
}
