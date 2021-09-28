<template>
  <v-app>
    <v-app-bar
      app
      color="primary"
      dark
      clipped-left
    >
      <!-- TODO: Da modificare il link da dove pesca il logo, al momento provvisorio -->
      <div class="d-flex align-center">
        <v-img
          alt="Vuetify Name"
          class="shrink mt-1 hidden-sm-and-down"
          contain
          src="@/assets/images/pgaziendale.png"
          width="75px"
        />
      </div>

      <v-spacer></v-spacer>
      <profile-manager v-if="account && account.status && account.status.loggedIn && currentRouteName!='login' && currentRouteName!='resetpwd'"/>
    </v-app-bar>

    <menu-header v-if="account && account.status && account.status.loggedIn && currentRouteName!='login' && currentRouteName!='resetpwd'" />

    <v-main>
      <v-container class="p-0 m-0 blockScroll">
        <!-- NON VIENE MAI UTILIZZATO - Componente inutile (?)-->
        <!-- <Loader v-if="loading" /> -->
        <transition name="fade">
          <snackbar v-if="alert.message" :snackbar="true" :text="alert.message">
          </snackbar>
        </transition>

        <!-- INIZIO ESEMPIO: Nuovo componente modale-->
        <modal-center v-show="active" :modalType="type" :modalObject="object"/>
        <!-- FINE ESEMPIO: Nuovo componente modale-->

        <router-view class="min-h-screen px-5 py-5 pb-10" v-if="account && account.status && account.status.loggedIn && currentRouteName!='login' && currentRouteName!='resetpwd'" />
        <router-view class="min-h-screen " v-else />
      </v-container>
    </v-main>

    <app-footer/>

  </v-app>
</template>

<script>

import MenuHeader from "./components/NavBar/MenuHeader.vue";
import { mapActions, mapState } from "vuex";

/*import Loader from "./components/Loader";*/
import Footer from "@/components/Footer"
import Snackbar from "@/components/Snackbar.vue"
import ModalCenter from "@/components/ModalCenter.vue"
import ProfileManager from "@/components/ProfileManager.vue"

export default {
  name: 'App',

  components: { 
    "menu-header": MenuHeader,
    "app-footer":Footer,
    "snackbar":Snackbar,
    "modal-center": ModalCenter,
    "profile-manager": ProfileManager,
  },


  computed: {
    ...mapState({
      account: (state) => state.account,
      alert: (state) => state.alert
    }),
    ...mapState("loader", ["loading"]),
      currentRouteName() {
        return this.$route.name;
    },
    ...mapState("modal", ["active", "type", "object"]),
    ...mapState('account', ['status']),
    ...mapState('alert', ['message'])
  },
  created() {
    console.log("account" + this.account);
    //check login and push the right page
    // this.bootProfile();
  },
  methods: {
    ...mapActions("account", { setDefaultCompany: "setDefaultCompany" }),
    ...mapActions("alert", { clearAlert: "clear" }),
  },
  watch: {
    // eslint-disable-next-line no-unused-vars
    status(newCount, oldCount) {
      console.log(JSON.stringify(newCount) + JSON.stringify(oldCount));
    },
    message(newAlert,oldAlert){
      console.log(JSON.stringify(newAlert) + JSON.stringify(oldAlert));
      setTimeout(()=>this.clearAlert(),2500)
    },
    // eslint-disable-next-line no-unused-vars
    $route(to, from) {
      // clear alert on location change
      this.clearAlert();
    },
    active: function() {
      if(this.active){
        document.documentElement.style.overflow = 'hidden';
        return;
      }
      document.documentElement.style.overflow = 'auto';
      document.documentElement.style.overflowX = 'hidden';
    }
  },
};
</script>

<style scoped>
.closebtn {
  margin-left: 15px;
  color: white;
  font-weight: bold;
  float: right;
  font-size: 22px;
  line-height: 20px;
  cursor: pointer;
  transition: 0.3s;
}
.alert {
  padding: 20px;
  color: white;
  position: fixed;
    position: fixed; /* or absolute */
  top: 50%;
  margin: 10px;
      left: 50%;
    transform: translate(-50%, -50%);
    z-index: 999;
  border-radius: 25px;
}
.alert-success {
  background-color: rgba(15, 112, 183);
}
.alert-danger {
  background-color: #dc3545;
}

.blockScroll{
  overflow: hidden;
}
</style>
