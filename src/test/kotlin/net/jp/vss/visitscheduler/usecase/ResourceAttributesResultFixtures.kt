package net.jp.vss.visitscheduler.usecase

/**
 * ResourceAttributesResult の Fixture.
 */
class ResourceAttributesResultFixtures {

    companion object {
        fun create() = ResourceAttributesResult(
            createUserCode = "CREATE_USER_001",
            createAt = 1896268400000L,
            lastUpdateUserCode = "UPDATE_USER_001",
            lastUpdateAt = 1896268400001L,
            version = 3L)
    }
}
