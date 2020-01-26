<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">学校を{{actionTypeName}}します</h1>
    </div>

    <div class="message is-info">
      <h4 class="message-header">{{actionTypeName}}内容</h4>

      <div class="box">

        <article class="message is-danger" v-if="globalErrorMessage !== ''">
          <div class="message-body">
            {{globalErrorMessage}}
          </div>
        </article>

        <ValidationObserver v-slot="{handleSubmit, valid}">
          <form @submit.prevent>

            <validation-provider rules="required|max:256" v-slot="{ errors }" name="学校名">
              <div class="field">
                <label class="label">学校名<span class="require-input">(*)</span></label>
                <div class="control">
                  <input class="input" type="text" v-model="school.name" placeholder="e.g. xxx 学校" name="name">
                </div>
                <p class="help is-danger">{{ errors[0] }}</p>
              </div>
            </validation-provider>

            <div class="field">
              <label class="label">内容</label>

              <div class="tabs is-small">
                <ul>
                  <li :class="{'is-active': isEdit}" @click="editContent"><a class="tab-element">編集</a></li>
                  <li :class="{'is-active': isPreview}" @click="previewContent"><a class="tab-element">プレビュー</a></li>
                </ul>
              </div>

              <div class="control">
                <textarea class="textarea" v-model="school.memo" placeholder="メモを入力します" v-if="isEdit" name="content"></textarea>
                <div v-if="isPreview" class="content"><div v-html="toMarkDown"></div></div>
              </div>
            </div>

            <div class="field">
              <p class="control has-text-right">
                <button class="button is-info" :disabled="!valid" @click="handleSubmit(saveSchool)">
              <span class="icon is-small">
                <font-awesome-icon icon="save" />
              </span>
                  <span>{{actionTypeName}}する</span>
                </button>
              </p>
            </div>

          </form>
        </ValidationObserver>
      </div>
    </div>

    <p class="back"><a @click="moveTop"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>

  import Utils from '../../utils'
  import Uuid from 'uuid/v4'

  export default {
    name: 'school-edit',
    data() {
      return {
        school: {
          school_code: "",
          name: "",
          memo: "",
          attributes: null,
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
        const schoolCode = self.$route.params.school_code
        const response = await self.$http.get('/api/schools/' + schoolCode)
        const school = self.school
        const responseSchool = response.data
        school.school_code = responseSchool.school_code
        school.name = responseSchool.name
        school.memo = responseSchool.memo
        school.attributes = responseSchool.attributes
        school.version = responseSchool.version
      } catch(error) {
        self.$toasted.show(error.response.data.message)
        setTimeout(() => {
          self.$router.push('/')
        }, 1500)
      }
    },
    computed: {
      actionTypeName:function () {
        return this.school.school_code === "" ? "登録" : "変更"
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
      saveSchool() {

        const self = this
        const callback = async () => {
          const schoolCode = self.school.school_code === "" ? Uuid() : self.school.school_code
          const url = self.school.school_code === "" ? '/api/schools' : '/api/schools/' + schoolCode + '?version=' + self.school.version

          const parameter = {
            school_code: schoolCode,
            name: self.school.name,
            memo: self.school.memo,
            attributes: self.school.attributes
          };

          try {
            await self.$http.post(url, parameter)
            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              self.$router.push('/school')
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
        callback()
      },
      moveTop() {
        const self = this
        self.$router.push('/school')
      },
      previewContent() {
        const self = this
        self.previewContentString = self.school.memo
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
  a.tab-element {
    text-decoration: none !important;
  }
</style>
