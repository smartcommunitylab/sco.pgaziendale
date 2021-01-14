<template>
  <div id="app">
    <app-header v-if="account && account.status &&  account.status.loggedIn " />
     <div v-if="alert.message" :class="`alert ${alert.type}`">{{alert.message}}</div>
    <router-view class="lg:pl-64 pt-16 lg:pt-16" />
  </div>
</template>

<script>
import Header from "./components/Header.vue";
import { mapState, mapActions } from 'vuex'

export default {
  name: "App",
  components: { "app-header": Header },
  computed: {
        ...mapState({
            account: state => state.account,
            alert: state => state.alert
        })

    },
    methods: {
        ...mapActions({
            clearAlert: 'alert/clear' 
        })
    },
    created () {
        console.log('account'+this.account);
        console.log('alert'+this.alert);
    },
    watch: {
      account(newCount, oldCount) {
      // Our fancy notification (2).
      console.log(JSON.stringify(newCount)+JSON.stringify(oldCount))
    },
      // eslint-disable-next-line no-unused-vars
        $route (to, from){
            this.clearAlert();
        }
    } 
};
</script>

<style></style>
