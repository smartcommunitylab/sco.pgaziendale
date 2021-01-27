<template>
  <footer class="absolute w-full bg-gray-900 pb-6 ">
    <div class=" px-4 lg:ml-64">
      <hr class="mb-6 border-b-1 border-white" />
      <div
        class="flex flex-wrap items-center md:justify-between justify-center"
      >
         <div class="w-full lg:w-2/12 px-4 text-center">
          <div class="text-sm text-white font-semibold py-1">
            Creato da:
          </div>
        </div>
        <div class="w-full lg:w-2/12 px-4">
          <div class="text-sm text-white font-semibold py-1">
            <img src="@/assets/images/fbk.png" alt="fbk" class="object-contain h-12 w-auto p-1 m-auto" @click="openTab('https://www.fbk.eu')"    />
          </div>
        </div>
        
        <div class="w-full lg:w-4/12 px-4  text-center lg:text-right">
          <div class="text-sm text-white font-semibold py-1">
            In collaborazione con:
          </div>
        </div>
        <div class="w-full lg:w-4/12 px-4">
          <div
            class="flex flex-wrap list-none lg:justify-end  justify-center"
          >
            <div>
             <img src="@/assets/images/comunefe.jpg" alt="Comune di Ferrara" class="flex h-12 w-auto m-1" @click="openTab('https://www.comune.fe.it/')" />

            </div>
            <div>
             <img src="@/assets/images/regioneer.jpeg" alt="Regione Emilia Romagna" class="flex max-w-xs h-12 w-auto m-1" @click="openTab('https://www.regione.emilia-romagna.it/')"/>
            </div>

          </div>
        </div>
      </div>
    </div>
  </footer>
</template>
<script>
export default {
  data() {
    return {
      date: new Date().getFullYear()
    }
  },
    methods: {
    openTab(url){
      window.open(url, '_blank');
    }
  },
}
</script>
<style>
footer{
  /* z-index: 999; */
  background-color: #1970b7;
}
</style>
