<template>
  <div class="home flex flex-col lg:flex-row">
    <div class="flex flex-col m-4 lg:w-5/12 lg:ml-16 lg:mt-16">
      <div class="lg:text-4xl text-xl lg:mt-8 mt-2 text-white">Aderisci alle tue</div>
      <div class="lg:text-4xl  text-xl text-white font-bold">
        <h1>Campagne di mobilitá</h1>
      </div>
      <div class="lg:text-xl text-l lg:mt-8 mt-2 text-white">
        <b>Muoversi in modo Sostenibile </b> conviene
      </div>
      <div class="lg:text-xl text-l lg:mt-4 mt-2 text-white">
        Partecipa alle <b>Campagne di Mobilitá</b> pensate dalla <b>tua azienda</b>, e i tuoi Km sostenibili li ritrovi in busta paga.
      </div>
      <t-button class="w-5/12 mt-4 lg:mt-8 m-auto " variant="green"
        ><login-icon />
        <p class="text-center w-full" @click="login">Entra</p></t-button
      >
    </div>
    <div class="flex flex-col lg:w-5/12 lg:ml-16 lg:mt-16 bg-primary">
      <img class="object-contain" src="@/assets/images/home.png" />
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
