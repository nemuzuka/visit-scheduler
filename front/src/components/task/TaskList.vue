<template>
  <div>
    <div v-if="tasks.length > 0">
      <div class="columns is-multiline">
        <task-item v-for="task in tasks" :task="task" :key="task.task_code" @OpenDetailDialog="openDetailDialog"></task-item>
      </div>
    </div>

    <div v-if="message !== ''">
      <article class="message is-danger">
        <div class="message-body">{{message}}</div>
      </article>
    </div>

    <task-detail-dialog ref="taskDetailDialog" @Refresh="refresh"></task-detail-dialog>

  </div>
</template>

<script>
  import TaskItem from './TaskItem'
  import TaskDetailDialog from "./TaskDetailDialog"
  export default {
    components: {
      TaskDetailDialog,
      TaskItem
    },
    name: 'task-list',
    props:["tasks", "message"],
    methods: {
      openDetailDialog(e, task) {
        const self = this
        self.$refs.taskDetailDialog.openDetailDialog(task)
      },
      refresh(e) {
        const self = this
        self.$emit("Refresh", e)
      }
    }
  }
</script>
