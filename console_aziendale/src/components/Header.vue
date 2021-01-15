<template>
  <div>
    <nav
      class="visible lg:invisible flex fixed w-full items-center justify-between px-6 h-16 bg-primary text-white text-gray-700  z-10 "
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
           <div class="flex items-center" v-if="page && page.back==true">
        <button class="mr-2" aria-label="Open Menu" @click="backPage">
          <!-- <svg
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            class="w-8 h-8"
          >
            <path d="M15 8.25H5.87l4.19-4.19L9 3 3 9l6 6 1.06-1.06-4.19-4.19H15v-1.5z"></path>
          </svg> -->
          <back-icon/>
        </button>
        <img src="@/assets/images/pgaziendale.png" alt="Logo" class="h-auto w-12" />
      </div>
           <div class="flex items-center" v-if="page">
              <span class="text-xl">{{page.title}}</span>
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
        class="shadow-xl  transform top-0 left-0 w-64 bg-primary text-white fixed h-full overflow-auto ease-in-out transition-all duration-300 z-30"
        :class="isOpen ? 'translate-x-0' : '-translate-x-full'"
      >
        <span
          @click="isOpen = false"
          class="flex w-full items-center p-4 border-b"
        >
        <img src="@/assets/images/pgaziendale.png" alt="Logo" class="h-auto w-12" />
        </span>
        <div>
          <router-link to="/aziende" v-if="role=='ROLE_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <factory-icon />
              </span>
              <span>Gestione Aziende</span></span
            >
          </router-link>
          <router-link to="/azienda" v-if="role=='ROLE_COMPANY_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <login-icon />
              </span>
              <span>Profilo Azienda</span></span
            >
          </router-link>
          <router-link to="/sedi" v-if="role=='ROLE_ADMIN'|| role=='ROLE_COMPANY_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <sedi-icon />
              </span>
              <span>Gestione Sedi</span></span
            >
          </router-link>
          <router-link to="/dipendenti">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <users-icon />
              </span>
              <span>Gestione Dipendenti</span></span
            >
          </router-link>
          <router-link to="/gestionecampagne" v-if="role=='ROLE_COMPANY_ADMIN' || role=='ROLE_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <podio-icon />
              </span>
              <span>Gestione Campagne</span></span
            ></router-link
          >
          <router-link to="/stats" v-if="role!='ROLE_COMPANY_ADMIN' || role=='ROLE_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
            >
              <span class="mr-2">
                <chart-icon />
              </span>
              <span>Statistiche</span></span
            >
          </router-link>
          <router-link to="/" v-on:click.native="logout">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
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
      class="invisible lg:visible flex fixed w-full items-center justify-between px-6 h-16 bg-primary text-white text-gray-700  z-10 "
    >
      <div class="flex items-center">
        <img src="@/assets/images/pgaziendale.png" alt="Logo" class="h-auto w-12" />
      </div>
      <div class="flex items-center" v-if="page">
        <span class="text-xl">{{page.title}}</span>
      </div>
      <div class="flex items-center">
        <div
          class="hidden md:block md:flex md:justify-between md:bg-transparent"
        ></div>
      </div>
      <aside
        class="transform top-0 left-0 w-64 bg-primary text-white fixed h-full  ease-in-out transition-all duration-300 z-30"
      >
        <span class="flex w-full items-center p-4 ">
        <img src="@/assets/images/pgaziendale.png" alt="Logo"             class="h-auto w-32 mx-auto"
 />
        </span>
        <div>
        <router-link to="/aziende" v-if="role=='ROLE_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <factory-icon />
              </span>
              <span>Gestione Aziende</span></span
            >
          </router-link>
          <router-link to="/azienda" v-if="role=='ROLE_COMPANY_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <login-icon />
              </span>
              <span>Profilo Azienda</span></span
            >
          </router-link>
          <router-link to="/sedi" v-if="role=='ROLE_ADMIN'|| role=='ROLE_COMPANY_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <sedi-icon />
              </span>
              <span>Gestione Sedi</span></span
            >
          </router-link>
          <router-link to="/dipendenti">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <users-icon />
              </span>
              <span>Gestione Dipendenti</span></span
            >
          </router-link>
          <router-link to="/gestionecampagne" v-if="role=='ROLE_COMPANY_ADMIN' || role=='ROLE_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <podio-icon />
              </span>
              <span>Gestione Campagne</span></span
            ></router-link
          >
          <router-link to="/stats" v-if="role!='ROLE_COMPANY_ADMIN' || role=='ROLE_ADMIN'">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
            >
              <span class="mr-2">
                <chart-icon />
              </span>
              <span>Statistiche</span></span
            >
          </router-link>
          <router-link to="/" v-on:click.native="logout">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
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
import { mapState,mapActions } from 'vuex'

export default {
  name: "Header",
  data: function() {
    return { isOpen: true };
  },
    computed: {
        ...mapState('account', ['status','user','role']),
        ...mapState('navigation', ['page'])
    },
  methods: {
    drawer() {
      this.isOpen = !this.isOpen;
    },
     ...mapActions('account', ['logout'])
  },
};
</script>

<style></style>
