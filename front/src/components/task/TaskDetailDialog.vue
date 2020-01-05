<template>
  <div class="modal" id="task-detail-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title dialog-title"><span>{{task.title}}</span></p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">
        <div class="content">

          <article class="message is-danger" v-if="globalErrorMessage !== ''">
            <div class="message-body">
              {{globalErrorMessage}}
            </div>
          </article>

          <span class="tag" :class="{'is-info': isOpen, 'is-dark': isDone}">{{task.status}}</span>
          <span class="deadline">{{task.deadline_text}}</span>

          <h1 class="title">{{task.title}}</h1>
          <hr>
          <div v-html="toMarkDown"></div>
          <hr>
        </div>
      </section>
      <footer class="modal-card-foot">
        <a class="button is-success" @click="moveEdit">
          <span class="icon is-small">
            <font-awesome-icon icon="edit" />
          </span>
          <span>変更</span>
        </a>

        <a class="button is-info" @click="done" v-if="task.status === 'OPEN'">
          <span class="icon is-small">
            <font-awesome-icon icon="check-circle" />
          </span>
          <span>完了する</span>
        </a>

        <a class="button is-danger is-outlined" @click="reopen" v-if="task.status === 'DONE'">
          <span class="icon is-small">
            <font-awesome-icon icon="undo" />
          </span>
          <span>再OPEN</span>
        </a>

        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>
  import Utils from '../../utils'
  export default {
    name: 'task-detail-dialog',
    data() {
      return {
        task: {
          task_code: "",
          title: "",
          content: "",
          status: "",
          deadline: null,
          attributes: null,
          deadline_text: "",
          version: null
        },
        globalErrorMessage: ""
      }
    },
    methods: {
      openDetailDialog(targetTask) {
        const self = this
        self.globalErrorMessage = ""

        const task = self.task
        task.task_code = targetTask.task_code
        task.title = targetTask.title
        task.content = targetTask.content
        task.status = targetTask.status
        task.deadline = targetTask.deadline
        task.deadline_text = Utils.dateToString(task.deadline)
        task.attributes = targetTask.attributes
        task.version = targetTask.version

        Utils.openDialog('task-detail-dialog')
      },
      closeDialog() {
        Utils.closeDialog('task-detail-dialog')
      },
      moveEdit() {
        Utils.closeDialog('task-detail-dialog')
        const self = this
        const taskCode = self.task.task_code
        self.$router.push('/edit-task/' + taskCode)
      },
      async done(e) {
        const self = this
        self.globalErrorMessage = ""
        try{
          const taskCode = self.task.task_code
          const version = self.task.version
          const url = '/api/tasks/' + taskCode + '/_done?version=' + version
          await self.$http.post(url, {})
          self.$toasted.show('処理が終了しました')

          setTimeout(() => {
            Utils.closeDialog('task-detail-dialog')
            self.$emit("Refresh", e)
          }, 1500)

        } catch (error) {
          self.$toasted.show('入力内容にエラーがあります')
          const errorData = error.response.data
          self.globalErrorMessage = errorData.message
        }
      },
      async reopen(e) {
        const self = this
        self.globalErrorMessage = ""
        try {
          const taskCode = self.task.task_code
          const version = self.task.version
          const url = '/api/tasks/' + taskCode + '/_reopen?version=' + version
          await self.$http.post(url, {})
          self.$toasted.show('処理が終了しました')
          setTimeout(() => {
            Utils.closeDialog('task-detail-dialog')
            self.$emit("Refresh", e)
          }, 1500)

        } catch (error) {
          self.$toasted.show('入力内容にエラーがあります')
          const errorData = error.response.data
          self.globalErrorMessage = errorData.message
        }
      }
    },
    computed: {
      isOpen() {
        const self = this
        const task = self.task
        return task.status === 'OPEN'
      },
      isDone() {
        const self = this
        const task = self.task
        return task.status === 'DONE'
      },
      toMarkDown() {
        const self = this
        return Utils.toMarkdown(self.task.content)
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
  .content h1.title {
    margin-top: 10px;
  }
  .deadline {
    margin-left: 5px;
  }
</style>
