package net.jp.vss.visitscheduler.controller.schedules

/**
 * SavePrivateScheduleApiParameter の Fixture.
 */
class SavePrivateScheduleApiParameterFixtures {

    companion object {
        fun create(): SavePrivateScheduleApiParameter = SavePrivateScheduleApiParameter(
                targetYearAndMonth = "2019-12",
                targetDayAndMemos = listOf(
                    SavePrivateScheduleApiParameter.TargetDayAndMemo(1, "メモ1"),
                    SavePrivateScheduleApiParameter.TargetDayAndMemo(3, "メモ2")))
    }
}
