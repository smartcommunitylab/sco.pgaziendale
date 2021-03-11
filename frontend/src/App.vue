<template>
  <div id="app" class="">
    <menu-header></menu-header>
    <router-view class="pt-16 lg:pt-16 lg:pl-64 min-h-screen padding-bottom"></router-view>
    <snack-bar />
    <app-footer />
  </div>
</template>

<script>
import MenuHeader from "./Components/MenuHeader.vue";
import SnackBar from "./Components/SnackBar.vue";
import Footer from "./Components/Footer.vue";
export default {
  name: "App",
  components: {
    menuHeader: MenuHeader,
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
