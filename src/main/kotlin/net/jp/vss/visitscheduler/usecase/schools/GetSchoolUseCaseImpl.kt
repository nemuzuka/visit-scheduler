package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link GetSchoolUseCase}
 *
 * @property schoolRepo School のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class GetSchoolUseCaseImpl(
    private val schoolRepo: SchoolRepository
) : GetSchoolUseCase {
    override fun getSchool(schoolCode: String, userCode: String): SchoolUseCaseResult =
        SchoolUseCaseResult.of(schoolRepo.getSchool(School.SchoolCode(schoolCode), User.UserCode(userCode)))
}
