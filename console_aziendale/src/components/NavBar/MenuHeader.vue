<template>
  <div>
    <nav
      class="visible lg:invisible flex fixed w-full items-center justify-between px-6 h-16 bg-primary text-white text-gray-700 z-10"
    >
      <div class="flex items-center">
        <button class="mr-2" aria-label="Open Menu" @click="drawer">
          <svg
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            class="w-8 h-8"
          >
            <path d="M4 6h16M4 12h16M4 18h16"></path>
          </svg>
        </button>
        <img src="@/assets/images/pgaziendale.png" alt="Logo" class="h-auto w-12" />
      </div>
      <div class="flex items-center" v-if="page && page.back == true">
        <button class="mr-2" aria-label="Open Menu" @click="backPage">
          <back-icon />
        </button>
        <img src="@/assets/images/pgaziendale.png" alt="Logo" class="h-auto w-12" />
      </div>
      <div class="m-auto" v-if="page">
        <span class="text-xl">{{ page.title }}</span>
      </div>
      <div class="profile-button">
        <profile-manager />
      </div>
      <transition
        enter-class="opacity-0"
        enter-active-class="ease-out transition-medium"
        enter-to-class="opacity-100"
        leave-class="opacity-100"
        leave-active-class="ease-out transition-medium"
        leave-to-class="opacity-0"
      >
        <div
          @keydown.esc="isOpen = false"
          v-show="isOpen"
          class="z-10 fixed inset-0 transition-opacity"
        >
          <div
            @click="isOpen = false"
            class="absolute inset-0 bg-black opacity-50"
            tabindex="0"
          ></div>
        </div>
      </transition>
      <aside
        class="shadow-xl transform top-0 left-0 w-64 bg-primary text-white fixed h-full overflow-auto ease-in-out transition-all duration-300 z-30"
        :class="isOpen ? 'translate-x-0' : '-translate-x-full'"
      >
        <span @click="isOpen = false" class="flex w-full items-center p-4 border-b">
          <img src="@/assets/images/pgaziendale.png" alt="Logo" class="h-auto w-12" />
        </span>
        <div>
          <profilo-header></profilo-header>
          <router-link to="/aziende" v-if="role == 'ROLE_ADMIN' && adminCompany == null">
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <factory-icon />
              </span>
              <span>Gestione Aziende</span></span
            >
          </router-link>
          <router-link
            to="/azienda"
            v-if="
              role == 'ROLE_COMPANY_ADMIN' ||
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <login-icon />
              </span>
              <span>Profilo Azienda</span></span
            >
          </router-link>
          <router-link
            to="/locations"
            v-if="
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_COMPANY_ADMIN' && actualCompany != null)
            "
          >
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <sedi-icon />
              </span>
              <span>Gestione Sedi</span></span
            >
          </router-link>
          <router-link
            to="/dipendenti"
            v-if="
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_COMPANY_ADMIN' && actualCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <users-icon />
              </span>
              <span>Gestione Dipendenti</span></span
            >
          </router-link>
          <router-link
            to="/gestionecampagne"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'"
          >
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <podio-icon />
              </span>
              <span>Gestione Campagne</span></span
            ></router-link
          >
          <router-link
            to="/stats"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'"
          >
            <span class="flex items-center p-4 hover:bg-white hover:text-primary">
              <span class="mr-2">
                <chart-icon />
              </span>
              <span>Statistiche</span></span
            >
          </router-link>
          <router-link to="/" v-on:click.native="logout">
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <logout-icon />
              </span>
              <span>Esci</span></span
            >
          </router-link>
        </div>
      </aside>
    </nav>
    <nav
      class="invisible lg:visible flex fixed w-full items-center justify-between px-6 h-16 bg-primary text-white text-gray-700 z-10"
    >
      <div class="flex items-center">
        <img src="@/assets/images/pgaziendale.png" alt="Logo" class="h-auto w-12" />
      </div>
      <div class="m-auto" v-if="page">
        <span class="text-xl">{{ page.title }}</span>
      </div>
      <div class="profile-button">
        <profile-manager />
      </div>
      <div class="flex items-center">
        <div class="hidden md:block md:justify-between md:bg-transparent"></div>
      </div>
      <aside
        class="transform top-0 left-0 w-64 bg-primary text-white fixed h-full ease-in-out transition-all duration-300 z-30"
      >
        <span class="flex w-full items-center p-4" @click="goHome()">
          <img
            src="@/assets/images/pgaziendale.png"
            alt="Logo"
            class="h-auto w-32 mx-auto"
          />
        </span>
        <div>
          <profilo-header></profilo-header>

          <router-link to="/aziende" v-if="role == 'ROLE_ADMIN' && adminCompany == null">
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <factory-icon />
              </span>
              <span>Gestione Aziende</span></span
            >
          </router-link>
          <router-link
            to="/azienda"
            v-if="
              role == 'ROLE_COMPANY_ADMIN' ||
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <login-icon />
              </span>
              <span>Profilo Azienda</span></span
            >
          </router-link>
          <router-link
            to="/locations"
            v-if="
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_COMPANY_ADMIN' && actualCompany != null) 
            "
          >
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <sedi-icon/>
              </span>
              <span>Gestione Sedi</span></span
            >
          </router-link>
          <div v-if="allLocations && allLocations.items && allLocations.items.length > 0">
          <router-link
            to="/dipendenti"
            v-if="
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_COMPANY_ADMIN' && actualCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <users-icon />
              </span>
              <span>Gestione Dipendenti</span></span
            >
          </router-link>
          </div>
          <div v-else>
            <button
            class="w-full focus:outline-none"
            @click="redirectAlertEmployees"
            v-if="
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_COMPANY_ADMIN' && actualCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
            <span class="flex items-center p-4 bg-gray"
              ><span class="mr-2">
                <users-icon />
              </span>
              <span>Gestione Dipendenti 
                <button class="pl-6 focus:outline-none"> <lock-icon/> </button>
              </span>
            </span>
          </button>
          </div>
          

          <div v-if="allLocations && allLocations.items && allLocations.items.length > 0 
                     && allEmployees && allEmployees.items && allEmployees.items.length > 0">
            <router-link
            to="/gestionecampagne"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'">
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <podio-icon />
              </span>
              <span>Gestione Campagne</span></span>
            </router-link>
          </div>
          <div v-else-if="allLocations.items.length == 0">
            <button
            class="w-full focus:outline-none"
            @click="redirectAlertCampaigns1"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'">
            <span class="flex items-center p-4 bg-gray"
              ><span class="mr-2">
                <podio-icon />
              </span>
              <span>Gestione Campagne <button class="pl-6 focus:outline-none"> <lock-icon/> </button> </span></span>
            </button>
          </div>
          <div v-else-if="allEmployees.items.length == 0">
            <button
            class="w-full focus:outline-none"
            @click="redirectAlertCampaigns2"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'">
            <span class="flex items-center p-4 bg-gray"
              ><span class="mr-2">
                <podio-icon />
              </span>
              <span>Gestione Campagne <button class="pl-6 focus:outline-none"><lock-icon/> </button> </span></span>
            </button>
          </div>

          
          <div v-if="(allCampaigns && allCampaigns.items && allCampaigns.items.length > 0) && 
              ((allEmployees && allEmployees.items && allEmployees.items.length > 0) || (allLocations
                && allLocations.items && allLocations.items.length > 0))">
            <router-link
            to="/stats"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'"
            >
            <span class="flex items-center p-4 hover:bg-white hover:text-primary">
              <span class="mr-2">
                <chart-icon />
              </span>
              <span>Statistiche</span></span
            >
            </router-link>
          </div>
          
          <div v-else-if="(allCampaigns && allCampaigns.items && allCampaigns.items.length == 0) ||
            ((allEmployees && allEmployees.items && allEmployees.items.length == 0) && (allLocations && 
            allLocations.items && allLocations.items.length == 0))"> 
            <div v-if="(allCampaigns.items.length == 0)">
              <button
              class="w-full focus:outline-none"
              @click="redirectAlertStats1"
              v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'">
              <span class="flex items-center p-4 bg-gray"
              ><span class="mr-2">
                <chart-icon />
              </span>
              <span>Statistiche<button class="pl-6 focus:outline-none"><lock-icon/> </button> </span></span>
            </button>
            </div>
            <div v-else>
              <button
              class="w-full focus:outline-none"
              @click="redirectAlertStats2"
              v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'">
              <span class="flex items-center p-4 bg-gray"
              ><span class="mr-2">
                <chart-icon />
              </span>
              <span>Statistiche<button class="pl-6 focus:outline-none"><lock-icon/> </button> </span></span>
              </button>
            </div>
            
          </div>
          
          


          <router-link to="/" v-on:click.native="logout">
            <span class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <logout-icon />
              </span>
              <span>Esci</span></span
            >
          </router-link>
        </div>
      </aside>
    </nav>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ProfiloHeader from "./ProfiloHeader.vue";
