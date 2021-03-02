import Vue from 'vue'
import App from './App.vue'
import Back from "vue-material-design-icons/ArrowLeft";
import LoginIcon from 'vue-material-design-icons/LoginVariant.vue';
import PencilOutlineIcon from 'vue-material-design-icons/PencilOutline.vue';
import ListCampaingsIcon from 'vue-material-design-icons/FormatListText.vue';
import LogoutIcon from 'vue-material-design-icons/LogoutVariant.vue';
import InformationOutlineIcon from 'vue-material-design-icons/InformationOutline.vue';
import Factory from "vue-material-design-icons/Factory";
import Chart from "vue-material-design-icons/ChartLine";
import Users from "vue-material-design-icons/AccountGroup";
import Sedi from "vue-material-design-icons/HomeGroup";
import Podio from "vue-material-design-icons/Podium";
import Web from "vue-material-design-icons/Web";
import Address from "vue-material-design-icons/MapMarker";
import Email from "vue-material-design-icons/Email";
import Phone from "vue-material-design-icons/Phone";
import Add from "vue-material-design-icons/Plus";
import AddEmployess from "vue-material-design-icons/AccountPlus";
import Import from "vue-material-design-icons/Import";
import DotsHorizzontal from "vue-material-design-icons/DotsHorizontal";
import Eye from "vue-material-design-icons/Eye";
import EyeOff from "vue-material-design-icons/EyeOff";
import Delete from "vue-material-design-icons/Delete";
import axios from 'axios'
import {router} from "./routes.js"
import { store } from './store'
import './assets/styles/index.css';
import Vuelidate from 'vuelidate'

Vue.config.productionTip = false
Vue.component('pencil-outline-icon', PencilOutlineIcon);
Vue.component('back-icon',Back);
Vue.component('login-icon', LoginIcon);
Vue.component('info-outline-icon', InformationOutlineIcon);
Vue.component('logout-icon', LogoutIcon);
Vue.component('list-campaigns-icon', ListCampaingsIcon);
Vue.component('factory-icon', Factory);
Vue.component('chart-icon', Chart);
Vue.component('users-icon', Users);
Vue.component('sedi-icon', Sedi);
Vue.component('podio-icon', Podio);
Vue.component('web-icon', Web);
Vue.component('address-icon', Address);
Vue.component('email-icon', Email);
Vue.component('phone-icon', Phone);
Vue.component('add-icon', Add);
Vue.component('add-employee', AddEmployess);
Vue.component('import-icon',Import)
Vue.component('dots-h-icon',DotsHorizzontal)
Vue.component('eye-icon',Eye)
Vue.component('eye-off-icon',EyeOff)
Vue.component('delete-icon',Delete)
axios.interceptors.request.use(
  (config) => {
    let token = localStorage.getItem('token');

    if (token) {
      config.headers['Authorization'] = 'Bearer '+ token;
    }

    return config;
  }, 

  (error) => {
    return Promise.reject(error);
  }
);

Vue.use(Vuelidate)

new Vue({
  render: h => h(App),
  router,
  store:store,
}).$mount('#app')
