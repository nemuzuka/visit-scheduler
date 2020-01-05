<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">タスクを{{actionTypeName}}します</h1>
    </div>

    <div class="message is-info">
      <h4 class="message-header">{{actionTypeName}}内容</h4>

      <div class="box">

        <article class="message is-danger" v-if="globalErrorMessage !== ''">
          <div class="message-body">
            {{globalErrorMessage}}
          </div>
        </article>

        <div class="field">
          <label class="label">タイトル<span class="require-input">(*)</span></label>
          <div class="control">
            <input class="input" type="text" v-model="task.title" placeholder="e.g. xxx をする" v-validate="'required|max:256'" name="title" data-vv-as="タイトル">
          </div>
          <p class="help is-danger" v-if="errors.collect('title').length > 0" >{{errors.first('title')}}</p>
        </div>

        <div class="field">
          <label class="label">内容<span class="require-input">(*)</span></label>

          <div class="tabs is-small">
            <ul>
              <li :class="{'is-active': isEdit}" @click="editContent"><a class="tab-element">編集</a></li>
              <li :class="{'is-active': isPreview}" @click="previewContent"><a class="tab-element">プレビュー</a></li>
            </ul>
          </div>

          <div class="control">
            <textarea class="textarea" v-model="task.content" placeholder="e.g. A をする" v-if="isEdit" v-validate="'required'" name="content" data-vv-as="内容"></textarea>
            <div v-if="isPreview" class="content"><div v-html="toMarkDown"></div></div>
          </div>
          <p class="help is-danger" v-if="errors.collect('content').length > 0" >{{errors.first('content')}}</p>
        </div>

        <div class="field">
          <label class="label">期限</label>
          <div class="control">
            <input class="input" type="date" v-model="task.deadline_text" v-on:change="changeDate">
          </div>
        </div>


        <div class="field">
          <p class="control has-text-right">
            <button class="button is-info" @click="saveTask">
              <span class="icon is-small">
                <font-awesome-icon icon="save" />
              </span>
              <span>{{actionTypeName}}する</span>
            </button>
          </p>
        </div>

      </div>
    </div>

    <p class="back"><a @click="moveTop"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>

  import Utils from '../../utils'
  import Uuid from 'uuid/v4'

  export default {
    name: 'task-edit',
    data() {
      return {
        task: {
          task_code: "",
          title: "",
          content: "",
          deadline: null,
          attributes: null,
          deadline_text: "",
          version: null
        },
        editMode: true,
        previewContentString: "",
        globalErrorMessage: ""
      }
    },
    async created () {
      const self = this
      self.editMode = true
      self.previewContentString = ""
      self.globalErrorMessage = ""

      try {
        const taskCode = self.$route.params.task_code
        const response = await self.$http.get('/api/tasks/' + taskCode)
        const task = self.task
        const responseTask = response.data
        task.task_code = responseTask.task_code
        task.title = responseTask.title
        task.content = responseTask.content
        task.deadline = responseTask.deadline
        task.deadline_text = Utils.dateToString(task.deadline)
        task.attributes = responseTask.attributes
        task.version = responseTask.version
      } catch(error) {
        self.$toasted.show(error.response.data.message)
        setTimeout(() => {
          self.$router.push('/')
        }, 1500)
      }
    },
    computed: {
      actionTypeName:function () {
        return this.task.task_code === "" ? "登録" : "変更"
      },
      isEdit: function () {
        return this.editMode === true
      },
      isPreview: function() {
        return this.editMode !== true
      },
      toMarkDown() {
        const self = this
        return Utils.toMarkdown(self.previewContentString)
      }
    },
    methods: {
      changeDate:function () {
        const task = this.task
        const deadlineText = task.deadline_text
        if(deadlineText === "") {
          task.deadline = null
        } else {
          task.deadline = Date.parse(deadlineText)
        }
      },
      saveTask() {

        const self = this
        const callback = async () => {
          const taskCode = self.task.task_code === "" ? Uuid() : self.task.task_code
          const url = self.task.task_code === "" ? '/api/tasks' : '/api/tasks/' + taskCode + '?version=' + self.task.version

          const parameter = {
            task_code: taskCode,
            title: self.task.title,
            content: self.task.content,
            deadline: self.task.deadline,
            attributes: self.task.attributes
          };

          if(self.task.task_code !== "") {
            parameter.is_set_deadline_to_null = self.task.deadline_text === ""
          }

          try {
            await self.$http.post(url, parameter)
            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              self.$router.push('/')
            }, 1500)
          } catch(error) {
            self.$toasted.show('入力内容にエラーがあります')
            if(error.response.data) {
              const errorData = error.response.data
              self.globalErrorMessage = errorData.message
            }
          }
        }

        self.globalErrorMessage = ""
        self.$validator.validateAll().then((result) => {
          if (result) {
            return callback()
          } else {
            self.$toasted.show('入力内容にエラーがあります')
          }
        })
      },
      moveTop() {
        const self = this
        self.$router.push('/')
      },
      previewContent() {
        const self = this
        self.previewContentString = self.task.content
        self.editMode = false
      },
      editContent() {
        const self = this
        self.editMode = true
      }
    }
  }
</script>

<style scoped>

  p.back {
    position: fixed;
    left: 15px;
    top: 40%;
    z-index: 10;
  }
  p.back a:hover {
    background: #999;
  }
  p.back a:hover {
    text-decoration: none;
  }
  p.back a {
    background: #666;
    color: #fff;
  }
  p.back a {
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
  p.back a i {
    margin-top: 8px;
  }
  a.tab-element {
    text-decoration: none !important;
  }
</style>
