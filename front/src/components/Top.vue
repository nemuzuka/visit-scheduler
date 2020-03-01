<template>
  <div>
    <div class="box">
      <button class="button is-large is-fullwidth" @click="moveCreateSchedule">
      <span class="icon is-small">
        <font-awesome-icon icon="calendar" />
      </span>
        <span>スケジュールを登録する</span>
      </button>
    </div>

    <schedule-list :schedules="schedules" :message="scheduleMessage" @Refresh="refresh"></schedule-list>

    <p class="edit-user"><a @click="moveUserSettings"><font-awesome-icon icon="user-cog" /></a></p>
    <p class="school"><a @click="moveSchool"><font-awesome-icon icon="school" /></a></p>

  </div>
</template>

<script>

import ScheduleList from './schedule/ScheduleList'

export default {
  components: {
    ScheduleList
  },
  name: 'top',
  data() {
    return {
      schedules: [],
      scheduleMessage: ""
    }
  },
  created () {
    const self = this
    self.refresh()
  },
  methods: {
    async refresh() {
      const self = this
      self.scheduleMessage = ""

      const response = await self.$http.get('/api/schedules')
      self.schedules.splice(0, self.schedules.length)

      const schedules = response.data.elements
      self.schedules.push(...schedules)

      if(schedules.length <= 0) {
        self.scheduleMessage = "表示するスケジュールがありません"
      }
    },
    moveUserSettings() {
      const self = this
      self.$router.push('/user-settings')
    },
    moveSchool() {
      const self = this
      self.$router.push('/school')
    },
    moveCreateSchedule() {
      const self = this
      self.$router.push('/edit-schedule/_new')
    }
  }
}
</script>

<style scoped>
  p.school {
    position: fixed;
    right: 15px;
    top: 25%;
    z-index: 10;
  }
  p.edit-user {
    position: fixed;
    right: 15px;
    top: 35%;
    z-index: 10;
  }

  p.school a:hover,
  p.edit-user a:hover{
    background: #999;
    text-decoration: none;
  }
  p.school a,
  p.edit-user a {
    background: #666;
    color: #fff;
  }
  p.school a,
  p.edit-user a {
    opacity: .75;
    text-decoration: none;
    width: 55.5px;
    height: 55.5px;
    padding: 5px 0;
    text-align: center;
    display: block;
    border-radius: 5px;
    font-size: 200%;
  }
  p.school a i,
  p.edit-user a i {
    margin-top: 8px;
  }
</style>
