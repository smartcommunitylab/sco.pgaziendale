<template>
  <div id="app">
    <Loader v-if="loading" />
    <menu-header v-if="account && account.status && account.status.loggedIn && currentRouteName!='login'" />
    <div v-if="alert.message" :class="`alert ${alert.type}`">
      <span class="closebtn" onclick="this.parentElement.style.display='none';"
        >&times;</span
      >
      {{ alert.message }}
    </div>
    <router-view
      :class="{
        'lg:pl-64 pt-16 lg:pt-16': account && account.status && account.status.loggedIn && currentRouteName!='login',
      }"
    />
  </div>
</template>

<script>
import MenuHeader from "./components/MenuHeader.vue";
import { mapActions, mapState } from "vuex";
import Loader from "./components/Loader";
// import httpClient from './utils/httpClient';
export default {
  name: "App",
  components: { "menu-header": MenuHeader,Loader },
  computed: {
    ...mapState({
      account: (state) => state.account,
      alert: (state) => state.alert
    }),
    ...mapState("loader", ["loading"]),
      currentRouteName() {
        return this.$route.name;
    }
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
    account(newCount, oldCount) {
      // Our fancy notification (2).
      console.log(JSON.stringify(newCount) + JSON.stringify(oldCount));
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
  position: absolute;
  margin: 10px;
      left: 50%;
    transform: translate(-50%, 0);
    z-index: 999;
}
.alert-success {
  background-color: #17a2b8;
}
.alert-danger {
  background-color: #dc3545;
}
</style>
