import Vue from 'vue';
import Router from 'vue-router';
import Aziende from "./Pagine/Aziende.vue"
import Azienda from "./Pagine/Azienda.vue"
import Login from "./Pagine/Login.vue"
import Locations from "./Pagine/Locations.vue"
import Stats from "./Pagine/Stats.vue"
import GestioneDipendenti from "./Pagine/GestioneDipendenti.vue"
import GestioneCampagne from "./Pagine/GestioneCampagne.vue"
import NotFound from "./Pagine/NotFound.vue"
import ResetPwd from "./Pagine/ResetPwd.vue"
Vue.use(Router);
const routes = [
    {
      path: '/',
      redirect: '/login' 
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/resetPwd',
      name: 'resetpwd',
      component: ResetPwd
    },
    
    {
      path: '/aziende',
      name: 'aziende',
      component: Aziende
    },
    {
        path: '/locations',
        name: 'locations',
        component: Locations
    },
    {
        path: '/dipendenti',
        name: 'dipendenti',
        component: GestioneDipendenti
    },
    {
      path: '/stats',
      name: 'stats',
      component: Stats
  },
  {
    path: '/gestionecampagne',
    name: 'gestionecampagne',
    component: GestioneCampagne
},
{
  path: '/azienda',
  name: 'azienda',
  component: Azienda
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
    
      if (authRequired && !loggedIn && to.name!='resetpwd') {
        return next('/login');
      }
    
      next();
    })