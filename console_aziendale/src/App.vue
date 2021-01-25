<template>
  <div id="app">
    <app-header v-if="account && account.status &&  account.status.loggedIn " />
    <router-view :class="{'lg:pl-64 pt-16 lg:pt-16': (account && account.status &&  account.status.loggedIn)}" />
  </div>
</template>

<script>
import Header from "./components/Header.vue";
import { mapActions, mapState } from 'vuex'
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
        this.bootProfile();
        
    },
methods: {
    ...mapActions("account", { setDefaultCompany: "setDefaultCompany" }),
  bootProfile() {
    //if role!= admin load default company (first)

if (this.account && this.account.home)
          {
            this.setDefaultCompany();
            router.push(this.account.home.route);
            }
  }
},
    watch: {
      account(newCount, oldCount) {
      // Our fancy notification (2).
      console.log(JSON.stringify(newCount)+JSON.stringify(oldCount))
    }
    } 
};
</script>

<style></style>
