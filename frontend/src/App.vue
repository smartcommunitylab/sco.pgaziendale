<template>
  <div id="app">
    <app-header></app-header>
    <router-view class="mt-32 lg:mt-16"></router-view>
  </div>
</template>

<script>
import Header from './Shared/Header.vue'
export default {
  name: "App",
  components: {
            appHeader: Header
        },
};
</script>

<style>

</style>