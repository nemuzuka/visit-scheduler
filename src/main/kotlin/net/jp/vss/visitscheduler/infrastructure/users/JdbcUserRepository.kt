package net.jp.vss.visitscheduler.infrastructure.users

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.users.User
import net.jp.vss.visitscheduler.domain.users.UserRepository
import org.seasar.doma.jdbc.SelectOptions
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする UserRepository の実装.
 */
@Repository
class JdbcUserRepository(
    private val authenticatedPrincipalDao: AuthenticatedPrincipalDao,
    private val userDao: UserDao
) : UserRepository {

    companion object {
        private val log = LoggerFactory.getLogger(JdbcUserRepository::class.java)
    }

    override fun getUserOrNull(
        authorizedClientRegistrationId: User.AuthorizedClientRegistrationId,
        principal: User.Principal
    ): User? =
        userDao.findByAuthorizedClientRegistrationIdAndPrincipal(
            authorizedClientRegistrationId = authorizedClientRegistrationId.value,
            principal = principal.value)
        ?.toUser()

    fun getUserOrNull(userCode: User.UserCode): User? =
        userDao.findByUserCode(userCode.value, SelectOptions.get())?.toUser()

    override fun createUser(
        user: User,
        authenticatedPrincipalId: User.AuthenticatedPrincipalId,
        authorizedClientRegistrationId: User.AuthorizedClientRegistrationId,
        principal: User.Principal
    ): User {

        try {
            val authenticatedPrincipalEntity = AuthenticatedPrincipalEntity(
                authorizedClientRegistrationId = authorizedClientRegistrationId.value,
                authenticatedPrincipalId = authenticatedPrincipalId.value,
                principal = principal.value
            )
            authenticatedPrincipalDao.create(authenticatedPrincipalEntity)

            val userEntity = UserEntity(
                userId = user.userId.value,
                userCode = user.userCode.value,
                authenticatedPrincipalId = authenticatedPrincipalId.value,
                userName = user.userDetail.userName
            )
            userDao.create(userEntity)
            return user
        } catch (e: DuplicateKeyException) {
            log.info("Duplicate key: {}", e.message, e)
            val message = "User(${user.userCode.value}) は既に存在しています"
            throw DuplicateException(message)
        }
    }

    override fun updateUser(user: User): User {
        val updatedCount = userDao.updateUserName(userId = user.userId.value, userName = user.userDetail.userName)
        if (updatedCount != 1) {
            val message = "User(${user.userCode.value}) は存在しません"
            throw NotFoundException(message)
        }
        return user
    }

    override fun lockUser(userCode: User.UserCode): User =
        userDao.findByUserCode(userCode.value, SelectOptions.get().forUpdate())?.toUser()
        ?: throw NotFoundException("User(${userCode.value}) は存在しません")
}
