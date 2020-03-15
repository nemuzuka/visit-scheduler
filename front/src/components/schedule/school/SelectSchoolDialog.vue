<template>
  <div class="modal" id="select-schedule-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title dialog-title"><span>スケジュールを設定する学校を選択します</span></p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">
        <div class="content">

          <table class="table is-bordered">
            <thead>
            <tr>
              <th class="label-target">対象</th>
              <th>学校名</th>
            </tr>
            </thead>
            <tbody>
              <tr v-for="schoolWithSchedule in schoolWithSchedules" :key="schoolWithSchedule.school.school_code" style="cursor: pointer" @click="moveSchoolSchedule(schoolWithSchedule.school)">
                <td class="has-text-centered">{{toCalculationTargetLabel(schoolWithSchedule.calculation_target)}}</td>
                <td>{{schoolWithSchedule.school.name}}</td>
              </tr>
            </tbody>
          </table>

        </div>
      </section>
      <footer class="modal-card-foot">
        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>
  import Utils from '../../../utils'
  export default {
    name: 'select-school-dialog',
    props: ["targetYearAndMonth", "schoolWithSchedules"],
    methods: {
      openDialog() {
        Utils.openDialog('select-schedule-dialog')
      },
      closeDialog() {
        Utils.closeDialog('select-schedule-dialog')
      },
      toCalculationTargetLabel(calculationTarget) {
        return calculationTarget ? "○" : "×"
      },
      moveSchoolSchedule(school) {
        const self = this
        self.closeDialog()
        self.$router.push("/school-schedule/" + self.targetYearAndMonth + "/school/" + school.school_code)
      }
    }
  }
</script>
