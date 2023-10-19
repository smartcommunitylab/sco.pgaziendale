<template>
  <svg class="animate-spin h-5 w-5 mr-3 ..." viewBox="0 0 24 24">Login</svg>
</template>

<script>
import { oauthService } from '@/services/oauth.services';
import { mapActions } from "vuex";


export default {
  name: "Callback",
  methods: {
    ...mapActions("account", ["loginOAuth"]),
  },
  data() {
    return {
      loginData: {},
      hashData: "",
    };
  },
  mounted() {
    oauthService.callback().then((res) => {
        this.loginOAuth({ access_token: res.access_token });
    });
    
  },
};
</script>

<style></style>
