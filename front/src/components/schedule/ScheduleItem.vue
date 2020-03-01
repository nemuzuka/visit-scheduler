<template>
  <div class="column is-3">
    <div class="card card-area" style="cursor: pointer;" @click.stop="moveScheduleDetail">
      <div class="card-content">
        <p class="title">
          {{title}}
        </p>
      </div>
    </div>

  </div>
</template>

<script>
  import Utils from '../../utils.js'

  export default {
    name: 'schedule-item',
    props: ["schedule"],
    data() {
      return {
        title: ""
      }
    },
    created () {
      const self = this
      self.title = Utils.targetYearAndMonthForView(self.schedule.target_year_and_month)
    },
    methods: {
      moveScheduleDetail() {
        const self = this
        self.$router.push('/detail-schedule/' + self.schedule.schedule_code)
      }
    }
  }
</script>

<style scoped>
  .card-content p.title {
    margin-top: 10px;
  }

  div.card-area {
    border: solid #363636 2px;
  }

  div.card-area:hover {
    background: gainsboro;
    border: dashed #363636 2px;
  }
</style>
