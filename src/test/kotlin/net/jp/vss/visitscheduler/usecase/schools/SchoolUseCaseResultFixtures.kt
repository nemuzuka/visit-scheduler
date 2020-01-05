package net.jp.vss.visitscheduler.usecase.schools

import net.jp.vss.visitscheduler.usecase.ResourceAttributesResultFixtures

/**
 * SchoolUseCaseResult の Fixture.
 */
class SchoolUseCaseResultFixtures {
    companion object {
        fun create() = SchoolUseCaseResult(
            schoolCode = "SCHOOL_0001",
            name = "学校1",
            memo = "メモ1",
            attributes = """{"hoge":"hige","fuga":{"neko":"nyan"}}""",
            resourceAttributesResult = ResourceAttributesResultFixtures.create()
        )
    }
}
