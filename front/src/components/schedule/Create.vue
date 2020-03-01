<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">スケジュールを登録します</h1>
    </div>

    <div class="message is-info">
      <h4 class="message-header">登録内容</h4>

      <div class="box">

        <article class="message is-danger" v-if="globalErrorMessage !== ''">
          <div class="message-body">
            {{globalErrorMessage}}
          </div>
        </article>

        <ValidationObserver v-slot="{handleSubmit, valid}">
          <form @submit.prevent>

            <div class="field is-grouped">

              <div class="control">
                <div class="select">
                  <select v-model="selectedYear" @change="changeTargetYearAndMonth">
                    <option v-for="year in selectYears" v-bind:key="year" v-bind:value="year">{{year}}</option>
                  </select>
                </div>
              </div>

              <div class="control is-vertical-center">
                  <label class="label">年</label>
              </div>

              <div class="control">
                <div class="select">
                  <select v-model="selectedMonth" @change="changeTargetYearAndMonth">
                    <option v-for="month in selectMonths" v-bind:key="month.key" v-bind:value="month.value">{{month.label}}</option>
                  </select>
                </div>
              </div>

              <div class="control is-vertical-center">
                <label class="label">月</label>
              </div>

              <div class="control is-vertical-center">
                分のスケジュールを作成します
              </div>

            </div>

            <div class="field">

              <div>
                <table class="table is-bordered">
                  <thead>
                  <tr>
                    <th>対象</th>
                    <th>学校名</th>
                  </tr>
                  </thead>
                  <draggable v-model="schoolAndCalculationTargets" tag="tbody">
                    <tr v-for="schoolAndCalculationTarget in schoolAndCalculationTargets" :key="schoolAndCalculationTarget.school.school_code" style="cursor: pointer">
                      <td class="has-text-centered"><input type="checkbox" v-model="schoolAndCalculationTarget.calculationTarget"></td>
                      <td>{{schoolAndCalculationTarget.school.name}}</td>
                    </tr>
                  </draggable>
                </table>

                <div class="notification primary is-light">
                  学校の並び順を Drag & Drop で変更することでスケジュールを採用する優先度が変わります。上に行くほど優先度が上がります。<br>
                  ※後から変更することも可能です。
                </div>

              </div>
            </div>

            <div class="field">
              <p class="control has-text-right">
                <button class="button is-info" :disabled="!valid" @click="handleSubmit(saveSchedule)">
              <span class="icon is-small">
                <font-awesome-icon icon="save" />
              </span>
                  <span>登録する</span>
                </button>
              </p>
            </div>

          </form>
        </ValidationObserver>
      </div>
    </div>

    <p class="back"><a @click="moveTop"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>

  import Uuid from 'uuid/v4'
  import Moment from "moment"
  import draggable from "vuedraggable"

  export default {
    name: 'schedule-create',
    components: {
      draggable
    },
    data() {
      return {
        targetYearAndMonth: "",
        selectedYear : "",
        selectedMonth : "",
        globalErrorMessage: "",
        schoolAndCalculationTargets: [],
        selectYears:[],
        selectMonths:[]
      }
    },
    async created () {
      const self = this
      self.globalErrorMessage = ""

      const response = await self.$http.get('/api/schools')
      self.schoolAndCalculationTargets.splice(0, self.schoolAndCalculationTargets.length)

      const schools = response.data.elements

      schools.forEach(school =>{
        self.schoolAndCalculationTargets.push({
          school: school,
          calculationTarget: true
        })
      })

      const targetYearAndMonth = self.addMonthStringBySystemDate()
      self.targetYearAndMonth = targetYearAndMonth
      self.selectedYear = targetYearAndMonth.slice(0, 4)
      self.selectedMonth = targetYearAndMonth.slice(-2).padStart(2, "0")

      // 年の select 要素
      self.selectYears.splice(0, self.selectYears.length)
      for (let i = parseInt(self.selectedYear) - 2; i < parseInt(self.selectedYear) + 3; i++) {
        self.selectYears.push(i.toString())
      }

      // 月の select 要素
      self.selectMonths.splice(0, self.selectMonths.length)
      for (let i = 0; i < 12; i++) {
        self.selectMonths.push({
          value: (i + 1).toString().padStart(2, "0"),
          label: (i + 1).toString()
        })
      }
    },
    methods: {
      addMonthStringBySystemDate() {
        const now = new Date();
        const addedDate = new Date(now.getTime())
        addedDate.setMonth(now.getMonth() + 1)
        if(now.getDate() > addedDate.getDate()) {
          addedDate.setDate(0);
        }

        const moment = Moment(addedDate.getTime())
        return moment.format("YYYY-MM")
      },
      changeTargetYearAndMonth() {
        const self = this
        self.targetYearAndMonth = self.selectedYear + "-" + self.selectedMonth
      },
      saveSchedule() {

        const self = this
        const callback = async () => {

          const parameter = {
            schedule_code: Uuid(),
            target_year_and_month: self.targetYearAndMonth,
            attributes: null,
            school_code_and_calculation_targets:self.schoolAndCalculationTargets.map(schoolAndCalculationTarget=>{
              return {
                school_code: schoolAndCalculationTarget.school.school_code,
                calculation_target: schoolAndCalculationTarget.calculationTarget
              }
            })
          }

          try {
            await self.$http.post('/api/schedules', parameter)
            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              self.$router.push('/detail-schedule/' + parameter.schedule_code)
            }, 1500)
          } catch(error) {
            self.$toasted.show('入力内容にエラーがあります')

            if(error.response.status === 409) {
              self.globalErrorMessage = "その年月のスケジュールは既に存在します"
            } else if(error.response.data) {
              const errorData = error.response.data
              self.globalErrorMessage = errorData.message
            }
          }
        }

        self.globalErrorMessage = ""
        callback()
      },
      moveTop() {
        const self = this
        self.$router.push('/')
      }
    }
  }
</script>
