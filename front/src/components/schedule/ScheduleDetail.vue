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

    <select-school-dialog :targetYearAndMonth="targetYearAndMonth" :schools="schools" ref="selectSchoolDialog"></select-school-dialog>

    <p class="back"><a @click="moveTop"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>

  import SelectSchoolDialog from "./school/SelectSchoolDialog"
  import Utils from "../../utils"

  export default {
    name: 'schedule-detail',
    components:{
      SelectSchoolDialog
    },
    data() {
      return {
        scheduleTitle: "",
        scheduleCode: "",
        scheduleDetail:{},
        targetYearAndMonth : "2020-03", // ひとまず
        schools:[
          {
            school_code:"96c36211-3c06-40a1-a2cf-c5fe7ed08270",
            name: "学校名1",
            calculationTarget: true
          }
        ]
      }
    },
    created () {
      const self = this
      self.scheduleTitle = Utils.targetYearAndMonthForView(self.targetYearAndMonth) + " スケジュール" // XXX年XX月のスケジュール 的な記載にしたい
      self.sceduleCode = self.$route.params.schedule_code
      localStorage.sceduleCode = self.sceduleCode
      // TODO schedule.targetYearAndMonth を更新する
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
