package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link CreateSchoolUseCase}
 *
 * @property schoolRepo School のリポジトリ
 */
@Service
@Transactional
class CreateSchoolUseCaseImpl(
    private val schoolRepo: SchoolRepository
) : CreateSchoolUseCase {

    override fun createSchool(parameter: CreateSchoolUseCaseParameter): SchoolUseCaseResult {
        val school = School.buildForCreate(schoolCodeValue = parameter.schoolCode,
            userCode = User.UserCode(parameter.createUserCode),
            name = parameter.name,
            memo = parameter.memo,
            attributeJsonString = parameter.attributes,
            createUserCode = parameter.createUserCode)
        return SchoolUseCaseResult.of(schoolRepo.createSchool(school))
    }
}
