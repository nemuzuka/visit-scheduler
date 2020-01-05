package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link UpdateSchoolUseCase}
 *
 * @property schoolRepo School のリポジトリ
 */
@Service
@Transactional
class UpdateSchoolUseCaseImpl(private val schoolRepo: SchoolRepository) : UpdateSchoolUseCase {
    override fun updateSchool(parameter: UpdateSchoolUseCaseParameter): SchoolUseCaseResult {
        val school = schoolRepo.lockSchool(School.SchoolCode(parameter.schoolCode),
            User.UserCode(parameter.updateUserCode))
        school.validateVersion(parameter.version)

        val updateSchool = parameter.buildUpdateSchool(school)
        return SchoolUseCaseResult.of(schoolRepo.updateSchool(updateSchool))
    }
}
