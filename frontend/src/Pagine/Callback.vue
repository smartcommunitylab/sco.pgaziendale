<template>
  <svg class="animate-spin h-5 w-5 mr-3 ..." viewBox="0 0 24 24">Login</svg>
</template>

<script>
import axios from "axios";
import { BASE_URL, TOKEN_API } from '../variables.js'

export default {
  name: "Callback",
  methods: {},
  data() {
    return {
      loginData: {},
      hashData: "",
    };
  },
  beforeMount() {
    var mobileToken = this.$route.query.mobileToken;
    console.log(mobileToken);
    this.hasData = new URLSearchParams(
      window.location.hash.substr(1) // skip the first char (#)
    );

    //call api for getting token
    //and store the id_token
    var access_token = this.hasData.get("access_token");

    if (mobileToken)
      access_token = mobileToken
    const headers = {
      Accept: "*/*",
      Authorization: access_token,
    };
    axios
      .get(
        BASE_URL + TOKEN_API,

        { headers }
      )
      .then((res) => {
        this.$store.dispatch("loginWithToken", { idToken: res.data.id_token }).then(() => {
          this.$router.push("campagne").catch(() => {});
        });
      })
      .catch((error) => {
        console.log(error);
        this.$router.push("/");
      });
  },
};
</script>

<style></style>
