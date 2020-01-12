package net.jp.vss.visitscheduler.controller.schools

/**
 * UpdateSchoolApiParameter の Fixture.
 */
class UpdateSchoolApiParameterFixtures {
    companion object {
        fun create(): UpdateSchoolApiParameter = UpdateSchoolApiParameter(
            name = "update_学校名1",
            memo = "update_メモ1",
            attributes = """{"hoge":"super_hoge"}""")
    }
}
