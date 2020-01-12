<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">ユーザを{{actionTypeName}}します</h1>
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
            <validation-provider rules="required|max:256" v-slot="{ errors }" name="氏名">
              <div class="field">
                <label class="label">氏名<span class="require-input">(*)</span></label>
                <div class="control">
                  <input class="input" type="text" v-model="user.user_name" placeholder="e.g. 山田 太郎" name="userName">
                </div>
                <p class="help is-danger">{{ errors[0] }}</p>
              </div>
            </validation-provider>

            <div class="field">
              <p class="control has-text-right">
                <button class="button is-info" :disabled="!valid" @click="handleSubmit(saveUser)">
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

  import Uuid from 'uuid/v4'

  export default {
    name: 'user-settings',
    data() {
      return {
        user: {
          user_code: "",
          user_name: ""
        },
        globalErrorMessage: ""
      }
    },
    async created () {
      const self = this
      self.globalErrorMessage = ""
      const response = await self.$http.get('/api/me')
      self.user.user_code = response.data.user_code
      self.user.user_name = response.data.user_name
    },
    computed: {
      actionTypeName:function () {
        return this.user.user_code === "" ? "登録" : "変更"
      }
    },
    methods: {
      saveUser() {
        const self = this

        const callback = async () => {
          const userCode = self.user.user_code === "" ? Uuid() : self.user.user_code
          const url = self.user.user_code === "" ? '/api/users' : '/api/users/' + userCode

          try {
            await self.$http.post(url, {
              user_code: userCode,
              user_name: self.user.user_name
            })
            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              self.$router.push('/')
            }, 1500)
          } catch (error) {
            self.$toasted.show('入力内容にエラーがあります')
            if(error.response.data) {
              const errorData = error.response.data
              self.globalErrorMessage = errorData.message
            }
          }
        }

        self.globalErrorMessage = ""
        callback();
      },
      moveTop() {
        const self = this
        self.$router.push('/')
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
</style>
