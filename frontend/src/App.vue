<template>
  <div id="app" class="">
    <app-header></app-header>
    <router-view class="pt-16 lg:pt-16 lg:pl-64"></router-view>
    <snack-bar />
  </div>
</template>

<script>
import Header from "./Components/Header.vue";
import SnackBar from "./Components/SnackBar.vue";
export default {
  name: "App",
  components: {
    appHeader: Header,
    "snack-bar": SnackBar,
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
