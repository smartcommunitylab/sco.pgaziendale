import Vue from 'vue'
import VueTailwind from 'vue-tailwind'
import App from './App.vue'

import routes from "./routes";
import Router from "vue-router";

import "./assets/styles/index.css";
import settings from './myTheme.js'
import store from './store/store'
import axios from 'axios'


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
