<template>
  <div id="app" class="">
    <app-header></app-header>
    <router-view class="pt-16 lg:pt-16 lg:pl-64"></router-view>
    <snack-bar />
    <app-footer />
  </div>
</template>

<script>
import Header from "./Components/Header.vue";
import SnackBar from "./Components/SnackBar.vue";
import Footer from "./Components/Footer.vue";
export default {
  name: "App",
  components: {
    appHeader: Header,
    "snack-bar": SnackBar,
    "app-footer":Footer
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
#app {
  position: relative;
}
</style>
