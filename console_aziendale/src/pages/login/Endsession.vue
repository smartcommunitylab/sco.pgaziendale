<template>
  <svg class="animate-spin h-5 w-5 mr-3 ..." viewBox="0 0 24 24">...</svg>
</template>

<script>
import { mapActions } from "vuex";


export default {
  name: "Callback",
  methods: {
    ...mapActions("account", [ "logoutLocal"]),
  },
  data() {
    return {
      loginData: {},
      hashData: "",
    };
  },
  mounted() {
      this.logoutLocal();    
  },
};
</script>

<style></style>
