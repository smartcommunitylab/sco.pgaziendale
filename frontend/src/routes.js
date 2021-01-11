

import Homepage from "./Pagine/Homepage.vue";
import Info from "./Pagine/Info.vue";
import Contatti from "./Pagine/Contatti.vue";
import Campagne from "./Pagine/Campagne.vue";
import Campagna from "./Pagine/Campagna/Campagna.vue";
import Privacy from "./Pagine/Campagna/Privacy.vue";
import Credits from "./Pagine/Credits.vue";
import Rules from "./Pagine/Campagna/Rules.vue";
import Callback from "./Pagine/Callback.vue";
import Logout from "./Pagine/Logout.vue";
import MyPerformance from "./Pagine/Campagna/MyPerformance.vue";
import InviaRichiesta from "./Pagine/Campagna/InviaRichiesta.vue"
 import store from './store/store'

const routes = [
    {
      path: '/',
      name: 'homepage',
      component: Homepage
    },
    {
      path: '/info',
      name: 'info',
      component: Info
    },
    {
      path: '/contatti',
      name: 'contatti',
      component: Contatti
    },
    {
      path: '/credits',
      name: 'credits',
      component: Credits
    },
    {
      path: '/campagne',
      name: 'campagne',
      component: Campagne,
      beforeEnter (to, from, next) {
        if (store.getters.isAuthenticated) {
          next()
        } else {
          next('/')
        }
      }
    },
    {
      path: '/callback',
      name: 'callback',
      component: Callback
    },
    {
      path: '/logout',
      name: 'logout',
      component: Logout
    },
    {
      path: '/campagna/:id',
      name: 'campagna',
      component: Campagna,
      beforeEnter (to, from, next) {
        if (store.getters.isAuthenticated) {
          next()
        } else {
          next('/')
        }
      }
    },
    {
      path: '/la-mia-performance/:id',
      name: 'myperformance',
      component: MyPerformance,
      beforeEnter (to, from, next) {
        if (store.getters.isAuthenticated) {
          next()
        } else {
          next('/')
        }
      }
    },
    {
      path: '/rules/:id',
      name: 'rules',
      component: Rules,
      beforeEnter (to, from, next) {
        if (store.getters.isAuthenticated) {
          next()
        } else {
          next('/')
        }
      }
    },
    {
      path: '/send-request/:id',
      name: 'sendrequest',
      component: InviaRichiesta,
      beforeEnter (to, from, next) {
        if (store.getters.isAuthenticated) {
          next()
        } else {
          next('/')
        }
      }},
    {
      path: '/privacy/:id',
      name: 'privacy',
      component: Privacy,
      beforeEnter (to, from, next) {
        if (store.getters.isAuthenticated) {
          next()
        } else {
          next('/')
        }
      }
    }
    
  ];

  export default routes;