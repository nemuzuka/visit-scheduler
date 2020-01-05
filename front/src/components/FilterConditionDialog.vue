<template>
  <div class="modal" id="filter-condition-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title dialog-title"><span>絞り込み条件変更</span></p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">
        <div class="content">

          <div class="field">
            <label class="label">ステータスの絞り込み条件</label>
            <div class="control">
              <label class="radio">
                <input type="radio" name="status" value="OPEN_ONLY" v-model="status">
                OPEN のみ
              </label>
              <label class="radio">
                <input type="radio" name="status" value="DONE_ONLY" v-model="status">
                CLOSE のみ
              </label>
              <label class="radio">
                <input type="radio" name="status" value="NONE" v-model="status">
                絞り込まない
              </label>

            </div>
          </div>


          <div class="field">
            <label class="label">期限の絞り込み条件</label>
            <div class="control">
              <label class="radio">
                <input type="radio" name="deadline" value="SET_ONLY" v-model="deadline">
                期限ありのみ
              </label>
              <label class="radio">
                <input type="radio" name="deadline" value="NULL_ONLY" v-model="deadline">
                期限なしのみ
              </label>
              <label class="radio">
                <input type="radio" name="deadline" value="NONE" v-model="deadline">
                絞り込まない
              </label>

            </div>
          </div>

        </div>
      </section>
      <footer class="modal-card-foot">
        <a class="button is-success" @click="setCondition">
          <span class="icon is-small">
            <font-awesome-icon icon="check" />
          </span>
          <span>絞り込み条件変更</span>
        </a>

        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>
  import Utils from '../utils'
  export default {
    name: 'filter-condition-dialog',
    props: ["sortConditions"],
    data() {
      return {
        status: "",
        deadline: ""
      }
    },
    methods: {
      openDialog() {
        const self = this
        self.status = self.sortConditions.status
        self.deadline = self.sortConditions.deadline
        Utils.openDialog('filter-condition-dialog')
      },
      closeDialog() {
        Utils.closeDialog('filter-condition-dialog')
      },
      setCondition(e) {
        const self = this
        const condition = {
          "status": self.status,
          "deadline": self.deadline
        }
        self.$emit("SetCondition", e, condition)
        self.closeDialog()
      }
    }
  }
</script>

<style scoped>
  .dialog-title {
    overflow: hidden;
    width: 80%;
  }
  .dialog-title span {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
</style>
