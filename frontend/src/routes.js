

import Homepage from "./Pagine/Homepage.vue";
import Info from "./Pagine/Info.vue";
import Contatti from "./Pagine/Contatti.vue";
import Campagne from "./Pagine/Campagne.vue";
import Campagna from "./Pagine/Campagna.vue";
import Callback from "./Pagine/Callback.vue";
import Logout from "./Pagine/Logout.vue";
import MyPerformance from "./Pagine/MyPerformance.vue";
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
    }
  ];

  export default routes;