package net.jp.vss.visitscheduler.domain.schools

import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.exceptions.UnmatchVersionException
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * School のテスト.
 */
class SchoolTest {

    companion object {
        const val NOW = 1546268400001L
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
        val schoolCodeValue = "SCHOOL-0001"
        val name = "学校001"
        val memo = "メモ01"
        val userCode = User.UserCode("CUSTOMER-0004")
        val attributeJsonString = """{"hoge":"hige"}"""
        val createUserCode = "CUSTOMER-0003"

        // execution
        val actual = School.buildForCreate(schoolCodeValue, userCode, name, memo, attributeJsonString, createUserCode)

        // verify
        val schoolId = actual.schoolId // 処理内で生成する為
        val schoolCode = School.SchoolCode(schoolCodeValue)
        val schoolDetail = School.SchoolDetail(name = name,
                memo = memo,
                attributes = Attributes(attributeJsonString))
        val resourceAttributes = ResourceAttributes.buildForCreate(createUserCode)
        val expected = School(schoolId = schoolId, schoolCode = schoolCode, userCode = userCode,
            schoolDetail = schoolDetail, resourceAttributes = resourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testValidateVersion() {
        // setup
        val sut = SchoolFixtures.create()
        val version = sut.resourceAttributes.version

        // execution
        sut.validateVersion(version) // Exception を throw しないこと
    }

    @Test
    fun testValidateVersion_NoValidate() {
        // setup
        val sut = SchoolFixtures.create()

        // execution
        sut.validateVersion(null) // Exception を throw しないこと
    }

    @Test
    fun testValidateVersion_InvalidVersion_UVE() {
        // setup
        val sut = SchoolFixtures.create()
        val version = sut.resourceAttributes.version + 1

        // execution
        val actual = Assertions.catchThrowable { sut.validateVersion(version) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(UnmatchVersionException::class.java) { e ->
            assertThat(e.message).isEqualTo("指定した version が不正です")
        }
    }
}
