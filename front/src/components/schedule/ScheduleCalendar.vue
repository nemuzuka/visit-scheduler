<template>
  <div class="table-container">
    <table class="table is-bordered is-striped is-narrow is-hoverable is-fullwidth">
      <thead>
      <tr bgcolor="lightgray">
        <th></th>
        <th>訪問</th>
        <th @click="movePrivateSchedule" style="cursor: pointer">個人</th>
        <th v-for="schoolWithSchedule in schoolWithSchedules" :key="schoolWithSchedule.school.school_code"  @click="moveSchoolSchedule(schoolWithSchedule)" style="cursor: pointer">
          {{schoolWithSchedule.school.name}} <br><small class="label">{{viewLastMonthVisitDate(schoolWithSchedule)}}</small> <span v-bind:class="[activeClass, isCalculationTarget(schoolWithSchedule)?'is-info' : 'is-black']">{{viewSchoolDetail(schoolWithSchedule)}}</span>
        </th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="targetDay in targetDays" :key="targetDay.day">
        <td>{{targetDay.day}} ({{targetDay.weekdayString}})</td>
        <td><b>{{viewVisitSchedule(targetDay.day)}}</b></td>
        <td>{{viewPrivateSchedule(targetDay.day)}}</td>
        <td v-for="(schoolWithSchedule, index) in schoolWithSchedules" :key="schoolWithSchedule.school.school_code">
          {{viewSchoolSchedule(index, targetDay.day)}}
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
  import Moment from "moment"
  import Utils from "../../utils"

  export default {
    name: 'schedule-calendar',
    props: ["privateSchedules", "schoolWithSchedules", "targetYearAndMonth", "visitSchedules"],
    data() {
      return {
        targetDays:[
          {
            day: 1,
            weekdayString: ''
          }
        ],
        activeClass:"tag"
      }
    },
    methods: {
      refresh() {
        const self = this
        self.targetDays.splice(0, self.targetDays.length)

        const startDate = Moment(self.targetYearAndMonth+"-01", 'YYYY-MM-DD')
        const endOfDate = Moment(startDate).add(1, 'month')
        let targetDate = Moment(startDate)
        let day = 1
        while (targetDate.unix() < endOfDate.unix()) {

          self.targetDays.push({
            day: day,
            weekdayString: Utils.getWeekdayString(targetDate)
          })

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
        if(schoolWithSchedule.school_schedules === null) {
          return ""
        }
        const scheduleIndex = schoolWithSchedule.school_schedules.findIndex(schedule => schedule.target_day === day)
        if(scheduleIndex === -1) {
          return ""
        }

        const targetSchedule = schoolWithSchedule.school_schedules[scheduleIndex]
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
      },
      viewSchoolDetail(schoolWithSchedule){
        let info = "対象外"
        if(schoolWithSchedule.calculation_target) {
          info = "対象"
        }
        return info
      },
      isCalculationTarget(schoolWithSchedule) {
        return schoolWithSchedule.calculation_target
      },
      viewVisitSchedule(day) {
        const self = this
        const visitSchedule = self.visitSchedules.find(visitSchedule => visitSchedule.visit_day === day)
        if(visitSchedule === undefined) {
          return
        }

        const schoolCode = visitSchedule.school_code
        const schoolWithSchedule = self.schoolWithSchedules.find(schoolWithSchedule => schoolWithSchedule.school.school_code === schoolCode)
        if(schoolWithSchedule === undefined) {
          return
        }
        return schoolWithSchedule.school.name
      },
      viewLastMonthVisitDate(schoolWithSchedule) {
        const lastMonthVisitDate = schoolWithSchedule.last_month_visit_date
        if(lastMonthVisitDate === null) {
          return "最終訪問日:未設定"
        }
        return lastMonthVisitDate
      },
      movePrivateSchedule() {
        const self = this
        self.$router.push('/private-schedule/'+self.targetYearAndMonth)
      },
      moveSchoolSchedule(schoolWithSchedule) {
        const self = this
        const school = schoolWithSchedule.school
        self.$router.push("/school-schedule/" + self.targetYearAndMonth + "/school/" + school.school_code)
      }
    },
    watch: {
      targetYearAndMonth: function() {
        const self = this
        self.refresh()
      }
    }
  }
</script>

<style scoped>
  small.label {
    font-weight: normal;
    font-size: small;
  }
</style>
