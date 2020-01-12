package net.jp.vss.visitscheduler.usecase.schools

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.DatetimeUtils
import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
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
 * CreateSchoolUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class CreateSchoolUseCaseImplTest {

    @Mock
    private lateinit var schoolRepo: SchoolRepository

    @InjectMocks
    private lateinit var sut: CreateSchoolUseCaseImpl

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
    fun testCreateSchool() {
        // setup
        val createdSchool = SchoolFixtures.create()
        whenever(schoolRepo.createSchool(any())).thenReturn(createdSchool)

        val input = CreateSchoolUseCaseParameterFixtures.create()

        // execution
        val actual = sut.createSchool(input)

        // verify
        assertThat(actual).isEqualTo(SchoolUseCaseResult.of(createdSchool))

        // org.mockito.ArgumentCaptor を使用する代わり
        argumentCaptor<School>().apply {
            verify(schoolRepo).createSchool(capture())

            val capturedSchool = firstValue // getValue と同意
            val expectedSchool = School(schoolId = capturedSchool.schoolId,
                schoolCode = School.SchoolCode(input.schoolCode),
                userCode = User.UserCode(input.createUserCode),
                schoolDetail = School.SchoolDetail(name = input.name,
                    memo = input.memo,
                    attributes = Attributes(input.attributes!!)),
                resourceAttributes = ResourceAttributes(createUserCode = input.createUserCode,
                    createAt = NOW,
                    lastUpdateUserCode = input.createUserCode,
                    lastUpdateAt = NOW,
                    version = 0L))
            assertThat(capturedSchool).isEqualTo(expectedSchool)
        }
    }
}
