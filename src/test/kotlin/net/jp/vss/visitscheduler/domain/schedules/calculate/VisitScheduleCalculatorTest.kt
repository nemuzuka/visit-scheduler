package net.jp.vss.visitscheduler.domain.schedules.calculate

import java.time.LocalDate
import java.time.Month
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/** VisitScheduleCalculator の Test. */
class VisitScheduleCalculatorTest {

    @Test
    fun testSetForceVisitDates() {
        // setup
        val schoolSchedule1 = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(), null,
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 3))),
            listOf())
        val schoolSchedule2 = SchoolSchedule(School.SchoolCode("CODE-02"), listOf(), null,
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 5))),
            listOf())
        val schoolSchedules = listOf(schoolSchedule1, schoolSchedule2)

        val sut = VisitScheduleCalculator(Schedule.TargetYearAndMonth("2019-01"),
            WorkerSchedule(listOf()), schoolSchedules, VisitRules(1, 1))

        val visitTargetDates = listOf(
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 2)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 3)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 4)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)))
        sut.setVisitTargetDates(visitTargetDates) // テスト対象の日付を絞る

        // exercise
        sut.setForceVisitDates()

        // verify
        assertThat(sut.getVisitTargetDates())
            .isEqualTo(
                listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 2)),
                    Schedule.ScheduleDate(LocalDate.of(2019, 1, 4))))
        assertThat(sut.getVisitDates())
            .isEqualTo(
                listOf(LocalDate.of(2019, 1, 3),
                    LocalDate.of(2019, 1, 5)))
    }

    @Test
    fun testSetForceVisitDates_AlreadySettingForceVisitDate() {
        // setup
        val schoolSchedule1 = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(), null,
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 5))),
            listOf())
        val schoolSchedule2 = SchoolSchedule(School.SchoolCode("CODE-02"), listOf(), null,
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 5))),
            listOf())
        val schoolSchedules = listOf(schoolSchedule1, schoolSchedule2)

        val sut = VisitScheduleCalculator(Schedule.TargetYearAndMonth("2019-01"),
            WorkerSchedule(listOf()), schoolSchedules, VisitRules(1, 1))

        // exercise
        val actual = catchThrowable { sut.setForceVisitDates() }

        // verify
        assertThat(actual).isInstanceOfSatisfying(IllegalArgumentException::class.java) { e ->
            assertThat(e.message)
                .isEqualTo("複数の学校で 2019-01-05 に対して強制訪問日として設定しています。見直してください。") }
    }

    @Test
    @DisplayName("choiceSchoolSchedule 3つの学校で同じ日を優先にしており、以降の3日間を元に設定校を判定。ポイントが一番小さい学校を採用")
    fun testChoiceSchoolSchedule1() {
        // setup
        val schoolSchedule1 = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 7)),
                    Schedule.ScheduleDate(LocalDate.of(2019, 1, 8)),
                    Schedule.ScheduleDate(LocalDate.of(2019, 1, 9))
                ))
        val schoolSchedule2 = SchoolSchedule(School.SchoolCode("CODE-02"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 7)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 8)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 9))
            ))
        val schoolSchedule3 = SchoolSchedule(School.SchoolCode("CODE-03"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 6))))
        val schoolSchedules = listOf(schoolSchedule1, schoolSchedule2, schoolSchedule3)

        val sut = VisitScheduleCalculator(Schedule.TargetYearAndMonth("2019-01"),
            WorkerSchedule(listOf()), schoolSchedules, VisitRules(10, 14))

        val visitTargetDates = listOf(
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 7)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 8)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 9)))
        sut.setVisitTargetDates(visitTargetDates) // テスト対象の日付を絞る

        // exercise
        val actual = sut.choiceSchoolSchedule(schoolSchedules, 1)

        // verify
        assertThat(actual).isEqualTo(schoolSchedule3) // 3つ目の学校が他の学校に比べてポイントが一番小さかったので採用
    }

    @Test
    @DisplayName("choiceSchoolSchedule 3つの学校で同じ日を優先にしており、以降の3日間を元に設定校を判定。ポイントが同じなので並び順で決定")
    fun testChoiceSchoolSchedule2() {
        // setup
        val schoolSchedule1 = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 7)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 8)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 9))))
        val schoolSchedule2 = SchoolSchedule(School.SchoolCode("CODE-02"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 7)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 8)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 9))))
        val schoolSchedule3 = SchoolSchedule(School.SchoolCode("CODE-03"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 7)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 8)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 9))))
        val schoolSchedules = listOf(schoolSchedule1, schoolSchedule2, schoolSchedule3)

        val sut = VisitScheduleCalculator(Schedule.TargetYearAndMonth("2019-01"),
            WorkerSchedule(listOf()), schoolSchedules, VisitRules(10, 14))

        val visitTargetDates = listOf(
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 7)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 8)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 9)))
        sut.setVisitTargetDates(visitTargetDates) // テスト対象の日付を絞る

        // exercise
        val actual = sut.choiceSchoolSchedule(schoolSchedules, 1)

        // verify
        assertThat(actual).isEqualTo(schoolSchedule1) // 並び順で1つめの学校が採用
    }

    @Test
    @DisplayName("choiceSchoolSchedule 最終日に対して3つの学校で同じ日を優先にしている")
    fun testChoiceSchoolSchedule3() {
        // setup
        val schoolSchedule1 = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 9))))
        val schoolSchedule2 = SchoolSchedule(School.SchoolCode("CODE-02"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 9))))
        val schoolSchedule3 = SchoolSchedule(School.SchoolCode("CODE-03"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 9))))
        val schoolSchedules = listOf(schoolSchedule1, schoolSchedule2, schoolSchedule3)

        val sut = VisitScheduleCalculator(Schedule.TargetYearAndMonth("2019-01"),
            WorkerSchedule(listOf()), schoolSchedules, VisitRules(10, 14))

        val visitTargetDates = listOf(
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 9)))
        sut.setVisitTargetDates(visitTargetDates) // テスト対象の日付を絞る

        // exercise
        val actual = sut.choiceSchoolSchedule(schoolSchedules, 0)

        // verify
        assertThat(actual).isEqualTo(schoolSchedule1)
    }

    @Test
    fun testSetPriorityVisitDates() {
        // setup
        val schoolSchedule1 = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 8))))
        val schoolSchedule2 = SchoolSchedule(School.SchoolCode("CODE-02"), listOf(), null,
            listOf(),
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 8))))
        val schoolSchedules = listOf(schoolSchedule1, schoolSchedule2)

        val sut = VisitScheduleCalculator(Schedule.TargetYearAndMonth("2019-01"),
            WorkerSchedule(listOf()), schoolSchedules, VisitRules(2, 1))
        val visitTargetDates = listOf(
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 7)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 8)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 9)))
        sut.setVisitTargetDates(visitTargetDates) // テスト対象の日付を絞る

        // exercise
        sut.setPriorityVisitDates()

        // verify
        assertThat(sut.getVisitTargetDates())
            .isEqualTo(listOf(
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 7)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 9))))
        assertThat(sut.getVisitDates())
            .isEqualTo(
                listOf(LocalDate.of(2019, 1, 5),
                    LocalDate.of(2019, 1, 6),
                    LocalDate.of(2019, 1, 8)))

        // 各学校の verify
        val expectedVisitSchedule1 = VisitSchedule.of(schoolSchedule1)
        expectedVisitSchedule1.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 6)))
        expectedVisitSchedule1.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 8)))
        val expectedVisitSchedule2 = VisitSchedule.of(schoolSchedule2)
        expectedVisitSchedule1.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)))
        assertThat(sut.getVisitScheduleMap()).isEqualTo(
            mapOf(expectedVisitSchedule1.schoolCode to expectedVisitSchedule1,
                expectedVisitSchedule2.schoolCode to expectedVisitSchedule2))
    }

    @Test
    fun testSetVisitDates() {
        // setup
        val schoolSchedule = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(), null, listOf(), listOf())
        val schoolSchedules = listOf(schoolSchedule)

        val sut = VisitScheduleCalculator(Schedule.TargetYearAndMonth("2019-01"),
            WorkerSchedule(listOf()), schoolSchedules, VisitRules(2, 14))
        val visitTargetDates = listOf(
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 18)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 19)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 20)))
        sut.setVisitTargetDates(visitTargetDates)

        // exercise
        sut.setVisitDates()

        // verify
        assertThat(sut.getVisitTargetDates())
            .isEqualTo(listOf(
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 18)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 19))))
        assertThat(sut.getVisitDates())
            .isEqualTo(
                listOf(LocalDate.of(2019, 1, 5),
                    LocalDate.of(2019, 1, 20)))

        // 各学校の verify
        val expectedVisitSchedule = VisitSchedule.of(schoolSchedule)
        expectedVisitSchedule.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)))
        expectedVisitSchedule.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 20)))
        assertThat(sut.getVisitScheduleMap()).isEqualTo(
            mapOf(expectedVisitSchedule.schoolCode to expectedVisitSchedule))
    }

    @Test
    fun testSetVisitDates_AlreadySetVisitDate() {
        // setup
        val schoolSchedule = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(), null,
            listOf(Schedule.ScheduleDate(LocalDate.of(2019, 1, 1)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 31))),
            listOf())
        val schoolSchedules = listOf(schoolSchedule)

        val sut = VisitScheduleCalculator(Schedule.TargetYearAndMonth("2019-01"),
            WorkerSchedule(listOf()), schoolSchedules, VisitRules(2, 14))
        sut.setForceVisitDates() // 強制訪問日を設定
        val visitTargetDates = listOf(
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 18)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 19)),
            Schedule.ScheduleDate(LocalDate.of(2019, 1, 20)))
        sut.setVisitTargetDates(visitTargetDates)

        // exercise
        sut.setVisitDates()

        // verify
        assertThat(sut.getVisitTargetDates())
            .isEqualTo(listOf(
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 5)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 18)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 19)),
                Schedule.ScheduleDate(LocalDate.of(2019, 1, 20))))
        assertThat(sut.getVisitDates())
            .isEqualTo(
                listOf(LocalDate.of(2019, 1, 1),
                    LocalDate.of(2019, 1, 31)))

        // 各学校の verify
        val expectedVisitSchedule = VisitSchedule.of(schoolSchedule)
        expectedVisitSchedule.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 1)))
        expectedVisitSchedule.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 31)))
        assertThat(sut.getVisitScheduleMap()).isEqualTo(
            mapOf(expectedVisitSchedule.schoolCode to expectedVisitSchedule))
    }

    @Test
    fun testCalculate() {
        // setup
        val workerSchedule = WorkerSchedule(
            listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 4)),
                Schedule.ScheduleDate(LocalDate.of(2020, 1, 21))))
        val schoolSchedule = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(),
            Schedule.ScheduleDate(LocalDate.of(2019, 12, 20)),
            listOf(), listOf())
        val schoolSchedules = listOf(schoolSchedule)

        val sut = VisitScheduleCalculator(Schedule.TargetYearAndMonth("2020-01"),
            workerSchedule, schoolSchedules, VisitRules(14, 15))

        // exercise
        val actual = sut.calculate()

        // verify
        assertThat(actual).hasSize(1)
        assertThat(actual[0].getVisitDateList()).isEqualTo(
            listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 5)),
                Schedule.ScheduleDate(LocalDate.of(2020, 1, 22))))
    }

    @Test
    fun testConstructor_InvalidParameter() {
        // setup
        val exclusionDates = (1..30).toList().map {
            Schedule.ScheduleDate(LocalDate.of(2020, Month.JANUARY, it))
        }.toList()
        val schoolSchedule = SchoolSchedule(School.SchoolCode("CODE-01"), listOf(), null, listOf(), listOf())
        val schoolSchedules = listOf(schoolSchedule)

        // exercise
        val actual = catchThrowable { VisitScheduleCalculator(Schedule.TargetYearAndMonth("2020-01"),
            WorkerSchedule(exclusionDates), schoolSchedules, VisitRules(14, 15)) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(IllegalStateException::class.java) { e ->
            assertThat(e.message)
                .isEqualTo("訪問可能日が足りないのでスケジュールを立てられません。") }
    }
}
