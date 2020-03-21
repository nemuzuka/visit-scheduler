<template>
  <div class="modal" id="schedule-setting-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title dialog-title"><span>スケジュールを計算します</span></p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">
        <div class="content">
          <article class="message is-danger" v-if="globalErrorMessage !== ''">
            <div class="message-body">
              {{globalErrorMessage}}
            </div>
          </article>

          <div class="field">
            <label class="label">1回目の訪問日は先月の訪問日から何日経過してからにしますか？</label>
            <div class="select">
              <select v-model="daysToWaitSinceLastVisit">
                <option v-for="day in selectDays" v-bind:key="day" v-bind:value="day">{{day}}</option>
              </select>
            </div>
          </div>

          <div class="field">
            <label class="label">2回目の訪問日は1回目の訪問日から何日経過してからにしますか？</label>
            <div class="select">
              <select v-model="daysToWaitSinceVisitForTargetMonth">
                <option v-for="day in selectDays" v-bind:key="day" v-bind:value="day">{{day}}</option>
              </select>
            </div>
          </div>

        </div>
      </section>
      <footer class="modal-card-foot">

        <a class="button is-success" @click="calculateSchedule">
          <span class="icon is-small">
            <font-awesome-icon icon="calculator" />
          </span>
          <span>スケジュール計算</span>
        </a>

        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>
  import Utils from '../../utils'
  export default {
    name: 'schedule-setting-dialog',
    props: ["targetYearAndMonth", "schoolWithSchedules", "privateSchedules", "visitSchedules"],
    data() {
      return {
        globalErrorMessage: "",
        daysToWaitSinceLastVisit: 7,
        daysToWaitSinceVisitForTargetMonth:14,
        selectDays:[]
      }
    },
    created() {
      const self = this
      self.selectDays.splice(0, self.selectDays.length)
      for (let i = 1; i < 17; i++) {
        self.selectDays.push(i)
      }
    },
    methods: {
      openDialog() {
        Utils.openDialog('schedule-setting-dialog')
      },
      closeDialog() {
        Utils.closeDialog('schedule-setting-dialog')
      },
      calculateSchedule(e) {
        const self = this
        const workerExclusionDates = self.privateSchedules.map(privateSchedule=>{
          return {
            day: privateSchedule.target_day
          }
        })

        const schoolRequestedSchedules = self.schoolWithSchedules.filter(schoolWithSchedule=>schoolWithSchedule.calculation_target)
          .map(schoolWithSchedule => {
            const school = schoolWithSchedule.school
            const schedules = schoolWithSchedule.schedules
            let dayWithPriproties = []
            if(schedules !== null) {
              dayWithPriproties = schedules.map(schedule=>{
                return {
                  day: schedule.target_day,
                  priority: schedule.priority
                }
              })
            }
            return {
              school_code: school.school_code,
              last_month_visit_day: null, // TODO 後で設定する
              day_with_priproties: dayWithPriproties
            }
          })

        const requestVisitRules = {
          days_to_wait_since_last_visit: self.daysToWaitSinceLastVisit,
          days_to_wait_since_visit_for_target_month: self.daysToWaitSinceVisitForTargetMonth
        }
        const parameter = {
          target_year_and_month : self.targetYearAndMonth,
          worker_exclusion_dates: workerExclusionDates,
          school_requested_schedules: schoolRequestedSchedules,
          request_visit_rules: requestVisitRules
        }

        const callback = async () => {
          try {
            const response = await self.$http.post('/api/schedules/_calculate', parameter)

            self.$emit("RefreshVisitSchedule", e, response.data.visit_schedules)

            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              self.closeDialog()
            }, 1500)
          } catch(error) {
            self.$toasted.show('入力内容にエラーがあります')
            const errorData = error.response.data
            self.globalErrorMessage = errorData.message
          }
        }

        self.globalErrorMessage = ""
        callback()
      }
    }
  }
</script>
