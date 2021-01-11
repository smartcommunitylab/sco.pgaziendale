<template>
  <div
    class="py-8 px-8 text-base leading-6 space-y-4 text-gray-700 sm:text-lg sm:leading-7 lg:mx-8"
  >

    <div class="text-center text-primary">
     Il progetto e' stato sviluppato da
    </div>
    <br />
    <div class="center">
        <img src="@/assets/images/fbk.png" alt="fbk" class="h-auto w-48 m-auto" @click="openTab('https://www.fbk.eu')"    />
   
 </div>
    <br />
    <div class="text-center text-primary">
 In collaborazione con
    </div>
    <br />
    <div class="center">
              <img src="@/assets/images/comunefe.jpg" alt="Comune di Ferrara" class="h-auto w-48 m-auto" @click="openTab('https://www.comune.fe.it/')" />
    </div>
    <br />
    <div class="center">
        <img src="@/assets/images/regioneer.jpeg" alt="Regione Emilia Romagna" class="h-auto w-48 m-auto" @click="openTab('https://www.regione.emilia-romagna.it/')"/>
    </div>
    <br />
    
  </div>
</template>

<script>
export default {
  name: "Credits",
  created() {
    this.$store.dispatch("storePage", { title: "Credits", back: false });
  },
  methods: {
    openTab(url){
      window.open(url, '_blank');
    }
  },
};
</script>

<style></style>
