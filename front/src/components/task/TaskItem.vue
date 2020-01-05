<template>
  <div class="column is-3">
    <div class="card card-area" style="cursor: pointer;" @click.stop="openDetailDialog">
      <div class="card-content">
        <span class="tag" :class="{'is-info': isOpen, 'is-dark': isDone}">{{task.status}}</span>
        <p class="title">
          {{task.title}}
        </p>
        <div class="subtitle">
          <p>{{viewDeadline}}</p>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
  import Utils from '../../utils'
  export default {
    name: 'task-item',
    props: ["task"],
    methods: {
      openDetailDialog(e) {
        const self = this
        self.$emit("OpenDetailDialog", e, self.task)
      }
    },
    computed:{
      viewDeadline() {
        const self = this
        const deadline = self.task.deadline
        return Utils.dateToString(deadline)
      },
      isOpen() {
        const self = this
        const task = self.task
        return task.status === 'OPEN'
      },
      isDone() {
        const self = this
        const task = self.task
        return task.status === 'DONE'
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
