<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">{{scheduleTitle}}</h1>
    </div>

    <div class="buttons are-medium">
      <button class="button" @click="movePrivateSchedule">
        <span class="icon is-small">
          <font-awesome-icon icon="user-lock" />
        </span>
        <span>個人スケジュール設定</span>
      </button>
      <button class="button" @click="openSchoolScheduleDialog">
        <span class="icon is-small">
          <font-awesome-icon icon="school" />
        </span>
        <span>学校スケジュール設定</span>
      </button>
      <button class="button is-primary is-light" @click="openScheduleSettingDialog">
        <span class="icon is-small">
          <font-awesome-icon icon="chalkboard-teacher" />
        </span>
        <span>スケジュール計算</span>
      </button>

      <button class="button" @click="openSortSchoolDialog">
        <span class="icon is-small">
          <font-awesome-icon icon="arrows-alt-v" />
        </span>
        <span>優先度設定</span>
      </button>

    </div>

    <div class="box">

      <article class="message is-danger" v-if="globalErrorMessage !== ''">
        <div class="message-body">
          {{globalErrorMessage}}
        </div>
      </article>

      <div class="field">
        <p class="control has-text-right">
          <button class="button is-info" @click="saveVisitSchedule">
              <span class="icon is-small">
                <font-awesome-icon icon="save" />
              </span>
            <span>訪問スケジュールを登録する</span>
          </button>
        </p>
      </div>

      <schedule-calendar :targetYearAndMonth="targetYearAndMonth" :privateSchedules="privateSchedules" :schoolWithSchedules="schoolWithSchedules" :visitSchedules="visitSchedules" ref="scheduleCalendar"></schedule-calendar>

    </div>

    <select-school-dialog :targetYearAndMonth="targetYearAndMonth" :schoolWithSchedules="schoolWithSchedules" ref="selectSchoolDialog"></select-school-dialog>
    <schedule-setting-dialog :targetYearAndMonth="targetYearAndMonth" :schoolWithSchedules="schoolWithSchedules" :privateSchedules="privateSchedules" :visitSchedules="visitSchedules" ref="scheduleSettingDialog" @RefreshVisitSchedule="refreshVisitSchedule"></schedule-setting-dialog>
    <sort-school-dialog :targetYearAndMonth="targetYearAndMonth" :schoolWithSchedules="schoolWithSchedules" :scheduleCode="scheduleCode" ref="sortSchoolDialog" @Refresh="refresh"></sort-school-dialog>

    <p class="back"><a @click="moveTop"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>

  import Utils from "../../utils"
  import SelectSchoolDialog from "./school/SelectSchoolDialog"
  import SortSchoolDialog from "./school/SortSchoolDialog"
  import ScheduleCalendar from "./ScheduleCalendar"
  import ScheduleSettingDialog from "./ScheduleSettingDialog"

  export default {
    name: 'schedule-detail',
    components:{
      SelectSchoolDialog,
      SortSchoolDialog,
      ScheduleCalendar,
      ScheduleSettingDialog
    },
    data() {
      return {
        globalErrorMessage: "",
        scheduleTitle: "",
        scheduleCode: "",
        scheduleDetail:{
          version: 0
        },
        targetYearAndMonth : "",
        schoolWithSchedules:[],
        privateSchedules:[],
        visitSchedules:[]
      }
    },
    created () {
      const self = this
      self.scheduleCode = self.$route.params.schedule_code
      localStorage.sceduleCode = self.scheduleCode

      self.refresh()
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
      },
      openSortSchoolDialog() {
        const self = this
        self.$refs.sortSchoolDialog.openDialog()
      },
      openScheduleSettingDialog() {
        const self = this
        self.$refs.scheduleSettingDialog.openDialog()
      },
      refreshVisitSchedule(e, list) {
        const self = this
        self.visitSchedules.splice(0, self.visitSchedules.length)
        self.visitSchedules.push(...list)

        self.$refs.scheduleCalendar.refresh()
      },
      async refresh() {
        const self = this
        try {
          const response = await self.$http.get('/api/schedules/' + self.scheduleCode)

          const scheduleDetail = response.data
          self.scheduleDetail.version = scheduleDetail.version

          self.schoolWithSchedules.splice(0, self.schoolWithSchedules.length)
          self.schoolWithSchedules.push(...scheduleDetail.school_with_schedules)

          self.privateSchedules.splice(0, self.privateSchedules.length)
          self.privateSchedules.push(...scheduleDetail.private_schedules)

          self.visitSchedules.splice(0, self.visitSchedules.length)
          self.visitSchedules.push(...scheduleDetail.visit_schedules)

          self.targetYearAndMonth = scheduleDetail.target_year_and_month
          self.scheduleTitle = Utils.targetYearAndMonthForView(self.targetYearAndMonth) + " スケジュール"

        } catch(error) {
          if(error.response.status === 404) {
            const errorData = error.response.data
            alert(errorData.message)
          }
          await self.$router.push('/')
        }
      },
      saveVisitSchedule() {
        const self = this
        const parameter = {
          target_year_and_month: self.targetYearAndMonth,
          visit_day_and_school_codes: self.visitSchedules.map(visitSchedule=>{
            return {
              visit_day: visitSchedule.visit_day,
              school_code: visitSchedule.school_code
            }
          })
        }

        const callback = async () => {
          try {
            await self.$http.post('/api/visit-schedules', parameter)
            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              self.$router.push('/detail-schedule/' + localStorage.sceduleCode)
            }, 1500)
          } catch(error) {
            self.$toasted.show('入力内容にエラーがあります')

            if(error.response.data) {
              const errorData = error.response.data
              self.globalErrorMessage = errorData.message
            }
          }
        }

        self.globalErrorMessage = ""
        callback()
      }
    }
  }
</script>
