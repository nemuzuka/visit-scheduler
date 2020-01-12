package net.jp.vss.visitscheduler.domain

/**
 * ResourceAttributes の Fixture.
 */
class ResourceAttributesFixtures {

    companion object {
        fun create(): ResourceAttributes =
            ResourceAttributes(
                createUserCode = "USER_0001",
                createAt = 1556417319042,
                lastUpdateUserCode = "USER_002",
                lastUpdateAt = 1564279719042,
                version = 3L)
    }
}
