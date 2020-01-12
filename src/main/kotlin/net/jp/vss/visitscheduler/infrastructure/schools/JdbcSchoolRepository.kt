package net.jp.vss.visitscheduler.infrastructure.schools

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.schools.SchoolRepository
import net.jp.vss.visitscheduler.domain.users.User
import org.seasar.doma.jdbc.SelectOptions
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする SchoolRepository の実装.
 */
@Repository
class JdbcSchoolRepository(private val schoolDao: SchoolDao) : SchoolRepository {

    companion object {
        private val log = LoggerFactory.getLogger(JdbcSchoolRepository::class.java)
    }

    override fun createSchool(school: School): School {
        try {
            schoolDao.create(toSchoolEntity(school))
            return getSchool(school.schoolCode, school.userCode)
        } catch (e: DuplicateKeyException) {
            log.info("Duplicate key: {}", e.message, e)
            val message = "School(${school.schoolCode.value}) は既に存在しています"
            throw DuplicateException(message)
        }
    }

    override fun getSchool(schoolCode: School.SchoolCode, userCode: User.UserCode): School =
        getSchool(schoolCode, userCode, false)

    private fun getSchool(schoolCode: School.SchoolCode, userCode: User.UserCode, isLock: Boolean): School =
        schoolDao.findBySchoolCodeAndUserCode(schoolCode.value, userCode.value,
            if (isLock) SelectOptions.get().forUpdate() else SelectOptions.get())?.toSchool()
            ?: throw NotFoundException("School(${schoolCode.value}) は存在しません")

    override fun lockSchool(schoolCode: School.SchoolCode, userCode: User.UserCode): School =
        getSchool(schoolCode, userCode, true)

    override fun updateSchool(school: School): School {
        try {
            schoolDao.update(toSchoolEntity(school))
        } catch (e: OptimisticLockingFailureException) {
            val message = "School(${school.schoolCode.value}) は存在しません"
            throw NotFoundException(message)
        }
        return getSchool(school.schoolCode, school.userCode)
    }

    override fun deleteSchool(school: School) {
        try {
            schoolDao.delete(toSchoolEntity(school))
        } catch (e: OptimisticLockingFailureException) {
            val message = "School(${school.schoolCode.value}) は存在しません"
            throw NotFoundException(message)
        }
    }

    override fun allSchools(userCode: User.UserCode): List<School> =
        schoolDao.findAll(userCode.value).map { v -> v.toSchool() }

    private fun toSchoolEntity(school: School): SchoolEntity {
        val schoolDetail = school.schoolDetail
        val resourceAttributes = school.resourceAttributes
        return SchoolEntity(
            schoolId = school.schoolId.value,
            schoolCode = school.schoolCode.value,
            userCode = school.userCode.value,
            name = schoolDetail.name,
            memo = schoolDetail.memo,
            attributes = schoolDetail.attributes?.value,
            createUserCode = resourceAttributes.createUserCode,
            createAt = resourceAttributes.createAt,
            lastUpdateUserCode = resourceAttributes.lastUpdateUserCode,
            lastUpdateAt = resourceAttributes.lastUpdateAt,
            versionNo = resourceAttributes.version)
    }
}
