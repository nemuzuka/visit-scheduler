package net.jp.vss.visitscheduler.usecase.schools

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.schools.SchoolFixtures
import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * UpdateSchoolUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class UpdateSchoolUseCaseImplTest {

    @Mock
    private lateinit var schoolRepo: SchoolRepository

    @InjectMocks
    private lateinit var sut: UpdateSchoolUseCaseImpl

    companion object {
        const val NOW = 1546268400002L
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
    fun testUpdateSchool() {
        // setup
        val school = SchoolFixtures.create()
        whenever(schoolRepo.lockSchool(any(), any())).thenReturn(school)
        val updatedSchool = SchoolFixtures.create("UPDATED_SCHOOL_0001", "USER_0001") // あえて別のインスタンスにする
        whenever(schoolRepo.updateSchool(any())).thenReturn(updatedSchool)

        val parameter = UpdateSchoolUseCaseParameterFixture.create()

        // execution
        val actual = sut.updateSchool(parameter)

        // verify
        assertThat(actual).isEqualTo(SchoolUseCaseResult.of(updatedSchool))

        verify(schoolRepo).lockSchool(School.SchoolCode(parameter.schoolCode), User.UserCode(parameter.updateUserCode))

        argumentCaptor<School>().apply {
            verify(schoolRepo).updateSchool(capture())
            val capturedSchool = firstValue
            val expectedSchool = parameter.buildUpdateSchool(school)
            assertThat(capturedSchool).isEqualTo(expectedSchool)
        }
    }
}
