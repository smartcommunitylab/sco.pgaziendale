<template>
  <v-app>
    <v-app-bar
      app
      color="primary"
      dark
      clipped-left
    >
      <div class="d-flex align-center">
        <v-img
          alt="Vuetify Logo"
          class="shrink mr-2"
          contain
          src="https://cdn.vuetifyjs.com/images/logos/vuetify-logo-dark.png"
          transition="scale-transition"
          width="40"
        />

        <v-img
          alt="Vuetify Name"
          class="shrink mt-1 hidden-sm-and-down"
          contain
          min-width="100"
          src="https://cdn.vuetifyjs.com/images/logos/vuetify-name-dark.png"
          width="100"
        />
      </div>

      <v-spacer></v-spacer>

      <v-btn
        href="https://github.com/vuetifyjs/vuetify/releases/latest"
        target="_blank"
        text
      >
        <span class="mr-2">User Settings</span>
        <v-icon>mdi-setting</v-icon>
      </v-btn>
    </v-app-bar>

    <menu-header v-if="account && account.status && account.status.loggedIn && currentRouteName!='login' && currentRouteName!='resetpwd'" />

    <v-main>
      <v-container fluid>
        <Loader v-if="loading" />
        <transition name="fade">
          <div v-if="alert.message" :class="`alert ${alert.type}`">
          {{ alert.message }}
        </div>
        </transition>
        <router-view class="min-h-screen " v-if="account && account.status && account.status.loggedIn && currentRouteName!='login' && currentRouteName!='resetpwd'" />
      </v-container>
    </v-main>

    <app-footer v-if="account && account.status && account.status.loggedIn && currentRouteName!='login' && currentRouteName!='resetpwd'"/>

  </v-app>
</template>

<script>

import MenuHeader from "./components/NavBar/MenuHeader.vue";
import { mapActions, mapState } from "vuex";

import Loader from "./components/Loader";
import Footer from "@/components/Footer"
export default {
  name: 'App',

   components: { "menu-header": MenuHeader,Loader,"app-footer":Footer },


  computed: {
    ...mapState({
      account: (state) => state.account,
      alert: (state) => state.alert
    }),
    ...mapState("loader", ["loading"]),
      currentRouteName() {
        return this.$route.name;
    },
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
</style>
