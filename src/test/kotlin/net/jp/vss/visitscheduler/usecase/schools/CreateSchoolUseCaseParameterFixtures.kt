package net.jp.vss.visitscheduler.usecase.schools

/**
 * CreateSchoolUseCaseParameter の Fixture.
 */
class CreateSchoolUseCaseParameterFixtures {
    companion object {
        fun create() = CreateSchoolUseCaseParameter("SCHOOL_0001",
        "名前1", "メモ1", """{"age":30}""", "USER_0011")
    }
}
