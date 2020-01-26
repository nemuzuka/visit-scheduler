<template>
  <div>
    <div class="box has-text-centered">
      <h1 class="title">登録済みの学校一覧</h1>
    </div>

    <school-list :schools="schools" :message="schoolMessage" @Refresh="refresh"></school-list>

    <p class="create-school"><a @click="moveCreateSchool"><font-awesome-icon icon="plus" /></a></p>

    <p class="back"><a @click="moveTop"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>

import SchoolList from './SchoolList'

export default {
  components: {
    SchoolList
  },
  name: 'school-top',
  data() {
    return {
      schools: [],
      schoolMessage: ""
    }
  },
  created () {
    const self = this
    self.refresh()
  },
  methods: {
    moveCreateSchool() {
      const self = this
      self.$router.push('/edit-school/_new')
    },
    async refresh() {
      const self = this
      self.schoolMessage = ""

      const response = await self.$http.get('/api/schools')
      self.schools.splice(0, self.schools.length)

      const schools = response.data.elements
      self.schools.push(...schools)

      if(schools.length <= 0) {
        self.schoolMessage = "表示する学校がありません"
      }
    },
    moveTop() {
      const self = this
      self.$router.push('/')
    }
  }
}
</script>

<style scoped>
  p.create-school {
    position: fixed;
    right: 15px;
    top: 35%;
    z-index: 10;
  }

  p.create-school a:hover {
    background: #999;
    text-decoration: none;
  }
  p.create-school a {
    background: #666;
    color: #fff;
  }
  p.create-school a {
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
  p.create-school a i {
    margin-top: 8px;
  }
</style>
