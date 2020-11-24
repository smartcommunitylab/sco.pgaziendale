<template>
  <div class="home flex flex-col lg:flex-row">
    <div class="flex flex-col ml-8 mt-8 lg:w-6/12 lg:ml-16 lg:mt-16">
      <div class="text-4xl mt-8 text-white">Aderisci alle tue</div>
      <div class="text-4xl  text-white font-bold">
        <h1>Campagne di mobilitá</h1>
      </div>
      <div class="text-xl mt-8 text-white">
        <b>Partecipa </b> campagne di mobilitá proposte dalla tua azienda
      </div>
      <div class="text-xl  text-white">
        <b>Accedi </b> le stesse credenziali utilizzate nell'APP Play&go
      </div>
      <t-button class="w-2/12 mt-8 m-auto" variant="green"><p class="text-center w-full" @click="login"> Entra </p></t-button>
    </div>
    <div class="flex flex-col lg:w-4/12 lg:ml-16 lg:mt-16">
      <img src="@/assets/images/bike.svg" />
    </div>
  </div>
</template>

<script>
export default {
  name: "Homepage",
  methods: {
    login() {
      return window.open("https://tn.smartcommunitylab.it/aac/eauth/authorize?response_type=token&client_id=ec03a596-e41e-49cc-808c-62f39e01de0b&redirect_uri=http://localhost:8080/callback","_self")
    }
  },
};
</script>

<style scoped>
.home {
  background-color: var(--blue);
  height: 100%;
  min-height: 90vh;
}
</style>
