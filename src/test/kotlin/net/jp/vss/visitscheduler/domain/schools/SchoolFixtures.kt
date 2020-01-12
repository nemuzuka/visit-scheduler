package net.jp.vss.visitscheduler.domain.schools

import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributesFixtures
import net.jp.vss.visitscheduler.domain.users.User

/**
 * School の Fixture.
 */
class SchoolFixtures {
    companion object {
        fun create() = create("SCHOOL-0001", "USER-0001")

        fun create(schoolCodeValue: String, userCodeValue: String): School {
            val schoolId = School.SchoolId("550e8400-e29b-41d4-a716-446655440000")
            val schoolCode = School.SchoolCode(schoolCodeValue)
            val userCode = User.UserCode(userCodeValue)
            val schoolDetail = School.SchoolDetail(name = "SCHOOL-0001の学校名",
                memo = "メモ",
                attributes = Attributes("""{"attri":"bute"}"""))
            val resourceAttributes = ResourceAttributesFixtures.create()
            return School(schoolId = schoolId, schoolCode = schoolCode, userCode = userCode,
                schoolDetail = schoolDetail, resourceAttributes = resourceAttributes)
        }
    }
}
