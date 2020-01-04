package net.jp.vss.visitscheduler.controller.users

import com.google.common.base.Strings.repeat
import javax.validation.Validation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * CreateUserApiParameter のテスト.
 */
class CreateUserApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(CreateUserApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = CreateUserApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "userCode",
            "userName")
        assertThat(actual).hasSize(2)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = CreateUserApiParameterFixtures.create()

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
        val sut = CreateUserApiParameter(
            userCode = repeat("x", 129),
            userName = repeat("x", 129)
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(3)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "userCode size must be between 0 and 128",
            "userCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"",
            "userName size must be between 0 and 128")
    }

    /**
     * 異常パターンのテスト.
     */
    @Test
    fun testViolatePatternConstrains_Format() {
        // setup
        val sut = CreateUserApiParameter(
            userCode = "ユーザコード", // 正規表現外
            userName = "dummy") // dummy data

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(1)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "userCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }
}
