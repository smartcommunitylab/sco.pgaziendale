<template>
  <div id="app" class="">
    <app-header></app-header>
    <router-view class="pt-32 lg:pt-32"></router-view>
  </div>
</template>

<script>
import Header from "./Components/Header.vue";

export default {
  name: "App",
  components: {
    appHeader: Header,
  },
  created() {
    this.$store.dispatch("tryAutoLogin").then((logged) => {
      if (logged) this.$router.push("campagne").catch(() => {});
    });
  },
};
</script>

<style>
body {
  @apply bg-background;
}
</style>
