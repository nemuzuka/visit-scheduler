package net.jp.vss.visitscheduler.domain.users

/**
 * User の Fixture.
 */
class UserFixtures {
    companion object {
        fun create(): User =
            User(userId = User.UserId("550e8400-e29b-41d4-a716-446655440001"),
                userCode = User.UserCode("USER_0001"),
                userDetail = User.UserDetail(userName = "名前_0001"))
    }
}
