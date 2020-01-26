<template>
  <div class="column is-3">
    <div class="card card-area" style="cursor: pointer;" @click.stop="openDetailDialog">
      <div class="card-content">
        <p class="title">
          {{school.name}}
        </p>
      </div>
    </div>

  </div>
</template>

<script>
  export default {
    name: 'school-item',
    props: ["school"],
    methods: {
      openDetailDialog(e) {
        const self = this
        self.$emit("OpenDetailDialog", e, self.school)
      }
    }
  }
</script>

<style scoped>
  .card-content p.title {
    margin-top: 10px;
  }

  div.card-area {
    border: solid #363636 2px;
  }

  div.card-area:hover {
    background: gainsboro;
    border: dashed #363636 2px;
  }
</style>
