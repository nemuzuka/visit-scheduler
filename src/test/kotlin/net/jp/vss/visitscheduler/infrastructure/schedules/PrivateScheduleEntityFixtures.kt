package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate

/**
 * PrivateScheduleEntity の Fixture.
 */
class PrivateScheduleEntityFixtures {
    companion object {
        fun create(userId: String): PrivateScheduleEntity =
            PrivateScheduleEntity(privateScheduleId = "550e8400-e29b-41d4-a716-446655440000",
                userId = userId,
                targetDate = LocalDate.parse("2007-12-01"),
                memo = "授業参観",
                createUserCode = "CREATE_USER_0001",
                createAt = 1546732800001L,
                lastUpdateUserCode = "UPDATE_USER_0002",
                lastUpdateAt = 1546732800002L,
                versionNo = 123)
    }
}
