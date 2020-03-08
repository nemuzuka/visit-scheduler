package net.jp.vss.visitscheduler.infrastructure.schedules

import java.time.LocalDate

/**
 * SchoolScheduleEntity の Fixture.
 */
class SchoolScheduleEntityFixtures {
    companion object {
        fun create(schoolId: String): SchoolScheduleEntity =
            SchoolScheduleEntity(schoolScheduleId = "550e8400-e29b-41d4-a716-446655440000",
                schoolId = schoolId,
                targetDate = LocalDate.parse("2020-01-31"),
                memo = "授業参観",
                priority = "ABSOLUTELY",
                createUserCode = "CREATE_USER_0001",
                createAt = 1546732800001L,
                lastUpdateUserCode = "UPDATE_USER_0002",
                lastUpdateAt = 1546732800002L,
                versionNo = 123,
                schoolCode = "SCHOOL_0001")
    }
}
