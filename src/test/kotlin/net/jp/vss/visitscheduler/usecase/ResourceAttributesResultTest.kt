package net.jp.vss.visitscheduler.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import net.jp.vss.visitscheduler.domain.ResourceAttributesFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * ResourceAttributesResult のテスト.
 */
class ResourceAttributesResultTest {

    companion object {
        val objectMapper = ObjectMapper()
    }

    @Test
    fun testJsonString() {
        // setup
        val sut = ResourceAttributesResultFixtures.create()

        // execution
        val actual = objectMapper.writeValueAsString(sut)

        // verify
        val expected = """
            |{
                |"create_user_code":"${sut.createUserCode}",
                |"create_at":${sut.createAt},
                |"last_update_user_code":"${sut.lastUpdateUserCode}",
                |"last_update_at":${sut.lastUpdateAt},
                |"version":${sut.version}
            |}
        """.trimMargin().replace("\n", "") // もうちょっとかっこよく書きたい
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testOf() {
        // setup
        val resourceAttributes = ResourceAttributesFixtures.create()

        // execution
        val actual = ResourceAttributesResult.of(resourceAttributes)

        // verify
        val expected = ResourceAttributesResult(createUserCode = resourceAttributes.createUserCode,
            createAt = resourceAttributes.createAt,
            lastUpdateUserCode = resourceAttributes.lastUpdateUserCode,
            lastUpdateAt = resourceAttributes.lastUpdateAt,
            version = resourceAttributes.version)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testEmpty() {
        // execution
        val actual = ResourceAttributesResult.empty()

        // verify
        val expected = ResourceAttributesResult(createUserCode = "",
            createAt = 0L,
            lastUpdateUserCode = "",
            lastUpdateAt = 0L,
            version = 0L)
        assertThat(actual).isEqualTo(expected)
    }
}
