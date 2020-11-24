<template>
  <div class="font-sans antialiased" id="app">
    <nav :class="{ scrolled: !view.atTopOfPage }" class="flex items-center justify-between flex-wrap bg-teal p-6 fixed font-sans flex-col text-center lg:flex-row lg:text-left lg:justify-between py-2 px-6 bg-white shadow lg:items-baseline w-full animated">
      <div class="block sm:hidden">
        <button @click="toggle" class="flex items-center px-3 py-2 border rounded text-teal-lighter border-teal-light text-white	 hover:text-white hover:border-white">
          <svg class="fill-current h-3 w-3" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
            <title>Menu</title>
            <path d="M0 3h20v2H0V3zm0 6h20v2H0V9zm0 6h20v2H0v-2z" />
          </svg>
        </button>
      </div>
      <div :class="open ? 'block' : 'hidden'" class="w-full flex-grow sm:flex sm:items-center sm:w-auto">
        <a href="#responsive-header" class="no-underline block mt-4 sm:inline-block sm:mt-0 text-teal-lighter text-white	 hover:text-white">
          <router-link to="/" class="text-2xl no-underline text-grey-darkest hover:text-blue-dark">Home</router-link>
        </a>
        <div class="text-sm sm:flex-grow"></div>
        <div v-if="!campagna">
          <a href="#responsive-header" class="no-underline block mt-4 sm:inline-block sm:mt-0 text-teal-lighter text-white	 hover:text-white">
            <router-link to="/" v-on:click.native="onLogin" v-if="!auth" class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Entra</router-link>
          </a>
          <a href="#responsive-header" class="no-underline block mt-4 sm:inline-block sm:mt-0 text-teal-lighter text-white	 hover:text-white">
            <router-link to="/" v-on:click.native="onLogout" v-if="auth" class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Esci</router-link>
          </a>
          <a href="#responsive-header" class="no-underline block mt-4 sm:inline-block sm:mt-0 text-teal-lighter text-white	 hover:text-white">
            <router-link to="/campagne" v-if="auth" class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Campagne</router-link>
          </a>
          <a href="#responsive-header" class="no-underline block mt-4 sm:inline-block sm:mt-0 text-teal-lighter text-white	 hover:text-white">
            <router-link to="/info" class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Info</router-link>
          </a>
          <a href="#responsive-header" class="no-underline block mt-4 sm:inline-block sm:mt-0 text-teal-lighter text-white	 hover:text-white">
            <router-link to="/contatti" class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Contatti</router-link>
          </a>
        </div>
        <div v-if="campagna">
        <a href="#responsive-header" class="no-underline block mt-4 sm:inline-block sm:mt-0 text-teal-lighter text-white	 hover:text-white">
            <router-link to="/" v-on:click.native="indietro" v-if="auth" class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Indietro</router-link>
          </a>
          <a href="#responsive-header" class="no-underline block mt-4 sm:inline-block sm:mt-0 text-teal-lighter text-white	 hover:text-white">
            <router-link to="/campagne"  class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Le mie Performance</router-link>
          </a>
        </div>
      </div>
    </nav>
  </div>
</template>
<script>
export default {
  name: "Header",
  data() {
    return {
      view: {
        atTopOfPage: true,
      },
      open: false,
    };
  },
  beforeMount() {
    window.addEventListener("scroll", this.handleScroll);
  },
  computed: {
    auth() {
      return this.$store.getters.isAuthenticated;
    },
  campagna() {
      return this.$store.getters.campagna!=null;
   }

  },
  methods: {
    toggle() {
      this.open = !this.open;
    },
    indietro() {
      this.$store.dispatch("exitCampagna").then(() => {
        this.$router.push('campagne').catch(()=>{});
   })
    },
    onLogout() {
      this.$store.dispatch("logout");
    },
    onLogin() {
      return window.open("https://tn.smartcommunitylab.it/aac/eauth/authorize?response_type=token&client_id=ec03a596-e41e-49cc-808c-62f39e01de0b&redirect_uri=http://localhost:8080/callback", "_self");
      // this.$store.dispatch("login");
    },
    handleScroll() {
      if (window.pageYOffset > 0) {
        if (this.view.atTopOfPage) this.view.atTopOfPage = false;
      } else {
        if (!this.view.atTopOfPage) this.view.atTopOfPage = true;
      }
    },
  },
};
</script>
<style scoped>
nav {
  z-index: 10;
  top: 0;
   background-color: var(--blue);
}

nav.scrolled {
  @apply shadow-2xl;
  border-bottom: 0px;
}
</style>
