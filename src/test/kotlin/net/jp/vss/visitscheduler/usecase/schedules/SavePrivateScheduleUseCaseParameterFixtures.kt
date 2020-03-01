package net.jp.vss.visitscheduler.usecase.schedules

/**
 * SavePrivateScheduleUseCaseParameter の Fixture.
 */
class SavePrivateScheduleUseCaseParameterFixtures {
    companion object {
        fun create() = SavePrivateScheduleUseCaseParameter("2019-01",
            "USER_0011",
            listOf(
                SavePrivateScheduleUseCaseParameter.TargetDayAndMemo(3, "授業参観"),
                SavePrivateScheduleUseCaseParameter.TargetDayAndMemo(21, "補修")))
    }
}
