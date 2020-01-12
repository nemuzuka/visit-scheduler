package net.jp.vss.visitscheduler.controller.schools

import com.google.common.base.Strings
import javax.validation.Validation
import net.jp.vss.visitscheduler.usecase.schools.UpdateSchoolUseCaseParameter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * UpdateSchoolApiParameter の Test.
 */
class UpdateSchoolApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(UpdateSchoolApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = UpdateSchoolApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).isEmpty()
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = UpdateSchoolApiParameterFixtures.create()

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
        val sut = UpdateSchoolApiParameter(
            name = Strings.repeat("x", 257),
            memo = Strings.repeat("x", 1025),
            attributes = """{"invalid":json_value}""")

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(3)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "name size must be between 0 and 256",
            "memo size must be between 0 and 1024",
            "attributes must match json string format")
    }

    @Test
    fun testToParameter() {
        // setup
        val sut = UpdateSchoolApiParameterFixtures.create()
        val schoolCode = "SCHOOL_00001"
        val version = 12L
        val updateUserCode = "UPDATE_USER_0004"

        // execution
        val actual = sut.toParameter(schoolCode, version, updateUserCode)

        // verify
        val expected = UpdateSchoolUseCaseParameter(
            schoolCode = schoolCode,
            name = sut.name,
            memo = sut.memo,
            attributes = sut.attributes,
            version = version,
            updateUserCode = updateUserCode)
        assertThat(actual).isEqualTo(expected)
    }
}
