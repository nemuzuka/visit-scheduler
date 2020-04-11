<template>
  <div class="container">
    <app-menu></app-menu>
    <div class="main-contents">
      <router-view></router-view>
    </div>
    <div class="loader-contents" id="loader">
      <div class="ball" style="float:left;margin-right:10px;"></div><div class="loader-message" style="float:left;">Now Loading...</div>
    </div>
  </div>
</template>

<script>
  import Menu from "./components/header/Menu"

  export default {
    name: 'app',
    components: {
      'app-menu' : Menu,
    }
  }
</script>

<style scoped>
  .main-contents {
    background: darkolivegreen;
    padding: 3em;
  }
</style>
