<template>
  <div id="app">
    <app-header v-if="account && account.status &&  account.status.loggedIn " />
    <div v-if="alert.message" :class="`alert ${alert.type}`">  <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>
{{alert.message}}</div>
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
            account: state => state.account,
            alert: state => state.alert
                    })
                    

    },
    created () {
        console.log('account'+this.account);
        //check login and push the right page
        this.bootProfile();
        
    },
methods: {
    ...mapActions("account", { setDefaultCompany: "setDefaultCompany" }),
    ...mapActions("alert", { clearAlert: "clear" }),
  bootProfile() {

if (this.account && this.account.home)
          {
                //if role!= admin load default company (first)
            this.setDefaultCompany();
            router.push(this.account.home.route);
            }
  }
},
    watch: {
      account(newCount, oldCount) {
      // Our fancy notification (2).
      console.log(JSON.stringify(newCount)+JSON.stringify(oldCount))
    },
    // eslint-disable-next-line no-unused-vars
            $route (to, from){
            // clear alert on location change
            this.clearAlert();
        }
    } 
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
    margin: 10px;}
.alert-success {
  background-color: #17a2b8;  

}
.alert-danger {
  background-color: #dc3545;  

}
</style>
