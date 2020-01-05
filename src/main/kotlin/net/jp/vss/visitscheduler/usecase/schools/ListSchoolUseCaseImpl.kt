package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link ListSchoolUseCase}
 *
 * @property schoolRepo School のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class ListSchoolUseCaseImpl(
    private val schoolRepo: SchoolRepository
) : ListSchoolUseCase {
    override fun allSchools(userCode: String): List<SchoolUseCaseResult> =
        schoolRepo.allSchools(User.UserCode(userCode)).map { SchoolUseCaseResult.of(it) }.toList()
}
