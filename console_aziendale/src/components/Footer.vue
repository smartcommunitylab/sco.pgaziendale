<template>
  <footer class="absolute w-full bg-gray-900 pb-6 ">
    <div class=" bg-white text-primary px-4 lg:ml-64">
      <hr class="mb-6 border-b-1 border-white" />
      <div
        class="flex flex-wrap items-center md:justify-between justify-center"
      >
         <div class="w-full lg:w-2/12 px-4 text-center lg:text-right">
          <div class="text-sm text-primary font-semibold py-1">
            Creato da:
          </div>
        </div>
        <div class="w-full lg:w-2/12 px-4">
          <div class="text-sm text-primary font-semibold py-1">
            <img src="@/assets/images/fbk.png" alt="fbk" class="object-contain h-12 w-auto p-1 m-auto lg:ml-1" @click="openTab('https://www.fbk.eu')"    />
          </div>
        </div>
        
        <div class="w-full lg:w-4/12 px-4  text-center lg:text-right">
          <div class="text-sm text-primary font-semibold py-1">
            Nell'ambito del progetto:
          </div>
        </div>
        <div class="w-full lg:w-4/12 px-4">
          <div
            class="grid grid-cols-2 list-none lg:justify-end  justify-center mt-2"
          >
            <div class="">
             <img src="@/assets/images/airbreak.png" alt="Airbreak" class="  object-contain  h-12 w-auto max-w-32 lg:m-auto ml-auto mr-1" @click="openTab('https://www.uia-initiative.eu/en/uia-cities/ferrara-0')" />

            </div>
            <div>
             <img src="@/assets/images/eu.png" alt="Urban Innovative Actions" class="object-contain  h-12 w-auto max-w-32 lg:m-auto mr-auto ml-1" @click="openTab('https://www.uia-initiative.eu')"/>
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
  color: #1970b7;
  background-color: white;
      clear: both;
    position: relative;
    height: 100px;
    margin-top: -100px;
        box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24);

}
</style>
