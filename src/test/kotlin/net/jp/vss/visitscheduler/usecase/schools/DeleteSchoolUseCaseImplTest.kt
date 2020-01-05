package net.jp.vss.visitscheduler.usecase.schools

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.visitscheduler.domain.schools.SchoolFixtures
import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * DeleteSchoolUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class DeleteSchoolUseCaseImplTest {
    @Mock
    private lateinit var schoolRepo: SchoolRepository

    @InjectMocks
    private lateinit var sut: DeleteSchoolUseCaseImpl

    @Test
    fun testDeleteSchool() {
        // setup
        val school = SchoolFixtures.create()
        whenever(schoolRepo.lockSchool(any(), any())).thenReturn(school)
        doNothing().whenever(schoolRepo).deleteSchool(any())

        // execution
        sut.deleteSchool(school.schoolCode.value, school.userCode.value, school.resourceAttributes.version)

        // verify
        verify(schoolRepo).lockSchool(school.schoolCode, school.userCode)
        verify(schoolRepo).deleteSchool(school)
    }
}
