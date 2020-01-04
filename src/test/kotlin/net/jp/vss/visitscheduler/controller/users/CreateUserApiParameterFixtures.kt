package net.jp.vss.visitscheduler.controller.users

/**
 * CreateUserApiParameter の Fixture.
 */
class CreateUserApiParameterFixtures {

    companion object {
        fun create(): CreateUserApiParameter = CreateUserApiParameter(
                userCode = "USER_0001",
                userName = "名前1")
    }
}
