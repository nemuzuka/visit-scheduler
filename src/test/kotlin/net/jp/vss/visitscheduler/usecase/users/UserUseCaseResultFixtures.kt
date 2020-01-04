package net.jp.vss.visitscheduler.usecase.users

/**
 * UserUseCaseResult の Fixture.
 */
class UserUseCaseResultFixtures {
    companion object {
        fun create() = UserUseCaseResult(
            userCode = "USER_001",
            userName = "ユーザ0001")
    }
}
