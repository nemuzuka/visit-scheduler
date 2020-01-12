package net.jp.vss.visitscheduler.controller.schools

/**
 * CreateSchoolApiParameter の Fixture.
 */
class CreateSchoolApiParameterFixtures {

    companion object {
        fun create(): CreateSchoolApiParameter = CreateSchoolApiParameter(
                schoolCode = "SCHOOL_0001",
                name = "学校名1",
                memo = "メモ1",
                attributes = """{"hoge":"hage"}""")
    }
}
