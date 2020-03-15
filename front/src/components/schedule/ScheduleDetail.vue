<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">{{scheduleTitle}}</h1>
    </div>

    <div class="buttons are-medium">
      <button class="button" @click="movePrivateSchedule">個人スケジュール設定</button>
      <button class="button" @click="openSchoolScheduleDialog">学校スケジュール設定</button>
      <button class="button">優先度設定</button>
    </div>

    <schedule-calendar :targetYearAndMonth="targetYearAndMonth" :privateSchedules="privateSchedules" :schoolWithSchedules="schoolWithSchedules"></schedule-calendar>

    <select-school-dialog :targetYearAndMonth="targetYearAndMonth" :schoolWithSchedules="schoolWithSchedules" ref="selectSchoolDialog"></select-school-dialog>

    <p class="back"><a @click="moveTop"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>

  import Utils from "../../utils"
  import SelectSchoolDialog from "./school/SelectSchoolDialog"
  import ScheduleCalendar from "./school/ScheduleCalendar"

  export default {
    name: 'schedule-detail',
    components:{
      SelectSchoolDialog,
      ScheduleCalendar
    },
    data() {
      return {
        scheduleTitle: "",
        scheduleCode: "",
        scheduleDetail:{
          version: 0
        },
        targetYearAndMonth : "",
        schoolWithSchedules:[],
        privateSchedules:[]
      }
    },
    async created () {
      const self = this
      self.sceduleCode = self.$route.params.schedule_code
      localStorage.sceduleCode = self.sceduleCode

      try {
        const response = await self.$http.get('/api/schedules/' + self.sceduleCode)

        const scheduleDetail = response.data
        self.targetYearAndMonth = scheduleDetail.target_year_and_month
        self.scheduleTitle = Utils.targetYearAndMonthForView(self.targetYearAndMonth) + " スケジュール"
        self.scheduleDetail.version = scheduleDetail.version

        self.schoolWithSchedules.splice(0, self.schoolWithSchedules.length)
        self.schoolWithSchedules.push(...scheduleDetail.school_with_schedules)

        self.privateSchedules.splice(0, self.privateSchedules.length)
        self.privateSchedules.push(...scheduleDetail.private_schedules)

      } catch(error) {
        console.log(error.response.data)
        if(error.response.status === 404) {
          const errorData = error.response.data
          alert(errorData.message)
        }
        await self.$router.push('/')
      }
    },
    methods: {
      moveTop() {
        const self = this
        self.$router.push('/')
      },
      movePrivateSchedule() {
        const self = this
        self.$router.push('/private-schedule/'+self.targetYearAndMonth)
      },
      openSchoolScheduleDialog(){
        const self = this
        self.$refs.selectSchoolDialog.openDialog()
      }
    }
  }
</script>
