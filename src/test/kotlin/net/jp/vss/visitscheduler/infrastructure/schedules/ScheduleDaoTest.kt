package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate
import java.util.UUID
import net.jp.vss.visitscheduler.JdbcRepositoryUnitTest
import net.jp.vss.visitscheduler.infrastructure.users.AuthenticatedPrincipalDao
import net.jp.vss.visitscheduler.infrastructure.users.AuthenticatedPrincipalEntity
import net.jp.vss.visitscheduler.infrastructure.users.UserDao
import net.jp.vss.visitscheduler.infrastructure.users.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.seasar.doma.jdbc.SelectOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * ScheduleDao のテスト.
 */
@JdbcRepositoryUnitTest
@ExtendWith(SpringExtension::class)
class ScheduleDaoTest {

    @Autowired
    private lateinit var sut: ScheduleDao

    @Autowired
    private lateinit var userDao: UserDao

    @Autowired
    private lateinit var authenticatedPrincipalDao: AuthenticatedPrincipalDao

    @Test
    @FlywayTest
    @DisplayName("create のテスト")
    fun testCreate() {
        // setup
        val user = createUser()
        val schedule = ScheduleEntityFixtures.create("SCHEDULE_0001", user.userId)

        // execution
        val actual = sut.create(schedule)

        // verify
        assertThat(actual.count).isEqualTo(1L)
        assertThat(actual.entity).isEqualTo(schedule)
    }

    @Test
    @FlywayTest
    @DisplayName("create のテスト(schedule_code の一意制約)")
    fun testCreate_DuplicateScheduleCode() {
        // setup
        val user = createUser()
        val schedule = ScheduleEntityFixtures.create("SCHEDULE_0001", user.userId)
        sut.create(schedule)

        // execution
        val actual = catchThrowable {
            sut.create(schedule.copy(scheduleId = UUID.randomUUID().toString()))
        }

        // verify
        assertThat(actual).isInstanceOf(DuplicateKeyException::class.java)
    }

    @Test
    @FlywayTest
    @DisplayName("create のテスト(user_id と target_start_date の一意制約)")
    fun testCreate_DuplicateUserIdTargetStartDate() {
        // setup
        val user = createUser()
        val schedule = ScheduleEntityFixtures.create("SCHEDULE_0001", user.userId)
        sut.create(schedule)

        // execution
        val actual = catchThrowable {
            sut.create(schedule.copy(scheduleId = UUID.randomUUID().toString(), scheduleCode = "SCHEDULE_0002"))
        }

        // verify
        assertThat(actual).isInstanceOf(DuplicateKeyException::class.java)
    }

    @Test
    @FlywayTest
    @DisplayName("create のテスト(FK 違反)")
    fun testCreate_InvalidUser() {
        // setup
        val schedule = ScheduleEntityFixtures.create()

        // execution
        val actual = catchThrowable {
            sut.create(schedule)
        }

        // verify
        assertThat(actual).isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    @FlywayTest
    @DisplayName("findByScheduleCodeAndUserCode のテスト")
    fun testFindByScheduleCodeAndUserCode() {
        // setup
        val user = createUser()
        val scheduleCode = "SCHE-0001"
        val schedule = createSchedule(scheduleCode, user.userId, LocalDate.parse("2020-01-01"))

        // execution
        val actual = sut.findByScheduleCodeAndUserCode(scheduleCode, user.userCode, SelectOptions.get())

        // verify
        assertThat(actual).isEqualTo(schedule.copy(userCode = user.userCode))
    }

    private fun createUser(userCode: String = "USER-0001"): UserEntity {
        val authenticatedPrincipal = AuthenticatedPrincipalEntity(authenticatedPrincipalId = "$userCode-TEST01",
            principal = "$userCode-HOGE-PRINCIPAL01", authorizedClientRegistrationId = "$userCode-google")
        authenticatedPrincipalDao.create(authenticatedPrincipal) // 外部参照の為
        val entity = UserEntity(userId = UUID.randomUUID().toString(), userCode = userCode,
            authenticatedPrincipalId = authenticatedPrincipal.authenticatedPrincipalId, userName = "$userCode-ユーザ名")
        return userDao.create(entity).entity
    }

    private fun createSchedule(scheduleCode: String, userId: String, targetStartDate: LocalDate): ScheduleEntity {
        val schedule = ScheduleEntityFixtures.create(scheduleCode, userId = userId)
            .copy(targetStartDate = targetStartDate)
        return sut.create(schedule).entity
    }
}
