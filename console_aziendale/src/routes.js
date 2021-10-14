import Vue from 'vue';
import Router from 'vue-router';
import Aziende from "@/pages/companies/Companies.vue"
import Azienda from "@/pages/companies/CompanyProfile.vue"
import Login from "@/pages/login/Login.vue"
import Locations from "@/pages/locations/Locations.vue"
import StatsOLD from "@/pages/stats-old/Stats.vue"
import Stats from "@/pages/stats/Stats.vue"
import GestioneDipendenti from "@/pages/employees/Employees.vue"
import GestioneCampagne from "@/pages/campaigns/Campaigns.vue"
import GestioneUtenti from "@/pages/users/Users.vue"
import NotFound from "@/pages/NotFound.vue"
import ResetPwd from "@/pages/login/ResetPwd.vue"

Vue.use(Router);

const routes = [
    {
      path: '/',
      redirect: '/Login' 
    },
    {
      path: '/Login',
      name: 'Login',
      component: Login
    },
    {
      path: '/ResetPassword',
      name: 'ResetPassword',
      component: ResetPwd
    },
    
    {
      path: '/GestioneAziende',
      name: 'GestioneAziende',
      component: Aziende
    },
    {
        path: '/GestioneSedi',
        name: 'GestioneSedi',
        component: Locations
    },
    {
        path: '/GestioneDipendenti',
        name: 'GestioneDipendenti',
        component: GestioneDipendenti
    },
    {
      path: '/GestioneUtenti',
      name: 'GestioneUtenti',
      component: GestioneUtenti
  },
  {
      path: '/StatisticheOLD',
      name: 'StatisticheOLD',
      component: StatsOLD
  },
  {
    path: '/Statistiche',
    name: 'Statistiche',
    component: Stats
},
  {
    path: '/GestioneCampagne',
    name: 'GestioneCampagne',
    component: GestioneCampagne
},
{
  path: '/ProfiloAzienda',
  name: 'ProfiloAzienda',
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
      const publicPages = ['/Login'];
      const authRequired = !publicPages.includes(to.path);
      const loggedIn = localStorage.getItem('user');
    
      if (authRequired && !loggedIn && to.name!='ResetPassword') {
        return next('/Login');
      }
    
      next();
    })