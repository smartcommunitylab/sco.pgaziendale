<template>
  <nav
    class="flex fixed w-full items-center justify-between px-6 h-16 bg-white text-gray-700 border-b border-gray-200 z-10"
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
       <img src="@/assets/images/logo.png" alt="Logo" class="h-auto w-12" />
    </div>
    <div class="flex items-center">
      <div class="hidden md:block md:flex md:justify-between md:bg-transparent">
        <button
          title="Info"
          class="flex items-center p-3 font-medium mr-2 text-center bg-gray-300 rounded  hover:bg-gray-400 focus:outline-none focus:bg-gray-400"
        >
          <svg
            fill="none"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            stroke="currentColor"
            viewBox="0 0 24 24"
            class="w-6 h-6 mr-2"
          >
            <path d="M1251 -179q254 0 474 128q222 129 348 348q127 224 127 475q0 254 -127 474q-127 218 -348 347q-219 129 -474 129q-128 0 -245 -31.5t-229 -94.5q-218 -124 -348 -348q-129 -219 -129 -476q0 -255 127 -476q64 -110 150.5 -197t197.5 -150q221 -128 476 -128zM1251 1882 q298 0 555 -149q129 -75 229.5 -176.5t175.5 -230.5q149 -255 149 -554q0 -297 -149 -554q-75 -129 -175.5 -231t-229.5 -177q-257 -149 -555 -149q-301 0 -557 149q-129 75 -230 176t-176 232q-148 257 -148 554q0 300 150 557q75 129 177 229.5t231 175.5q258 148 553 148 zM1137 204v831h227v-831h-227zM1251 1359q123 0 123 -111t-123 -111t-123 111t123 111z"></path>
          </svg>
          <span>Info</span>
        </button>
                <button
          title="Contatti"
          class="flex items-center p-3 font-medium mr-2 text-center bg-gray-300 rounded  hover:bg-gray-400 focus:outline-none focus:bg-gray-400"
        >
          <svg
            fill="none"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            stroke="currentColor"
            viewBox="0 0 24 24"
            class="w-6 h-6 mr-2"
          >
            <path d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"></path>
          </svg>
          <span>Contatti</span>
        </button>
        
      </div>
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
      class="transform top-0 left-0 w-64 bg-white fixed h-full overflow-auto ease-in-out transition-all duration-300 z-30"
      :class="isOpen ? 'translate-x-0' : '-translate-x-full'"
    >

      <span
        @click="isOpen = false"
        class="flex w-full items-center p-4 border-b"
      >
        <img src="@/assets/images/logo.png" alt="Logo" class="h-auto w-32 mx-auto" />
      </span>
      <div v-if="!campagna">
       <router-link to="/">

      <span
        @click="isOpen = false"
        class="flex items-center p-4 hover:bg-indigo-500 hover:text-white "
        ><span class="mr-2">
          <svg
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            class="w-6 h-6"
          >
            <path
              d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
            ></path>
          </svg>
        </span>
        <span>Home</span></span
      >
        </router-link>
        <router-link to="/" v-on:click.native="onLogin" v-if="!auth">
      <span
        @click="isOpen = false"
        class="flex items-center p-4 hover:bg-indigo-500 hover:text-white "
        ><span class="mr-2">
          <svg
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            class="w-6 h-6"
          >
            <path
              d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            ></path>
          </svg>
        </span>
        <span>Entra</span></span
      >
        </router-link>
        <router-link to="/" v-on:click.native="onLogout" v-if="auth">
      <span
        @click="isOpen = false"
        class="flex items-center p-4 hover:bg-indigo-500 hover:text-white "
        ><span class="mr-2">
          <svg
            fill="none"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            stroke="currentColor"
            viewBox="0 0 24 24"
            class="w-6 h-6"
          >
            <path d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"></path>
          </svg>
        </span>
        <span>Esci</span></span
      >
        </router-link>
        <router-link to="/campagne" v-if="auth">
      <span
        @click="isOpen = false"
        class="flex items-center p-4 hover:bg-indigo-500 hover:text-white "
        ><span class="mr-2">
          <svg
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            class="w-6 h-6"
          >
            <path
              d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            ></path>
          </svg>
        </span>
        <span>Campagne</span></span
      >
        </router-link>
         <router-link to="/info">
      <span
        @click="isOpen = false"
        class="flex items-center p-4 hover:bg-indigo-500 hover:text-white "
        ><span class="mr-2">
          <svg
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            class="w-6 h-6"
          >
            <path
              d="M1251 -179q254 0 474 128q222 129 348 348q127 224 127 475q0 254 -127 474q-127 218 -348 347q-219 129 -474 129q-128 0 -245 -31.5t-229 -94.5q-218 -124 -348 -348q-129 -219 -129 -476q0 -255 127 -476q64 -110 150.5 -197t197.5 -150q221 -128 476 -128zM1251 1882 q298 0 555 -149q129 -75 229.5 -176.5t175.5 -230.5q149 -255 149 -554q0 -297 -149 -554q-75 -129 -175.5 -231t-229.5 -177q-257 -149 -555 -149q-301 0 -557 149q-129 75 -230 176t-176 232q-148 257 -148 554q0 300 150 557q75 129 177 229.5t231 175.5q258 148 553 148 zM1137 204v831h227v-831h-227zM1251 1359q123 0 123 -111t-123 -111t-123 111t123 111z"
            ></path>
          </svg>
        </span>
        <span>Info</span></span
        
      ></router-link>
      <router-link to="/contatti">
       <span
        @click="isOpen = false"
        class="flex items-center p-4 hover:bg-indigo-500 hover:text-white "
        ><span class="mr-2">
          <svg
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            class="w-6 h-6"
          >
            <path
              d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
            ></path>
          </svg>
        </span>
        <span>Contatti</span></span
        
      >
      </router-link>
      </div>
      <div v-if="campagna">
      <router-link to="/campagne" v-on:click.native="indietro">
      <span
        @click="isOpen = false"
        class="flex items-center p-4 hover:bg-indigo-500 hover:text-white "
        ><span class="mr-2">
          <svg
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            class="w-6 h-6"
          >
            <path
              d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
            ></path>
          </svg>
        </span>
        <span>Indietro</span></span
        
      ></router-link>
      <router-link to="/la-mia-performance/1">
       <span
        @click="isOpen = false"
        class="flex items-center p-4 hover:bg-indigo-500 hover:text-white "
        ><span class="mr-2">
          <svg
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            class="w-6 h-6"
          >
            <path
              d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
            ></path>
          </svg>
        </span>
        <span>Le mie performance</span></span
        
      >
      </router-link>
      </div>
    </aside>
  </nav>
</template>

<script>
export default {
  data() {
    return {
      isOpen: false
    };
  },
  methods: {
    drawer() {
      this.isOpen = !this.isOpen;
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
  },
  watch: {
    isOpen: {
      immediate: true,
      handler(isOpen) {
        if (process.client) {
          if (isOpen) document.body.style.setProperty("overflow", "hidden");
          else document.body.style.removeProperty("overflow");
        }
      }
    }
  },
  mounted() {
    document.addEventListener("keydown", e => {
      if (e.keyCode == 27 && this.isOpen) this.isOpen = false;
    });
  },
  computed: {
    auth() {
      return this.$store.getters.isAuthenticated;
    },
  campagna() {
      return this.$store.getters.campagna!=null;
   }
  },
};
</script>