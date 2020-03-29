<template>
  <div class="modal" id="sort-school-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title dialog-title"><span>優先度設定</span></p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">
        <div class="content">

          <div>
            <table class="table is-bordered">
              <thead>
              <tr>
                <th class="label-target">対象</th>
                <th>学校名</th>
              </tr>
              </thead>
              <draggable v-model="schoolWithSchedulesForDialog" tag="tbody">
                <tr v-for="schoolWithSchedule in schoolWithSchedulesForDialog" :key="schoolWithSchedule.school.school_code" style="cursor: pointer">
                  <td class="has-text-centered"><input type="checkbox" v-model="schoolWithSchedule.calculation_target"></td>
                  <td>{{schoolWithSchedule.school.name}}</td>
                </tr>
              </draggable>
            </table>

            <div class="notification primary is-light">
              学校の並び順を Drag & Drop で変更することでスケジュールを採用する優先度が変わります。上に行くほど優先度が上がります。
            </div>

          </div>
        </div>

      </section>
      <footer class="modal-card-foot">

        <a class="button is-info" @click="updateSort">
          <span class="icon is-small">
            <font-awesome-icon icon="edit" />
          </span>
          <span>変更する</span>
        </a>

        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>
  import Utils from '../../../utils'
  import draggable from "vuedraggable"

  export default {
    name: 'sort-school-dialog',
    components: {
      draggable
    },
    props: ["targetYearAndMonth", "schoolWithSchedules", "scheduleCode"],
    data() {
      return {
        schoolWithSchedulesForDialog:[]
      }
    },
    methods: {
      openDialog() {
        const self = this
        self.schoolWithSchedulesForDialog.splice(0, self.schoolWithSchedulesForDialog.length)
        self.schoolWithSchedulesForDialog.push(...self.schoolWithSchedules)
        Utils.openDialog('sort-school-dialog')
      },
      closeDialog() {
        Utils.closeDialog('sort-school-dialog')
      },
      updateSort(e) {
        const self = this
        const parameter = {
          school_code_and_calculation_targets:self.schoolWithSchedulesForDialog.map(schoolAndCalculationTarget=>{
            return {
              school_code: schoolAndCalculationTarget.school.school_code,
              calculation_target: schoolAndCalculationTarget.calculation_target
            }
          })
        }

        const callback = async () => {
          try {
            await self.$http.post('/api/schedules/' + self.scheduleCode + '/_update-school-code-and-calculation-target', parameter)
            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              self.closeDialog()
              self.$emit("Refresh", e)
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
