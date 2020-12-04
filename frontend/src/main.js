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

Vue.component('info-outline-icon', InformationOutlineIcon);
Vue.component('pencil-outline-icon', PencilOutlineIcon);
Vue.component('login-icon', LoginIcon);
Vue.component('logout-icon', LogoutIcon);
Vue.component('arrow-left-icon', ArrowLeftIcon);
Vue.component('chart-bar-icon', ChartBarIcon);
Vue.component('list-campaigns-icon', ListCampaingsIcon);
Vue.component('rules-icon', RulesIcon);
Vue.component('Performance',Performance)
Vue.config.productionTip = false
const router = new Router({routes:routes,mode:'history'})
Vue.use(Router);
Vue.use(VueTailwind, settings)
axios.interceptors.request.use(
  (config) => {
    let token = localStorage.getItem('token');

    if (token) {
      config.headers['Authorization'] = `Bearer ${ token }`;
    }

    return config;
  }, 

  (error) => {
    return Promise.reject(error);
  }
);
new Vue({
  render: h => h(App),
  router,
  store,
}).$mount('#app')


