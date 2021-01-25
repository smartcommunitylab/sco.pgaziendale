<template>
  <div class="home flex flex-col lg:flex-row">
    <div class="flex flex-col ml-8 mt-8 lg:w-5/12 lg:ml-16 lg:mt-16">
      <div class="text-4xl mt-8 text-white">Aderisci alle tue</div>
      <div class="text-4xl  text-white font-bold">
        <h1>Campagne di mobilitá</h1>
      </div>
      <div class="text-xl mt-8 text-white">
        <b>Muoversi in modo Sostenibile </b> conviene
      </div>
      <div class="text-xl mt-4 text-white">
        Partecipa alle <b>Campagne di Mobilitá</b> pensate dalla <b>tua azienda</b>, e i tuoi Km sostenibili li ritrovi in busta paga.
      </div>
      <t-button class="w-5/12 mt-8 m-auto " variant="green"
        ><login-icon />
        <p class="text-center w-full" @click="login">Entra</p></t-button
      >
    </div>
    <div class="flex flex-col lg:w-5/12 lg:ml-16 lg:mt-16 bg-primary">
      <img src="@/assets/images/home.svg" />
    </div>
  </div>
</template>

<script>

export default {
  name: "Homepage",
    created() {
        this.$store.dispatch("storePage", {title:"",back:false});
  },
  methods: {
    login() {
      var authUrl = process.env.VUE_APP_AUTH_URL;
      var tokenId = process.env.VUE_APP_AUTH_TOKEN_ID;
      var redirectUri = process.env.VUE_APP_REDIRECT_URI;
      return window.open(
        authUrl +
          "response_type=token&client_id=" +
          tokenId +
          "&redirect_uri=" +
          redirectUri,
        "_self"
      );
    },
  },
};
</script>

<style scoped>

.home {
  background-color: var(--blue);
  /* height: 100vh; */
   min-height: 100vh; 
}
</style>
