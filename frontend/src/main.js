import Vue from 'vue'
import App from './App.vue'

import routes from "./routes";
import Router from "vue-router";

import "./assets/styles/index.css";


Vue.config.productionTip = false

const router = new Router({routes:routes})
Vue.use(Router);

new Vue({
  render: h => h(App),router
}).$mount('#app')
