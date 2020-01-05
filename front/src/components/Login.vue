<template>
  <div>

    <div class="box has-text-centered">
      <h1 class="title">ログインするアカウントを選択してください</h1>
    </div>

    <div class="message">

      <div class="box has-text-centered">
        <client-registration-item v-for="row in elements" :row="row" :key="row.registration_id"></client-registration-item>
      </div>

    </div>

  </div>
</template>

<script>
import ClientRegistrationItem from "./oauth2/ClientRegistrationItem"

export default {
  name: 'login',
  components: {
    'client-registration-item' : ClientRegistrationItem,
  },
  data() {
    return {
      elements:[]
    }
  },
  async created () {
    const self = this
    const response = await self.$http.get('/api/open-id-connects')
    const elements = response.data.elements
    self.elements.splice(0,self.elements.length)
    self.elements.push(...elements)
  }
}
</script>
