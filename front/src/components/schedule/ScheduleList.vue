<template>
  <div>
    <div v-if="schedules.length > 0">
      <div class="columns is-multiline">
        <schedule-item v-for="schedule in schedules" :schedule="schedule" :key="schedule.schedule_code"></schedule-item>
      </div>
    </div>

    <div v-if="message !== ''">
      <article class="message is-danger">
        <div class="message-body">{{message}}</div>
      </article>
    </div>

  </div>
</template>

<script>
  import ScheduleItem from './ScheduleItem'
  export default {
    components: {
      ScheduleItem
    },
    name: 'schedule-list',
    props:["schedules", "message"],
  }
</script>
