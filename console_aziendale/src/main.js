import Vue from 'vue'
import App from './App.vue'


import routes from "./routes.js"
import Router from "vue-router"

import './assets/styles/index.css';

const router = new Router({routes:routes,mode:'history'})



Vue.config.productionTip = false


Vue.use(Router);

new Vue({
  render: h => h(App),router:router
}).$mount('#app')
