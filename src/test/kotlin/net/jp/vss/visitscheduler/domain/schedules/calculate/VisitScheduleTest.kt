package net.jp.vss.visitscheduler.domain.schedules.calculate

import java.time.LocalDate
import net.jp.vss.visitscheduler.domain.schedules.Schedule
import net.jp.vss.visitscheduler.domain.schools.School
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/** VisitSchedule の Test */
class VisitScheduleTest {

    @Test
    fun testAddvisitDate() {
        // setup
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)

        // exercise
        sut.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 12, 1)))

        // verify
        assertThat(sut.getVisitDateList()).isEqualTo(listOf(Schedule.ScheduleDate(LocalDate.of(2019, 12, 1))))
    }

    @Test
    fun testIsOutOfRangeFromLastMonthVisitDate_NullLastMonthVisitDate() {
        // setup
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        val visitRules = VisitRules(10, 20)

        // exercise
        val actual = sut.isOutOfRangeFromLastMonthVisitDate(LocalDate.of(2019, 12, 31), visitRules)

        // verify
        assertThat(actual).isTrue()
    }

    @Test
    @DisplayName("先月の最終訪問日の範囲外.")
    fun testIsOutOfRangeFromLastMonthVisitDate_OutOfRange() {
        // setup
        val visitRules = VisitRules(10, 20)
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), Schedule.ScheduleDate(LocalDate.of(2019, 11, 20)))

        // exercise
        val actual = sut.isOutOfRangeFromLastMonthVisitDate(LocalDate.of(2019, 12, 1), visitRules)

        // verify
        assertThat(actual).isTrue() // 計算結果は 2019-11-30 で、2019-12-01 は範囲外なので、true
    }

    @Test
    @DisplayName("先月の最終訪問日の範囲内.")
    fun testIsOutOfRangeFromLastMonthVisitDate_WithinRange() {
        // setup
        val visitRules = VisitRules(10, 20)
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), Schedule.ScheduleDate(LocalDate.of(2019, 11, 20)))

        // exercise
        val actual = sut.isOutOfRangeFromLastMonthVisitDate(LocalDate.of(2019, 11, 30), visitRules)

        // verify
        assertThat(actual).isFalse() // 計算結果は 2019-11-30 で、2019-11-30 は範囲内なので、false
    }

    @Test
    @DisplayName("今月の訪問日の範囲外.From 側")
    fun testIsOutOfRangeFromVisitDate_OutOfRange_From() {
        // setup
        val visitRules = VisitRules(10, 13)
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        val visitDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 1))

        // exercise
        val actual = sut.isOutOfRangeFromVisitDate(visitDate, targetDate, visitRules)

        // verify
        assertThat(actual).isTrue()
    }

    @Test
    @DisplayName("今月の訪問日の範囲内.From 側")
    fun testIsOutOfRangeFromVisitDate_WithInRange_From() {
        // setup
        val visitRules = VisitRules(10, 13)
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        val visitDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 2))

        // exercise
        val actual = sut.isOutOfRangeFromVisitDate(visitDate, targetDate, visitRules)

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    @DisplayName("今月の訪問日の範囲外.To 側")
    fun testIsOutOfRangeFromVisitDate_OutOfRange_To() {
        // setup
        val visitRules = VisitRules(10, 13)
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        val visitDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 29))

        // exercise
        val actual = sut.isOutOfRangeFromVisitDate(visitDate, targetDate, visitRules)

        // verify
        assertThat(actual).isTrue()
    }

    @Test
    @DisplayName("今月の訪問日の範囲内.To 側")
    fun testIsOutOfRangeFromVisitDate_WithInRange_To() {
        // setup
        val visitRules = VisitRules(10, 13)
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        val visitDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 28))

        // exercise
        val actual = sut.isOutOfRangeFromVisitDate(visitDate, targetDate, visitRules)

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    @DisplayName("今月の訪問日の範囲内")
    fun testIsOutOfRangeFromVisitDate_WithInRange() {
        // setup
        val visitRules = VisitRules(10, 13)
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        val visitDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 15)) // 同じ日

        // exercise
        val actual = sut.isOutOfRangeFromVisitDate(visitDate, targetDate, visitRules)

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    @DisplayName("すでに埋まっている.")
    fun testIsVisitTargetDate_Completed() {
        // setup
        val visitRules = VisitRules(10, 13)
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))

        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        sut.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2020, 1, 6)))
        sut.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2020, 1, 26)))

        // exercise
        val actual = sut.isVisitTargetDate(targetDate, visitRules)

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    @DisplayName("除外日を指定した.")
    fun testIsVisitTargetDate_ContainsExclusionDate() {
        // setup
        val visitRules = VisitRules(10, 13)
        val sut = VisitSchedule(School.SchoolCode("dummy"),
            listOf(Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))),
            null)
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 15))

        // exercise
        val actual = sut.isVisitTargetDate(targetDate, visitRules)

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    @DisplayName("先月の訪問日から一定期間経過していない.")
    fun testIsVisitTaregtDate_WithinRangeFromLastMonthVisitDate() {
        // setup
        val visitRules = VisitRules(10, 13)
        val sut = VisitSchedule(School.SchoolCode("dummy"),
            listOf(), Schedule.ScheduleDate(LocalDate.of(2019, 12, 30)))
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 9))

        // exercise
        val actual = sut.isVisitTargetDate(targetDate, visitRules)

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    @DisplayName("今月の訪問日の範囲内")
    fun testIsVisitTargetDate_WithinRangeFromVisitForTargetMonth() {
        // setup
        val visitRules = VisitRules(1, 10)
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 20))

        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        sut.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2020, 1, 10)))

        // exercise
        val actual = sut.isVisitTargetDate(targetDate, visitRules)

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    @DisplayName("先月の訪問日から一定期間経過しており、今月の訪問日の範囲外である")
    fun testIsVisitTaregtDate() {
        // setup
        val visitRules = VisitRules(1, 10)
        val targetDate = Schedule.ScheduleDate(LocalDate.of(2020, 1, 25))

        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(),
            Schedule.ScheduleDate(LocalDate.of(2019, 12, 22)))
        sut.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 10)))

        // exercise
        val actual = sut.isVisitTargetDate(targetDate, visitRules)

        // verify
        assertThat(actual).isTrue()
    }

    @Test
    fun testIsCompleted_EmptyVisitDates() {
        // setup
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)

        // exercise
        val actual = sut.isCompleted()

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    fun testIsCompleted_InvalidSizeVisitDates() {
        // setup
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        sut.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 10)))

        // exercise
        val actual = sut.isCompleted()

        // verify
        assertThat(actual).isFalse()
    }

    @Test
    fun testIsCompleted() {
        // setup
        val sut = VisitSchedule(School.SchoolCode("dummy"), listOf(), null)
        sut.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 10)))
        sut.addVisitDate(Schedule.ScheduleDate(LocalDate.of(2019, 1, 30)))

        // exercise
        val actual = sut.isCompleted()

        // verify
        assertThat(actual).isTrue()
    }
}
