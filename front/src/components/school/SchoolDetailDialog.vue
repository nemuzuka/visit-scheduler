<template>
  <div class="modal" id="school-detail-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title dialog-title"><span>{{school.name}}</span></p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">
        <div class="content">

          <article class="message is-danger" v-if="globalErrorMessage !== ''">
            <div class="message-body">
              {{globalErrorMessage}}
            </div>
          </article>

          <h1 class="title">{{school.name}}</h1>
          <hr>
          <div v-html="toMarkDown"></div>
        </div>
      </section>
      <footer class="modal-card-foot">
        <a class="button is-success" @click="moveEdit">
          <span class="icon is-small">
            <font-awesome-icon icon="edit" />
          </span>
          <span>変更</span>
        </a>

        <a class="button is-danger" @click="deleteSchool">
          <span class="icon is-small">
            <font-awesome-icon icon="trash-alt" />
          </span>
          <span>削除する</span>
        </a>

        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>
  import Utils from '../../utils'

  export default {
    name: 'school-detail-dialog',
    data() {
      return {
        school: {
          school_code: "",
          name: "",
          memo: "",
          attributes: null,
          version: null
        },
        globalErrorMessage: ""
      }
    },
    methods: {
      openDetailDialog(targetSchool) {
        const self = this
        self.globalErrorMessage = ""

        const school = self.school
        school.school_code = targetSchool.school_code
        school.name = targetSchool.name
        school.memo = targetSchool.memo
        school.attributes = targetSchool.attributes
        school.version = targetSchool.version

        Utils.openDialog('school-detail-dialog')
      },
      closeDialog() {
        Utils.closeDialog('school-detail-dialog')
      },
      moveEdit() {
        Utils.closeDialog('school-detail-dialog')
        const self = this
        const schoolCode = self.school.school_code
        self.$router.push('/edit-school/' + schoolCode)
      },
      deleteSchool(e) {
        const self = this;
        const result = window.confirm(self.school.name + 'を削除してもよろしいですか？')
        if(result == false) {
          return
        }

        const callback = async () => {
          const schoolCode = self.school.school_code
          const url = '/api/schools/' + schoolCode + '?version=' + self.school.version
          try {
            await self.$http.delete(url, {data:{dummy:'dummy'}})
            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              Utils.closeDialog('school-detail-dialog')
              self.$emit("Refresh", e)
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
        callback();
      }
    },
    computed: {
      toMarkDown() {
        const self = this
        return Utils.toMarkdown(self.school.memo)
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
</style>
