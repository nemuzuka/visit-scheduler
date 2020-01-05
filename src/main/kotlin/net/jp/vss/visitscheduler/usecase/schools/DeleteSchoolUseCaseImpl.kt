package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link DeleteSchoolUseCase}
 *
 * @property schoolRepo School のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class DeleteSchoolUseCaseImpl(
    private val schoolRepo: SchoolRepository
) : DeleteSchoolUseCase {
    override fun deleteSchool(schoolCode: String, userCode: String, version: Long?) {
        val school = schoolRepo.lockSchool(School.SchoolCode(schoolCode),
            User.UserCode(userCode))
        school.validateVersion(version)

        schoolRepo.deleteSchool(school)
    }
}
