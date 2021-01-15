<template>
  <div id="app">
    <app-header v-if="account && account.status &&  account.status.loggedIn " />
    <router-view :class="{'lg:pl-64 pt-16 lg:pt-16': (account && account.status &&  account.status.loggedIn)}" />
  </div>
</template>

<script>
import Header from "./components/Header.vue";
import { mapState } from 'vuex'
import {router} from './routes'
export default {
  name: "App",
  components: { "app-header": Header },
  computed: {
        ...mapState({
            account: state => state.account
                    })

    },
    created () {
        console.log('account'+this.account);
        //check login and push the right page
        if (this.account && this.account.home)
          router.push(this.account.home.route);
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
