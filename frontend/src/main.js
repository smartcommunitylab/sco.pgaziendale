import Vue from 'vue'
import VueTailwind from 'vue-tailwind'
import App from './App.vue'

import routes from "./routes";
import Router from "vue-router";

import "./assets/styles/index.css";
import settings from './myTheme.js'
import store from './store/store'


Vue.config.productionTip = false

const router = new Router({routes:routes})
Vue.use(Router);

Vue.use(VueTailwind, settings)

new Vue({
  render: h => h(App),
  router,
  store,
}).$mount('#app')
