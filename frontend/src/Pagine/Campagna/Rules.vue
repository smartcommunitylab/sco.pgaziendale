<template>
  <div >
    <div v-html="campagna.rules"></div>
  </div>
</template>

<script>
export default {
  name: "Rules",
  created() {
        this.$store.dispatch("storePage", {title:"Regolamento",back:true});

  },
  computed: {
    campagna() {
      return this.$store.getters.campagna;
    },  },
};
</script>

<style></style>
