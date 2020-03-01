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

    @Test
    @FlywayTest
    @DisplayName("findAll のテスト")
    fun testFindAll() {
        // setup
        val user = createUser()
        val scheduleCode1 = "SCHE-0001"
        val schedule1 = createSchedule(scheduleCode1, user.userId, LocalDate.parse("2020-02-01"))
        val scheduleCode2 = "SCHE-0002"
        val schedule2 = createSchedule(scheduleCode2, user.userId, LocalDate.parse("2020-01-01"))

        // execution
        val actual = sut.findAll(user.userCode)

        // verify
        assertThat(actual).hasSize(2)
        assertThat(actual[0]).isEqualTo(schedule1.copy(userCode = user.userCode))
        assertThat(actual[1]).isEqualTo(schedule2.copy(userCode = user.userCode))
    }

    @Test
    @FlywayTest
    @DisplayName("findAll のテスト. 異なる user の schedule は取得できないこと")
    fun testFindAll_DiffrentUserCode() {
        // setup
        val user1 = createUser(userCode = "USER-0001")
        val user1ScheduleCode1 = "SCHE-0001"
        val user1Schedule1 = createSchedule(user1ScheduleCode1, user1.userId, LocalDate.parse("2020-02-01"))
        val user1ScheduleCode2 = "SCHE-0002"
        val user1Schedule2 = createSchedule(user1ScheduleCode2, user1.userId, LocalDate.parse("2020-03-01"))
        val user2 = createUser(userCode = "USER-0002")
        val user2ScheduleCode1 = "SCHE-0003"
        val user2Schedule1 = createSchedule(user2ScheduleCode1, user2.userId, LocalDate.parse("2020-02-01"))

        // execution
        val actual = sut.findAll(user1.userCode)

        // verify
        assertThat(actual).hasSize(2)
        assertThat(actual[0]).isEqualTo(user1Schedule2.copy(userCode = user1.userCode)) // order by で並び替え
        assertThat(actual[1]).isEqualTo(user1Schedule1.copy(userCode = user1.userCode))
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
            .copy(scheduleId = UUID.randomUUID().toString(), targetStartDate = targetStartDate)
        return sut.create(schedule).entity
    }
}
