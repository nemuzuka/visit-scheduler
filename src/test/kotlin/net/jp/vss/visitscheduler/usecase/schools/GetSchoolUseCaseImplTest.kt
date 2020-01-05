package net.jp.vss.visitscheduler.usecase.schools

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schools.SchoolFixtures
import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * GetSchoolUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class GetSchoolUseCaseImplTest {
    @Mock
    private lateinit var schoolRepo: SchoolRepository

    @InjectMocks
    private lateinit var sut: GetSchoolUseCaseImpl

    @Test
    fun testGetSchool() {
        // setup
        val school = SchoolFixtures.create()
        whenever(schoolRepo.getSchool(any(), any())).thenReturn(school)

        // execution
        val actual = sut.getSchool(school.schoolCode.value, school.userCode.value)

        // verify
        assertThat(actual).isEqualTo(SchoolUseCaseResult.of(school))

        verify(schoolRepo).getSchool(school.schoolCode, school.userCode)
    }
}
