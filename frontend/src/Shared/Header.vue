<template>
<nav :class="{ 'scrolled': !view.atTopOfPage }" class="fixed font-sans flex flex-col text-center lg:flex-row lg:text-left lg:justify-between py-4 px-6 bg-white shadow lg:items-baseline w-full animated">
  <div class="mb-2 sm:mb-0">
    <router-link  to="/" class="text-2xl no-underline text-grey-darkest hover:text-blue-dark">Home</router-link>
  </div>
  <div>
    <router-link to="/" v-on:click.native="onLogin"  v-if="!auth" class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Entra</router-link>
    <router-link  to="/" v-on:click.native="onLogout"  v-if="auth" class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Esci</router-link>
    <router-link  to="/campagne"  v-if="auth" class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Campagne</router-link>
    <router-link  to="/info"  class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Info</router-link>
    <router-link  to="/contatti"  class="text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2">Contatti</router-link>
  </div>
</nav>
</template>
<script>
export default {
  name: "Header",
  data() {
    return {
       view: {
            atTopOfPage: true
        }
    }
  },
  beforeMount () {
    window.addEventListener('scroll', this.handleScroll);
},
  computed: {
    auth() {
      return this.$store.getters.isAuthenticated;
    },
  },
  methods: {
      onLogout() {
        this.$store.dispatch('logout')
      },
            onLogin() {
        this.$store.dispatch('login')
      },
    handleScroll(){
        if(window.pageYOffset>0){
            if(this.view.atTopOfPage) this.view.atTopOfPage = false
        }else{
            if(!this.view.atTopOfPage) this.view.atTopOfPage = true
        }
    }
    }
};
</script>
<style scoped>
nav {
    z-index: 10;
    top:0;
}

nav.scrolled {
    @apply shadow-2xl;
    border-bottom: 0px;
}</style>
