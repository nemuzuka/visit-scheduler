package net.jp.vss.visitscheduler.usecase.users

import net.jp.vss.visitscheduler.domain.users.UserFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * UpdateUserUseCaseParameter のテスト.
 */
class UpdateUserUseCaseParameterTest {

    @Test
    fun buildUpdateUserTest() {
        // setup
        val user = UserFixtures.create()
        val sut = UpdateUserUseCaseParameterFixture.create()

        // execution
        val actual = sut.buildUpdateUser(user)

        // verify
        val baseUserDetail = user.userDetail
        val userDetail = baseUserDetail.copy(userName = sut.userName)
        val expected = user.copy(userDetail = userDetail)
        assertThat(actual).isEqualTo(expected)
    }
}
