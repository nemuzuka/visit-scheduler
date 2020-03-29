package net.jp.vss.visitscheduler.usecase.schedules

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

/**
 * ScheduleDetailUseCaseResult の Test.
 */
class ScheduleDetailUseCaseResultTest {

    companion object {
        val objectMapper = ObjectMapper()
    }

    @Test
    fun testJsonString() {
        // setup
        val sut = ScheduleDetailUseCaseResultFixtures.create()

        // execution
        val actual = ScheduleUseCaseResultTest.objectMapper.writeValueAsString(sut)

        // verify
        val schoolUseCaseResult = sut.schoolWithSchedules.schoolWithSchedules[0].school
        val schoolJsonString = """
            |{
            |   "school_code": "${schoolUseCaseResult.schoolCode}",
            |   "name": "${schoolUseCaseResult.name}",
            |   "memo": "${schoolUseCaseResult.memo}",
            |   "attributes": {
            |       "hoge":"hige",
            |       "fuga":{
            |           "neko":"nyan"
            |       }
            |   },
            |   "create_at": ${schoolUseCaseResult.resourceAttributesResult.createAt},
            |   "create_user_code": "${schoolUseCaseResult.resourceAttributesResult.createUserCode}",
            |   "last_update_at": ${schoolUseCaseResult.resourceAttributesResult.lastUpdateAt},
            |   "last_update_user_code": "${schoolUseCaseResult.resourceAttributesResult.lastUpdateUserCode}",
            |   "version": ${schoolUseCaseResult.resourceAttributesResult.version}
            |}
        """.trimMargin()

        val scheduleJsonString = """
            |   "schedule_code": "${sut.scheduleUseCaseResult.scheduleCode}",
            |   "target_year_and_month": "${sut.scheduleUseCaseResult.targetYearAndMonth}",
            |   "attributes":{
            |       "hoge":"hige",
            |       "fuga":{
            |           "neko":"nyan"
            |       }
            |   },
            |   "create_at": ${sut.scheduleUseCaseResult.resourceAttributesResult.createAt},
            |   "create_user_code": "${sut.scheduleUseCaseResult.resourceAttributesResult.createUserCode}",
            |   "last_update_at": ${sut.scheduleUseCaseResult.resourceAttributesResult.lastUpdateAt},
            |   "last_update_user_code": "${sut.scheduleUseCaseResult.resourceAttributesResult.lastUpdateUserCode}",
            |   "version": ${sut.scheduleUseCaseResult.resourceAttributesResult.version}
        """.trimMargin()

        val schoolSchedule = sut.schoolWithSchedules.schoolWithSchedules[0].schoolSchedule.schoolSchedules[0]
        val lastMonthVisitDate = sut.schoolWithSchedules.schoolWithSchedules[0].schoolSchedule.lastMonthVisitDate
        val expected = """
            |{
            |   $scheduleJsonString,
            |   "private_schedules": [
            |       {
            |           "target_day": ${sut.privateScheduleUseCaseResults[0].targetDay},
            |           "memo": "${sut.privateScheduleUseCaseResults[0].memo}"
            |       }
            |   ],
            |   "school_with_schedules": [
            |       {
            |           "school": $schoolJsonString,
            |           "calculation_target": ${sut.schoolWithSchedules.schoolWithSchedules[0].calculationTarget},
            |           "school_schedules": [
            |               {
            |                   "target_day": ${schoolSchedule.targetDay},
            |                   "memo": ${schoolSchedule.memo},
            |                   "priority": ${schoolSchedule.priority}
            |               }
            |           ],
            |           "last_month_visit_date": "$lastMonthVisitDate"
            |       }
            |   ],
            |   "visit_schedules": [
            |       {
            |           "visit_day": ${sut.calculateUseCaseResult.visitSchedules[0].day},
            |           "school_code": "${sut.calculateUseCaseResult.visitSchedules[0].schoolCode}"
            |       }
            |   ]
            |}
        """.trimMargin()
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT)
    }
}
