<template>
  <div class="bg-red-300 lg:w-2/6">
    <!-- 
    -niente
    -Azienda
    -Sede
    -Campagna
    -Dipendente
  -->

    <!-- niente-->

    <div class="bg-primary h-screen -mt-16" v-if="currentView == 0">
      <div class="pt-16">
        <img src="@/assets/images/logo.png" class="w-1/2 m-auto" />
      </div>
    </div>
    <!-- azienda-->

    <div
      class="bg-white h-screen shadow-md -mt-16"
      v-else-if="currentView == 1"
    >
      <div class="pt-16">
        <div class="flex flex-col justify-center text-center pt-16">
          <img
            :src="showObject.logo"
            class="object-contain w-1/2 m-auto pb-2"
          />
          <h1 class="text-2xl">{{ showObject.name }}</h1>
          <div class="flex flex-col">
            <span>{{ showObject.address }}</span>
            <span>{{ showObject.phone }}</span>

            <span
              ><a :href="showObject.web">{{ showObject.web }}</a></span
            >

            <span
              ><a :href="'mailto:' + showObject.email">{{
                showObject.email
              }}</a></span
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "InfoBox",
  data: function() {
    return {
      currentView: 0,
      showObject: {},
    };
  },
  methods: {
    showCompanyDetails(company) {
      console.log(company);
      if (this.showObject != company) {
        this.showObject = company;
        this.currentView = 1;
      } else {
        this.currentView = 0;
        this.showObject = {};
      }
    },
  },
};
</script>

<style></style>
