<template>
  <nav class="flex fixed w-full items-center justify-between px-6 h-16 bg-white text-gray-700 border-b border-gray-200 z-10">
    <div class="flex items-center">
      <button class="mr-2" aria-label="Open Menu" @click="drawer">
        <svg fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" viewBox="0 0 24 24" class="w-8 h-8">
          <path d="M4 6h16M4 12h16M4 18h16"></path>
        </svg>
      </button>
      <img src="@/assets/images/logo.png" alt="Logo" class="h-auto w-12" />
    </div>
    <div class="flex items-center">
      <div class="hidden md:block md:flex md:justify-between md:bg-transparent">
        <button title="Info" class="flex items-center p-3 font-medium mr-2 text-center bg-gray-300 rounded hover:bg-gray-400 focus:outline-none focus:bg-gray-400">
          <info-outline-icon />
          <span>Info</span>
        </button>
        <button title="Contatti" class="flex items-center p-3 font-medium mr-2 text-center bg-gray-300 rounded hover:bg-gray-400 focus:outline-none focus:bg-gray-400">
          <pencil-outline-icon />

          <span>Contatti</span>
        </button>
      </div>
    </div>

    <transition enter-class="opacity-0" enter-active-class="ease-out transition-medium" enter-to-class="opacity-100" leave-class="opacity-100" leave-active-class="ease-out transition-medium" leave-to-class="opacity-0">
      <div @keydown.esc="isOpen = false" v-show="isOpen" class="z-10 fixed inset-0 transition-opacity">
        <div @click="isOpen = false" class="absolute inset-0 bg-black opacity-50" tabindex="0"></div>
      </div>
    </transition>
    <aside class="transform top-0 left-0 w-64 bg-white fixed h-full overflow-auto ease-in-out transition-all duration-300 z-30" :class="isOpen ? 'translate-x-0' : '-translate-x-full'">
      <span @click="isOpen = false" class="flex w-full items-center p-4 border-b">
        <img src="@/assets/images/logo.png" alt="Logo" class="h-auto w-32 mx-auto" />
      </span>
      <div v-if="!campagna">
        <router-link to="/" v-on:click.native="onLogin" v-if="!auth">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <login-icon />
            </span>
            <span>Entra</span></span
          >
        </router-link>

        <router-link to="/campagne" v-if="auth">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <list-campaigns-icon />
            </span>
            <span>Campagne</span></span
          >
        </router-link>
        <router-link to="/info">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <info-outline-icon />
            </span>
            <span>Info</span></span
          ></router-link
        >
        <router-link to="/contatti">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white">
            <span class="mr-2">
              <pencil-outline-icon />
            </span>
            <span>Contatti</span></span
          >
        </router-link>
        <router-link to="/" v-on:click.native="onLogout" v-if="auth">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <logout-icon />
            </span>
            <span>Esci</span></span
          >
        </router-link>
      </div>
      <div v-if="campagna">
        <router-link to="/campagne" v-on:click.native="indietro">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <arrow-left-icon />
            </span>
            <span>Indietro</span></span
          ></router-link
        >
          <router-link :to="{ name: 'campagna', params: { id: campagna.id }}">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <chart-bar-icon />
            </span>
            <span>Campagna</span></span
          >
        </router-link>
        <router-link :to="{ name: 'myperformance', params: { id: campagna.id }}">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <chart-bar-icon />
            </span>
            <span>Le mie performance</span></span
          >
        </router-link>
        <router-link :to="{ name: 'rules', params: { id: campagna.id }}">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <rules-icon />
            </span>
            <span>Regolamento</span></span
          >
        </router-link>
                <router-link :to="{ name: 'sendrequest', params: { id: campagna.id }}">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <chart-bar-icon />
            </span>
            <span>Invia Richiesta</span></span
          >
        </router-link>
                <router-link :to="{ name: 'privacy', params: { id: campagna.id }}">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <chart-bar-icon />
            </span>
            <span>Privacy Policy</span></span
          >
        </router-link>
                <router-link  to="/" v-on:click.native="leaveCampaign">
          <span @click="isOpen = false" class="flex items-center p-4 hover:bg-indigo-500 hover:text-white"
            ><span class="mr-2">
              <chart-bar-icon />
            </span>
            <span>Abbandona campagna</span></span
          >
        </router-link>
      </div>
    </aside>
  </nav>
</template>

<script>
import { AUTH } from '../variables.js'

export default {
  data() {
    return {
      isOpen: false,
    };
  },
  methods: {
    drawer() {
      this.isOpen = !this.isOpen;
    },
    indietro() {
      this.$store.dispatch("exitCampagna").then(() => {
        this.$router.push("campagne").catch(() => {});
      });
    },
    leaveCampaign() {
      //quit campaign
    },
    onLogout() {
      this.$store.dispatch("logout");
    },
    onLogin() {
       var authUrl=AUTH.AUTH_URL;
      var tokenId=AUTH.TOKEN_ID;
      var redirectUri=AUTH.REDIRECT_URI;
      return window.open(authUrl+"response_type=token&client_id="+tokenId+"&redirect_uri="+redirectUri,"_self")
    },
  },
  watch: {
    isOpen: {
      immediate: true,
      handler(isOpen) {
        if (process.client) {
          if (isOpen) document.body.style.setProperty("overflow", "hidden");
          else document.body.style.removeProperty("overflow");
        }
      },
    },
  },
  mounted() {
    document.addEventListener("keydown", (e) => {
      if (e.keyCode == 27 && this.isOpen) this.isOpen = false;
    });
  },
  computed: {
    auth() {
      return this.$store.getters.isAuthenticated;
    },
    campagna() {
      return this.$store.getters.campagna;
    },
  },
};
</script>
