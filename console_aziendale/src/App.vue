<template>
  <div id="app">
    <app-header />
    <router-view class="lg:pl-64 pt-16 lg:pt-16" />
  </div>
</template>

<script>
import Header from "./components/Header.vue";
export default {
  name: "App",
  components: { "app-header": Header },
};
</script>

<style></style>
