package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.schools.SchoolFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * UpdateSchoolUseCaseParameter のテスト.
 */
class UpdateSchoolUseCaseParameterTest {

    companion object {
        const val NOW = 1545368400001L
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
    fun buildUpdateSchoolTest() {
        // setup
        val school = SchoolFixtures.create()
        val sut = UpdateSchoolUseCaseParameterFixture.create()

        // execution
        val actual = sut.buildUpdateSchool(school)

        // verify
        val baseSchoolDetail = school.schoolDetail
        val schoolDetail = baseSchoolDetail.copy(
            name = sut.name!!,
            memo = sut.memo!!,
            attributes = Attributes(sut.attributes!!)
        )
        val expected = school.copy(
            schoolDetail = schoolDetail,
            resourceAttributes = school.resourceAttributes.buildForUpdate(sut.updateUserCode))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun buildUpdateSchoolTest_NullValue() {
        // setup
        val school = SchoolFixtures.create()
        val sut = UpdateSchoolUseCaseParameterFixture.createNullValue()

        // execution
        val actual = sut.buildUpdateSchool(school)

        // verify
        val baseSchoolDetail = school.schoolDetail
        val schoolDetail = baseSchoolDetail.copy(
            name = baseSchoolDetail.name,
            memo = baseSchoolDetail.memo,
            attributes = baseSchoolDetail.attributes
        )
        val expected = school.copy(
            schoolDetail = schoolDetail,
            resourceAttributes = school.resourceAttributes.buildForUpdate(sut.updateUserCode))
        assertThat(actual).isEqualTo(expected)
    }
}
