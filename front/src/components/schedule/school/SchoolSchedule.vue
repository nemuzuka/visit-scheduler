<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">学校スケジュール</h1>
    </div>

    <div class="message is-info">
      <h4 class="message-header">登録内容</h4>

      <div class="box">

        <article class="message is-danger" v-if="globalErrorMessage !== ''">
          <div class="message-body">
            {{globalErrorMessage}}
          </div>
        </article>

        <div class="notification primary is-light">
          {{schoolLabel}} の {{targetDateLabel}} のスケジュールを登録してください。<br><br>
          優先度は以下の通りです。
          <ul>
            <li>◎: この日は必ず訪問スケジュールに入れます</li>
            <li>○: この日を優先的にスケジュールに入れます</li>
            <li>×: この日は訪問スケジュールに入れません</li>
          </ul>
          <br>
          必要であれば先月の最終訪問日も設定してください。
        </div>


        <div class="tabs is-small">
          <ul>
            <li :class="{'is-active': isNormal}" @click="moveNormal"><a class="tab-element">通常</a></li>
            <li :class="{'is-active': isTextArea}" @click="moveBulkInput"><a class="tab-element">一括設定</a></li>
          </ul>
        </div>

        <div>
          <div v-if="isNormal">
            <table class="table is-bordered">
              <thead>
              <tr>
                <th class="trash"></th>
                <th>対象日(1-月末まで)</th>
                <th>優先度</th>
                <th>メモ</th>
              </tr>
              </thead>
              <tbody>
                <tr v-for="targetDayAndMemo in targetDayAndMemos" :key="targetDayAndMemo.key">
                  <td class="trash">
                    <button class="button is-danger is-outlined" @click="deleteTargetDayAndMemo(targetDayAndMemo.key)">
                      <span class="icon is-small">
                        <font-awesome-icon icon="trash" />
                      </span>
                    </button>
                  </td>
                  <td><input class="input input-day" type="text" v-model="targetDayAndMemo.targetDay"></td>
                  <td>
                    <div class="select">
                      <select v-model="targetDayAndMemo.priority">
                        <option v-for="option in options" :value="option.id" :key="option.id">
                          {{ option.name }}
                        </option>
                      </select>
                    </div>
                  </td>
                  <td><input class="input" type="text" v-model="targetDayAndMemo.memo"></td>
                </tr>
              </tbody>
            </table>

            <div class="buttons">

              <button class="button is-primary is-outlined" @click="addDefaultTargetDayAndMemo">
                <span class="icon is-small">
                  <font-awesome-icon icon="plus" />
                </span>
                <span>日付を追加</span>
              </button>

            </div>
          </div>

          <div v-if="isTextArea">
            <div class="notification primary is-light">
              Excel のデータを貼り付けます。<br>
              「優先度(tab 記号)メモ」で入力してください。<br>
              1行目が1日、2行目が2日のスケジュールになります。<br>
            </div>
            <textarea class="textarea" v-model="scheduleForTextareaValue" @change="bulkSetting"></textarea>
          </div>


          <div class="field">
            <label class="label">先月の最終訪問日</label>
            <div class="control">
              <datepicker v-model="lastMonthVisitDate" :format="DatePickerFormat" :language="ja" :input-class="DatePickerClass" :clear-button="true"></datepicker>
            </div>
          </div>

          <div class="field">
            <p class="control has-text-right">
              <button class="button is-info" @click="saveTargetDayAndMemos">
              <span class="icon is-small">
                <font-awesome-icon icon="save" />
              </span>
                <span>登録する</span>
              </button>
            </p>
          </div>

        </div>

      </div>

    </div>
    <p class="back"><a @click="moveScheduleDetail"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>
  import Uuid from 'uuid/v4'
  import Utils from "../../../utils"
  import Moment from 'moment'
  import Datepicker from 'vuejs-datepicker'
  import {ja} from 'vuejs-datepicker/dist/locale'

  export default {
    name: 'school-schedule',
    components :{
      Datepicker
    },
    data() {
      return {
        DatePickerClass: 'input input-date',
        ja: ja,
        DatePickerFormat: 'yyyy-MM-dd',
        editMode: 'NORMAL',
        scheduleForTextareaValue: '',
        globalErrorMessage: "",
        targetDateLabel:"",
        schoolLabel:"",
        schoolCode:"",
        targetYearAndMonth : "",
        targetDayAndMemos: [],
        lastMonthVisitDate: "",
        options:[
          {id: "ABSOLUTELY", name: "◎"},
          {id: "POSSIBLE", name: "○"},
          {id: "DONT_COME", name: "×"}
        ]
      }
    },
    async created () {
      const self = this
      self.editMode = 'NORMAL'
      self.scheduleForTextareaValue = ''
      self.targetYearAndMonth = self.$route.params.target_year_and_month
      self.schoolCode = self.$route.params.school_code

      self.targetDateLabel = Utils.targetYearAndMonthForView(self.targetYearAndMonth)

      const schoolRespons = await self.$http.get('/api/schools/' + self.schoolCode)
      self.schoolLabel = schoolRespons.data.name

      const response = await self.$http.get('/api/school-schedules/_by-school/' + self.schoolCode + '?target_year_and_month=' + self.targetYearAndMonth)
      self.targetDayAndMemos.splice(0,self.targetDayAndMemos.length)
      const targetDayAndMemos = response.data.school_schedules
      targetDayAndMemos.forEach(targetDayAndMemo => self.addTargetDayAndMemo(targetDayAndMemo.target_day, targetDayAndMemo.memo, targetDayAndMemo.priority, false))

      if(response.data.school_schedules.length === 0) {
        for(let i = 0; i < 3; i++) {
          self.addDefaultTargetDayAndMemo()
        }
      }

      self.lastMonthVisitDate = response.data.last_month_visit_date
    },
    methods: {
      addTargetDayAndMemo(targetDay, memo, priority, useUnshift) {
        const self = this

        if(targetDay !== '') {
          const index = self.targetDayAndMemos.findIndex((v) => v.targetDay === targetDay);
          if(index !== -1) {
            return
          }
        }

        const value = {
          key: Uuid(),
          targetDay: '' + targetDay, // 文字列として登録
          memo: memo,
          priority: priority
        };

        if(useUnshift) {
          self.targetDayAndMemos.unshift(value)
        } else {
          self.targetDayAndMemos.push(value)
        }
      },
      addDefaultTargetDayAndMemo() {
        const self = this
        self.addTargetDayAndMemo("", "", "DONT_COME",false)
      },
      deleteTargetDayAndMemo(key) {
        const self = this
        const index = self.targetDayAndMemos.findIndex((v) => v.key === key);
        if(index !== -1) {
          self.targetDayAndMemos.splice(index, 1)
        }
      },
      saveTargetDayAndMemos() {
        const self = this

        const errorEntries = self.targetDayAndMemos.filter(value => {
          const targetDay = value.targetDay
          if(targetDay === '') {
            return false
          }
          return Moment(self.targetYearAndMonth+'-'+targetDay, 'YYYY-MM-D', true).isValid() === false
        })

        if(errorEntries.length !== 0) {
          const errorDayString = errorEntries.map(value=>value.targetDay).join(',')
          alert("対象日の日付が不正です[" + errorDayString + "]")
          return
        }

        const targetDaySet = new Set()
        const targetDayAndMemos = self.targetDayAndMemos.filter(value=>{
          const targetDay = value.targetDay
          if(targetDay === '' || targetDaySet.has(targetDay)) {
            return false
          }
          targetDaySet.add(targetDay)
          return true
        }).map(value=>{
          return {
            target_day: parseInt(value.targetDay),
            memo: value.memo,
            priority: value.priority
          }
        })

        const callback = async () => {
          let last_month_visit_date = null
          if(self.lastMonthVisitDate === null || self.lastMonthVisitDate === '') {
            last_month_visit_date = null
          } else if(typeof self.lastMonthVisitDate === 'string') {
            last_month_visit_date = self.lastMonthVisitDate
          } else if(typeof self.lastMonthVisitDate === 'object') {
            // date 扱い
            last_month_visit_date = Moment(self.lastMonthVisitDate).format('YYYY-MM-DD')
          }
          const parameter = {
              target_year_and_month: self.targetYearAndMonth,
              school_code: self.schoolCode,
              target_day_and_memos: targetDayAndMemos,
              last_month_visit_date: last_month_visit_date
          }

          try {
            await self.$http.post('/api/school-schedules', parameter)
            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              self.moveScheduleDetail()
            }, 1500)
          } catch(error) {
            self.$toasted.show('入力内容にエラーがあります')

            if(error.response.status === 409) {
              self.globalErrorMessage = "その年月の学校スケジュールは既に存在します"
            } else if(error.response.data) {
              const errorData = error.response.data
              self.globalErrorMessage = errorData.message
            }
          }
        }

        self.globalErrorMessage = ""
        callback()

      },
      moveScheduleDetail() {
        const self = this
        self.$router.push('/detail-schedule/' + localStorage.sceduleCode)
      },
      moveNormal() {
        const self = this
        if(self.editMode === 'NORMAL') {
          return
        }
        self.editMode = 'NORMAL'
      },
      moveBulkInput() {
        const self = this
        if(self.editMode === 'TEXTAREA') {
          return
        }

        const dayAndMemoMap = new Map()
        const priorityMap = new Map([['ABSOLUTELY', '◎'], ['POSSIBLE', '○'], ['DONT_COME', '×']])
        this.targetDayAndMemos.filter(targetDayAndMemo => {
          return targetDayAndMemo.targetDay !== ''
        }).forEach(targetDayAndMemo => {
          dayAndMemoMap.set(targetDayAndMemo.targetDay,
            priorityMap.get(targetDayAndMemo.priority)+ '\t' +targetDayAndMemo.memo)
        })

        let textareaValue = ''
        if(dayAndMemoMap.size !== 0) {
          for(let day = 1; day <= 31; day++) {
            const priorityAndMemo = dayAndMemoMap.get("" + day)
            if(priorityAndMemo !== undefined) {
              textareaValue += priorityAndMemo + '\n'
            } else {
              textareaValue += '\n'
            }
          }
        }
        self.scheduleForTextareaValue = textareaValue
        self.editMode = 'TEXTAREA'
      },
      bulkSetting() {
        const self = this
        const results = self.scheduleForTextareaValue.split('\n')
        const priorityMap = new Map([['◎','ABSOLUTELY'], ['○','POSSIBLE'], ['×', 'DONT_COME']])

        const targetDayAndMemos = results.map((result, index) =>{
          if(result === '') {
            return {
              targetDay: undefined,
              priority: undefined,
              memo:undefined
            }
          }

          const targetDay = ""+(index+1)
          const datas = result.split('\t')
          const priority = priorityMap.get(datas[0]);
          const memo = datas.length >= 2 ? datas[1] : ''
          return {
            targetDay: targetDay,
            priority: priority,
            memo:memo
          }
        })

        self.targetDayAndMemos.splice(0, self.targetDayAndMemos.length)
        targetDayAndMemos.forEach(targetDayAndMemo =>{
          if(targetDayAndMemo.priority !== undefined) {
            self.addTargetDayAndMemo(targetDayAndMemo.targetDay, targetDayAndMemo.memo, targetDayAndMemo.priority, false)
          }
        })
      }
    },
    computed: {
      isNormal: function () {
        return this.editMode === 'NORMAL'
      },
      isTextArea: function() {
        return this.editMode === 'TEXTAREA'
      }
    }
  }
</script>

<style scoped>
  input.input-day {
    width: 3em;
  }

  a.tab-element {
    text-decoration: none !important;
  }
</style>
