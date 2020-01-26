<template>
  <div>
    <div class="box has-text-centered">
      <h1 class="title">タスク一覧</h1>
    </div>

    <p class="edit-user"><a @click="moveUserSettings"><font-awesome-icon icon="user-cog" /></a></p>
    <p class="school"><a @click="moveSchool"><font-awesome-icon icon="school" /></a></p>

  </div>
</template>

<script>

import Utils from '../utils'

const SORT_CONDITION_KEY = "SORT_CONDITION"

export default {
  name: 'top',
  data() {
    return {
      tasks: [],
      taskMessage: "",
      allTasks: [],
      sortConditions:{
        status: "OPEN_ONLY",
        deadline: "NONE"
      }
    }
  },
  created () {
    const self = this
    const condition = Utils.getLocalStorage(SORT_CONDITION_KEY)
    if(condition !== null) {
      // localStrage より取得して設定
      self.sortConditions.status = condition.status
      self.sortConditions.deadline = condition.deadline
    }
    self.refresh()
  },
  methods: {
    async refresh() {
      const self = this
      self.taskMessage = ""

      const tasks = []
      tasks.splice(0,tasks.length)

      self.allTasks = []
      const sortedTasks = self.allTasks
        .filter(self.filterTask)
        .sort(self.sortTask)
      tasks.push(...sortedTasks)

      if(sortedTasks.length <= 0) {
        self.taskMessage = "表示するタスクがありません"
      }
    },
    filterTask(task) {
      const self = this

      // status の絞り込み
      const statusCondition = self.sortConditions.status
      if(statusCondition === "OPEN_ONLY" && task.status === "DONE") {
        return false
      }
      if(statusCondition === "DONE_ONLY" && task.status === "OPEN") {
        return false
      }

      // deadline の絞り込み
      const deadlineCondition = self.sortConditions.deadline
      if(deadlineCondition === "SET_ONLY" && task.deadline === null) {
        return false
      }
      if(deadlineCondition === "NULL_ONLY" && task.deadline !== null) {
        return false
      }

      return true
    },
    sortTask(taskA, taskB) {
      // ステータス(OPEN, DONE の順)
      const statusA = taskA.status
      const statusB = taskB.status
      if(statusA !== statusB) {
        return statusA > statusB ? -1 : 1 // DONE より OPEN が先
      }

      // 期限 asc
      const deadlineA = taskA.deadline === null ? Number.MAX_VALUE : taskA.deadline
      const deadlineB = taskB.deadline === null ? Number.MAX_VALUE : taskB.deadline
      const deadlineResult = deadlineA - deadlineB
      if(deadlineResult !== 0) {
        return deadlineResult
      }

      // task_code
      const taskCodeA = taskA.task_code
      const taskCodeB = taskB.task_code
      if(taskCodeA !== taskCodeB) {
        return taskCodeA < taskCodeB ? -1 : 1
      }
      return 0
    },
    openFilterConditionDialog(){
      const self = this
      self.$refs.filterConditionDialog.openDialog()
    },
    setCondition(e, condition) {

      const self = this;
      self.sortConditions.status = condition.status
      self.sortConditions.deadline = condition.deadline

      // localStrage に設定
      Utils.setLocalStorage(SORT_CONDITION_KEY, condition)

      self.refresh()
    },
    moveUserSettings() {
      const self = this
      self.$router.push('/user-settings')
    },
    moveSchool() {
      const self = this
      self.$router.push('/school')
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