import ProfileManager from "./ProfileManager.vue";

export default {
  name: "MenuHeader",
  components: {
    ProfiloHeader,
    ProfileManager,
  },
  data: function () {
    return { isOpen: true };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
    ...mapState("navigation", ["page"]),
    ...mapState("company", ["adminCompany", "actualCompany"]),
    ...mapState("location", ["allLocations"]),
    ...mapState("campaign", ["allCampaigns", "actualCampaign"]),
    ...mapState("employee", ["allEmployees"])
  },
  methods: {
    drawer() {
      this.isOpen = !this.isOpen;
    },
    resetCompany() {
      this.resetCompanyAdmin();
    },
    redirectAlertEmployees() {
      if(confirm("Per accedere alla gestione dei dipendenti, inserire:\n--> almeno una sede\nCliccando OK verrai indirizzato alla pagina di gestione delle sedi"))
      {
        this.$router.push("/locations"); //da sistemare -> se siamo in Gestione Sedi non entra nel corpo dell'if
      }
    },
    redirectAlertCampaigns1() {
      if(confirm("Per accedere alla gestione delle campagne, inserire:\n--> almeno una sede \nCliccando OK verrai indirizzato alla pagina di gestione delle sedi"))
      {
        this.$router.push("/locations"); //da sistemare -> se siamo in Gestione Sedi non entra nel corpo dell'if
      }
    },
    redirectAlertCampaigns2() {
      if(confirm("Per accedere alla gestione delle campagne, inserire:\n--> almeno un dipendente \nCliccando OK verrai indirizzato alla pagina di gestione dei dipendenti"))
      {
        this.$router.push("/dipendenti"); //da sistemare -> se siamo in Gestione Sedi non entra nel corpo dell'if
      }
    },
    redirectAlertStats1() {
      if(confirm("Per accedere alle statistiche, inserire:\n--> associare almeno una campagna \nCliccando OK verrai indirizzato alla pagina di gestione delle campagne"))
      {
        this.$router.push("/gestionecampagne"); //da sistemare -> se siamo in Gestione Sedi non entra nel corpo dell'if
      }
    },
    redirectAlertStats2() {
      if(confirm("Per accedere alle statistiche, inserire:\n--> almeno una sede oppure un dipendente \nCliccando OK verrai indirizzato alla pagina di gestione delle sedi"))
      {
        this.$router.push("/locations"); //da sistemare -> se siamo in Gestione Sedi non entra nel corpo dell'if
      }
    },
    goHome() {
      if ( this.role == 'ROLE_ADMIN' && this.adminCompany == null) 
              {
                this.$router.push("/aziende");
              }
      else {
        this.$router.push("/azienda");
      }
    },
    ...mapActions("account", ["logout"]),
    ...mapActions("company", ["resetCompanyAdmin"]),
  },
};
</script>

<style>
.profile-button {
  position: absolute;
  right: 50px;
}
</style>
