<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">{{scheduleTitle}}</h1>
    </div>

    <div class="buttons are-medium">
      <button class="button" @click="movePrivateSchedule">個人スケジュール設定</button>
    </div>

    <p class="back"><a @click="moveTop"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>

  export default {
    name: 'schedule-detail',
    data() {
      return {
        scheduleTitle: "",
        scheduleCode: "",
        scheduleDetail:{},
        targetYearAndMonth : "2020-03" // ひとまず
      }
    },
    created () {
      const self = this
      self.scheduleTitle = "スケジュール" // XXX年XX月のスケジュール 的な記載にしたい
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
      }
    }
  }
</script>
