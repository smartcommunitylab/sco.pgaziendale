<template>
  <div >
    <div v-html="campagna.privacy" class="mx-8 my-8"></div>
  </div>
</template>

<script>
export default {
  name: "Privacy",
  created() {
        this.$store.dispatch("storePage", {title:"Privacy",back:true});

  },
  computed: {
    campagna() {
      return this.$store.getters.campagna;
    },  },
};
</script>

<style></style>
