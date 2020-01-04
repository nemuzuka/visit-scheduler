package net.jp.vss.visitscheduler.controller.users

import com.google.common.base.Strings
import javax.validation.Validation
import net.jp.vss.visitscheduler.usecase.users.UpdateUserUseCaseParameter
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * UpdateUserApiParameter の Test.
 */
class UpdateUserApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(UpdateUserApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = UpdateUserApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        Assertions.assertThat(errors).containsExactlyInAnyOrder(
            "userName")
        Assertions.assertThat(actual).hasSize(1)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = UpdateUserApiParameterFixtures.create()

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(0)
    }

    /**
     * 異常パターンのテスト.
     */
    @Test
    fun testViolatePatternConstrains() {
        // setup
        val sut = UpdateUserApiParameter(
            userName = Strings.repeat("x", 129))

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(1)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "userName size must be between 0 and 128")
    }

    @Test
    fun testToParameter() {
        // setup
        val sut = UpdateUserApiParameterFixtures.create()
        val userCode = "USER_00001"

        // execution
        val actual = sut.toParameter(userCode)

        // verify
        val expected = UpdateUserUseCaseParameter(
            userCode = userCode,
            userName = sut.userName!!)
        assertThat(actual).isEqualTo(expected)
    }
}
