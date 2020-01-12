package net.jp.vss.visitscheduler.domain

import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * ResourceAttributes のテスト.
 */
class ResourceAttributesTest {

    companion object {
        const val NOW = 1546268400000L
    }

    @BeforeEach
    fun setUp() {
        DatetimeUtils.setDummyDatetimeResource(NOW)
    }

    @AfterEach
    fun tearDown() {
        DatetimeUtils.clearDummyDatetimeResource()
    }

    @Test
    fun testBuildForCreate() {
        // setup
        val createUserCode = "CUSTOMER_USER_0001"

        // execution
        val actual = ResourceAttributes.buildForCreate("CUSTOMER_USER_0001")

        // verify
        val expected = ResourceAttributes(
            createUserCode = createUserCode,
            createAt = NOW,
            lastUpdateUserCode = createUserCode,
            lastUpdateAt = NOW,
            version = 0L)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testBuildForUpdate() {
        // setup
        val updateUserCode = "CUSTOMER_USER_0002"
        val sut = ResourceAttributesFixtures.create()

        // execution
        val actual = sut.buildForUpdate(updateUserCode)

        // verify
        val expected = sut.copy(lastUpdateUserCode = updateUserCode,
                lastUpdateAt = DatetimeUtils.now())
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testValidateVersion() {
        // setup
        val sut = ResourceAttributesFixtures.create()
        val version = sut.version

        // execution
        sut.validateVersion(version) // Exception を throw しないこと
    }

    @Test
    fun testValidateVersion_NoValidate() {
        // setup
        val sut = ResourceAttributesFixtures.create()

        // execution
        sut.validateVersion(null) // Exception を throw しないこと
    }

    @Test
    fun testValidateVersion_InvalidVersion_UVE() {
        // setup
        val sut = ResourceAttributesFixtures.create()
        val version = sut.version + 1

        // execution
        val actual = catchThrowable { sut.validateVersion(version) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(UnmatchVersionException::class.java) { e ->
            assertThat(e.message).isEqualTo("指定した version が不正です")
        }
    }
}
