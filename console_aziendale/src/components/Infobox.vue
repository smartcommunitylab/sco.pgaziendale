<template>
  <div class="bg-red-300 lg:w-2/6  pt-32  -mt-16  ">
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
      <div class="">
        <div class="flex flex-col justify-center text-center pt-16">
          <div class="flex flex-row ml-auto pr-8">
            <span class="mr-2">
              <svg
                class=" h-12 w-6  text-grey-dark-m-r-4"
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
              >
                <path
                  d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                />
              </svg>
            </span>
            <span class="mr-2">
              <svg
                class=" h-12 w-6  text-grey-dark-m-r-4"
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
              >
                <path
                  d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                />
              </svg>
            </span>
          </div>
          <img
            :src="showObject.logo"
            class="object-contain w-1/2 m-auto pb-2"
          />
          <h1 class="text-2xl pt-2">{{ showObject.name }}</h1>
          <div class="flex flex-col pt-4">
            <span class="flex items-center justify-center"
              ><span class="mr-2">
                <svg
                  class=" h-12 w-6  text-grey-dark-m-r-4"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                >
                  <path
                    d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                  />
                </svg> </span
              >{{ showObject.address }}</span
            >
            <span class="flex items-center justify-center"
              ><span class="mr-2">
                <svg
                  class=" h-12 w-6  text-grey-dark-m-r-4"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                >
                  <path
                    d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                  />
                </svg> </span
              >{{ showObject.phone }}</span
            >
            <span class="flex items-center justify-center"
              ><span class="mr-2">
                <svg
                  class=" h-12 w-6  text-grey-dark-m-r-4"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                >
                  <path
                    d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                  />
                </svg> </span
              ><a :href="showObject.web">{{ showObject.web }}</a></span
            >

            <span class="flex items-center justify-center"
              ><span class="mr-2">
                <svg
                  class=" h-12 w-6  text-grey-dark-m-r-4"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                >
                  <path
                    d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                  />
                </svg> </span
              ><a :href="'mailto:' + showObject.email">{{
                showObject.email
              }}</a></span
            >
          </div>
        </div>
      </div>
    </div>

    <!-- dipendente-->

    <div
      class="bg-white h-screen shadow-md -mt-16"
      v-else-if="currentView == 4"
    >
      <div class="">
        <div class="flex flex-col justify-center text-center ">
          <div class="flex flex-row ml-auto pr-8">
            <span class="mr-2">
              <svg
                class=" h-12 w-6  text-grey-dark-m-r-4"
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
              >
                <path
                  d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                />
              </svg>
            </span>
            <span class="mr-2">
              <svg
                class=" h-12 w-6  text-grey-dark-m-r-4"
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
              >
                <path
                  d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                />
              </svg>
            </span>
          </div>
          <img
            src="@/assets/images/user-profile.png"
            class="object-contain w-1/2 m-auto pb-2"
          />
          <h1 class="text-2xl pt-2">
            {{ showObject.first_name + " " + showObject.last_name }}
          </h1>
          <div class="text-sm">
            <p>{{ showObject.matricola }}</p>
            <p>{{ showObject.email }}</p>
          </div>

          <div class="pt-4">
            <p>partecipa alle seguenti campagne:</p>
            <ul>
              <li
                class="text-primary text-lg"
                :key="campaign.id"
                v-for="campaign in showObject.campagne"
              >
                {{ campaign.nome }}
              </li>
            </ul>
          </div>

          <div class="flex flex-row pt-4 max-h-36 ">
            <div class="w-1/3 flex flex-col py-2  bg-primary text-white">
              <h3 class="text-6xl">128</h3>
              <span class="text-sm mt-auto pb-2">Viaggi effettuati</span>
            </div>
            <div class="w-1/3 flex flex-col py-2  bg-secondary text-white">
              <h3 class="text-6xl">1242</h3>
              <span class="text-sm mt-auto pb-2">Km percorsi</span>
            </div>
            <div class="w-1/3 flex flex-col py-2  bg-primary text-white">
              <img src="@/assets/images/cycling.svg" class=" h-20 m-auto " />
              <span class="text-sm mt-auto pb-2">mezzo preferito</span>
            </div>
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

    showEmployeeDetails(employee) {
      if (this.showObject != employee) {
        this.showObject = employee;
        this.currentView = 4;
      } else {
        this.currentView = 0;
        this.showObject = {};
      }
    },
  },
};
</script>

<style></style>
