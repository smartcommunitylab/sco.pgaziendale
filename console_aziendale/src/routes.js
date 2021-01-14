import Vue from 'vue';
import Router from 'vue-router';
import Aziende from "./Pagine/Aziende.vue"
import Login from "./Pagine/Login.vue"
import Home from "./Pagine/Home.vue"
import Sedi from "./Pagine/Sedi.vue"
import Sede from "./Pagine/Sede.vue"
import NotFound from "./Pagine/NotFound.vue"
Vue.use(Router);
const routes = [
    {
      path: '/',
      name: 'homepage',
      component: Home
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    
    {
      path: '/aziende',
      name: 'aziende',
      component: Aziende
    },
    {
        path: '/azienda/:azienda/sedi',
        name: 'sedi',
        component: Sedi
    },
    {
        path: '/azienda/:azienda/sede/:sede',
        name: 'sede',
        component: Sede
    },
    { 
        path: '/404', 
        component: NotFound 
    },  
    { 
        path: '*', 
        redirect: '/404' 
    },
    
  ];

  export const router = new Router({
    mode: 'history',
    routes})
    router.beforeEach((to, from, next) => {
      // redirect to login page if not logged in and trying to access a restricted page
      const publicPages = ['/login'];
      const authRequired = !publicPages.includes(to.path);
      const loggedIn = localStorage.getItem('user');
    
      if (authRequired && !loggedIn) {
        return next('/login');
      }
    
      next();
    })