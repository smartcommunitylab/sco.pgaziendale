import Vue from 'vue'
import VueTailwind from 'vue-tailwind'
import App from './App.vue'

import routes from "./routes";
import Router from "vue-router";

import "./assets/styles/index.css";
import settings from './myTheme.js'
import store from './store/store'
import axios from 'axios'
import InformationOutlineIcon from 'vue-material-design-icons/InformationOutline.vue';
import PencilOutlineIcon from 'vue-material-design-icons/PencilOutline.vue';
import LoginIcon from 'vue-material-design-icons/LoginVariant.vue';
import LogoutIcon from 'vue-material-design-icons/LogoutVariant.vue';
import ArrowLeftIcon from 'vue-material-design-icons/ArrowLeft.vue';
import ChartBarIcon from 'vue-material-design-icons/ChartBarStacked.vue';
import ListCampaingsIcon from 'vue-material-design-icons/FormatListText.vue';
import RulesIcon from 'vue-material-design-icons/TextBoxSearchOutline.vue';
import Performance from 'vue-material-design-icons/ChartLine.vue';
import Join from "vue-material-design-icons/ControllerClassicOutline";
import Unsubscribe from "vue-material-design-icons/ClipboardOffOutline";
import Campaign from "vue-material-design-icons/SealVariant";
import SendRequest from "vue-material-design-icons/EmailSendOutline";
import Privacy from "vue-material-design-icons/FileDocumentEditOutline";
import Back from "vue-material-design-icons/ArrowLeft";
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/vue-loading.css';
Vue.use(Loading);
Vue.component('info-outline-icon', InformationOutlineIcon);
Vue.component('pencil-outline-icon', PencilOutlineIcon);
Vue.component('login-icon', LoginIcon);
Vue.component('logout-icon', LogoutIcon);
Vue.component('arrow-left-icon', ArrowLeftIcon);
Vue.component('chart-bar-icon', ChartBarIcon);
Vue.component('list-campaigns-icon', ListCampaingsIcon);
Vue.component('rules-icon', RulesIcon);
Vue.component('performance-icon',Performance)
Vue.component('join-icon',Join);
Vue.component('unsubscribe-icon',Unsubscribe);
Vue.component('campaign-icon',Campaign);
Vue.component('send-request-icon',SendRequest);
Vue.component('privacy-icon',Privacy);
Vue.component('back-icon',Back);
Vue.config.productionTip = false
const router = new Router({routes:routes,mode:'history'})
Vue.use(Router);
Vue.use(VueTailwind, settings)
axios.interceptors.request.use(
  (config) => {
    let token = sessionStorage.getItem('token');

    if (token) {
      config.headers['Authorization'] = `Bearer ${ token }`;
    }

    return config;
  }, 

  (error) => {
    return Promise.reject(error);
  }
);

//filters
Vue.filter('round', function(value, decimals) {
  if(!value) {
    value = 0;
  }

  if(!decimals) {
    decimals = 0;
  }

  value = Math.round(value * Math.pow(10, decimals)) / Math.pow(10, decimals);
  return value;
});

new Vue({
  render: h => h(App),
  router,
  store,
}).$mount('#app')


