<template>
  <div>
    <div v-if="schools.length > 0">
      <div class="columns is-multiline">
        <school-item v-for="school in schools" :school="school" :key="school.school_code" @OpenDetailDialog="openDetailDialog"></school-item>
      </div>
    </div>

    <div v-if="message !== ''">
      <article class="message is-danger">
        <div class="message-body">{{message}}</div>
      </article>
    </div>

    <school-detail-dialog ref="schoolDetailDialog" @Refresh="refresh"></school-detail-dialog>

  </div>
</template>

<script>
  import SchoolItem from './SchoolItem'
  import SchoolDetailDialog from "./SchoolDetailDialog"
  export default {
    components: {
      SchoolDetailDialog,
      SchoolItem
    },
    name: 'school-list',
    props:["schools", "message"],
    methods: {
      openDetailDialog(e, school) {
        const self = this
        self.$refs.schoolDetailDialog.openDetailDialog(school)
      },
      refresh(e) {
        const self = this
        self.$emit("Refresh", e)
      }
    }
  }
</script>
