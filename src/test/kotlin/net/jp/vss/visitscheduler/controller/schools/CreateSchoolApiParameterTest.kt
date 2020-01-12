package net.jp.vss.visitscheduler.controller.schools

import com.google.common.base.Strings.repeat
import javax.validation.Validation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * CreateSchoolApiParameter のテスト.
 */
class CreateSchoolApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(CreateSchoolApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = CreateSchoolApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "schoolCode",
            "name")
        assertThat(actual).hasSize(2)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = CreateSchoolApiParameterFixtures.create()

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
        val sut = CreateSchoolApiParameter(
            schoolCode = repeat("x", 129),
            name = repeat("x", 257),
            memo = repeat("x", 1025),
            attributes = """{"invalid":json_value}"""
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(5)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "schoolCode size must be between 0 and 128",
            "schoolCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"",
            "name size must be between 0 and 256",
            "memo size must be between 0 and 1024",
            "attributes must match json string format")
    }

    /**
     * 異常パターンのテスト.
     */
    @Test
    fun testViolatePatternConstrains_Format() {
        // setup
        val sut = CreateSchoolApiParameter(
            schoolCode = "スクールコード", // 正規表現外
            name = "dummy") // dummy data

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(1)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "schoolCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }
}
