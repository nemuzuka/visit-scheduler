package net.jp.vss.visitscheduler.usecase.schools

/**
 * UpdateSchoolUseCaseParameter の Fixture.
 */
class UpdateSchoolUseCaseParameterFixture {

    companion object {
        fun create() = UpdateSchoolUseCaseParameter("SCHOOL_0001",
            "学校名1", "メモ1", """{"age":30}""", 3L, "USER_0012")

        fun createNullValue() = UpdateSchoolUseCaseParameter("SCHOOL_0002",
            null, null, null, null, "USER_0013")
    }
}
