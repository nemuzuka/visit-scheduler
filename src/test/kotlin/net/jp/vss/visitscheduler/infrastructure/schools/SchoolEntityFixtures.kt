package net.jp.vss.visitscheduler.infrastructure.schools

/**
 * SchoolEntity の Fixture.
 */
class SchoolEntityFixtures {
    companion object {
        fun create() = create("SCHOOL-0001", "USER-0001")

        fun create(schoolCode: String, userCode: String): SchoolEntity =
            SchoolEntity(schoolId = "550e8400-e29b-41d4-a716-446655440000",
            schoolCode = schoolCode,
            userCode = userCode,
            name = "学校名1",
            memo = "メモ1",
            attributes = """{"attri":"bute"}""",
            createUserCode = "CREATE_USER_0001",
            createAt = 1546732800001L,
            lastUpdateUserCode = "UPDATE_USER_0002",
            lastUpdateAt = 1546732800002L,
            versionNo = 123)
    }
}
