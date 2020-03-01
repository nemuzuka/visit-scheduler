<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">{{scheduleTitle}}</h1>
    </div>

    <div class="buttons are-medium">
      <button class="button">個人スケジュール設定</button>
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
      }
    },
    created () {
      const self = this
      self.scheduleTitle = "スケジュール" // XXX年XX月のスケジュール 的な記載にしたい
      self.sceduleCode = self.$route.params.schedule_code
    },
    methods: {
      moveTop() {
        const self = this
        self.$router.push('/')
      }
    }
  }
</script>
