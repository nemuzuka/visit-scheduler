<template>
  <div class="table-container">
    <table class="table is-bordered is-striped is-narrow is-hoverable is-fullwidth">
      <thead>
      <tr>
        <th></th>
        <th>訪問</th>
        <th>個人</th>
        <th v-for="schoolWithSchedule in schoolWithSchedules" :key="schoolWithSchedule.school.school_code">
          {{schoolWithSchedule.school.name}}
        </th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="day in days" :key="day">
        <td>{{day}} ({{weekdayStrings[day-1]}})</td>
        <td></td>
        <td>{{viewPrivateSchedule(day)}}</td>
        <td v-for="(schoolWithSchedule, index) in schoolWithSchedules" :key="schoolWithSchedule.school.school_code">
          {{viewSchoolSchedule(index, day)}}
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
  import Moment from "moment"
  import Utils from "../../../utils"

  export default {
    name: 'schedule-calendar',
    props: ["privateSchedules", "schoolWithSchedules", "targetYearAndMonth"],
    data() {
      return {
        days: [],
        weekdayStrings: []
      }
    },
    created () {
      const self = this
      self.refresh()
    },
    methods: {
      refresh() {
        const self = this
        self.days.splice(0, self.days.length)
        self.weekdayStrings.splice(0, self.weekdayStrings.length)

        const startDate = Moment(self.targetYearAndMonth+"-01", 'YYYY-MM-DD')
        const endOfDate = Moment(startDate).add(1, 'month')
        let targetDate = Moment(startDate)
        let day = 1
        while (targetDate.unix() < endOfDate.unix()) {
          self.days.push(day)
          self.weekdayStrings.push(Utils.getWeekdayString(targetDate))
          targetDate = Moment(targetDate).add(1, 'days')
          day++
        }
      },
      viewPrivateSchedule(day) {
        const self = this
        const index = self.privateSchedules.findIndex(privateSchedule => privateSchedule.target_day === day)
        if(index === -1) {
          return ""
        }

        if(self.privateSchedules[index].memo === null || self.privateSchedules[index].memo === '') {
          return "予定あり"
        }
        return self.privateSchedules[index].memo
      },
      viewSchoolSchedule(index, day) {
        const self = this
        const schoolWithSchedule = self.schoolWithSchedules[index]
        if(schoolWithSchedule.schedules === null) {
          return ""
        }
        const scheduleIndex = schoolWithSchedule.schedules.findIndex(schedule => schedule.target_day === day)
        if(scheduleIndex === -1) {
          return ""
        }

        const targetSchedule = schoolWithSchedule.schedules[scheduleIndex]
        const priority = targetSchedule.priority
        let message = ""
        if(priority === "DONT_COME") {
          message = "×"
        } else if(priority === "POSSIBLE") {
          message = "○"
        } else if(priority === "ABSOLUTELY") {
          message = "◎"
        }
        const memo = targetSchedule.memo
        if(memo !== null && memo !== '') {
          message += "(" + memo + ")"
        }
        return message
      }
    }
  }
</script>
