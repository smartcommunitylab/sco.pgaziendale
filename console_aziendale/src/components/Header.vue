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
        <img src="" alt="Logo" class="h-auto w-12" />
      </div>
      <div class="flex items-center">
        <button class="mr-2" aria-label="Open Menu">
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
          <back-icon />
        </button>
        <img src="" alt="Logo" class="h-auto w-12" />
      </div>
      <div class="flex items-center">
        <span class="text-xl">Titolo pagina</span>
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
          <img src="" alt="Logo" class="h-auto w-32 mx-auto" />
        </span>
        <div>
          <router-link to="/" v-on:click.native="onLogin">
            <span
              @click="isOpen = false"
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <login-icon />
              </span>
              <span>Entra</span></span
            >
          </router-link>

          <router-link to="/campagne">
            <span
              @click="isOpen = false"
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <list-campaigns-icon />
              </span>
              <span>Campagne</span></span
            >
          </router-link>

          <router-link to="/" v-on:click.native="onLogout">
            <span
              @click="isOpen = false"
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
        <img src="" alt="Logo" class="h-auto w-12" />
      </div>
      <div class="flex items-center">
        <span class="text-xl">Titolo pagina</span>
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
          <img src="" alt="Logo" class="h-auto w-32 mx-auto" />
        </span>
        <div>
          <router-link to="/">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <login-icon />
              </span>
              <span>Gestione Sedi</span></span
            >
          </router-link>

          <router-link to="/campagne">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <list-campaigns-icon />
              </span>
              <span>Gestione Dipendenti</span></span
            >
          </router-link>
          <router-link to="/info">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
              ><span class="mr-2">
                <info-outline-icon />
              </span>
              <span>Gestione Campagne</span></span
            ></router-link
          >
          <router-link to="/contatti">
            <span
              class="flex items-center p-4 hover:bg-white hover:text-primary"
            >
              <span class="mr-2">
                <pencil-outline-icon />
              </span>
              <span>Statistiche</span></span
            >
          </router-link>
        </div>
      </aside>
    </nav>
  </div>
</template>

<script>
export default {
  name: "Header",
  data: function() {
    return { isOpen: true };
  },

  methods: {
    drawer() {
      this.isOpen = !this.isOpen;
    },
  },
};
</script>

<style></style>
