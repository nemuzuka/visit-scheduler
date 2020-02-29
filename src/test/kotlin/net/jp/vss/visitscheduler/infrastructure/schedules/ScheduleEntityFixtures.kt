package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate

/**
 * ScheduleEntity の Fixture.
 */
class ScheduleEntityFixtures {
    companion object {
        fun create() = create("SCHOOL-0001", "USER-0001")

        fun create(scheduleCode: String, userId: String): ScheduleEntity =
            ScheduleEntity(scheduleId = "550e8400-e29b-41d4-a716-446655440000",
                scheduleCode = scheduleCode,
                userId = userId,
                targetStartDate = LocalDate.parse("2007-12-01"),
                attributes = """{"attri":"bute"}""",
                createUserCode = "CREATE_USER_0001",
                createAt = 1546732800001L,
                lastUpdateUserCode = "UPDATE_USER_0002",
                lastUpdateAt = 1546732800002L,
                versionNo = 123,
                userCode = "USER_CODE_0001")
    }
}
