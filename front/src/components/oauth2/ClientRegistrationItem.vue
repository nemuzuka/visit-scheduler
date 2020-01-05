<template>
  <div>
    <img :src="image_src" @click.stop="moveAuthorizationUrl" style="cursor: pointer;">
  </div>
</template>

<script>
  import baseURL from '../../url'
  export default {
    name: 'client-registration-item',
    props: ["row"],
    data() {
      return {
        image_src:""
      }
    },
    created() {
      const self = this
      if(self.row.registration_id === 'google') {
        self.image_src = require("./../../assets/btn_google_signin_dark_normal_web@2x.png")
      }
    },
    methods: {
      moveAuthorizationUrl() {
        const self = this
        window.location.href = baseURL + self.row.authorization_url
      }
    }
  }
</script>
