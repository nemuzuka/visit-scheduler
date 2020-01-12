package net.jp.vss.visitscheduler.usecase.schools

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schools.SchoolFixtures
import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * ListSchoolUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class ListSchoolUseCaseImplTest {
    @Mock
    private lateinit var schoolRepo: SchoolRepository

    @InjectMocks
    private lateinit var sut: ListSchoolUseCaseImpl

    @Test
    fun testAllSchools() {
        // setup
        val school = SchoolFixtures.create()
        whenever(schoolRepo.allSchools(any())).thenReturn(listOf(school))
        val userCode = "USER-0001"

        // execution
        val actual = sut.allSchools(userCode)

        // verify
        assertThat(actual).isEqualTo(listOf(SchoolUseCaseResult.of(school)))
        verify(schoolRepo).allSchools(User.UserCode(userCode))
    }
}
