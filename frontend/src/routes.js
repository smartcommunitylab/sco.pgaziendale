

import Homepage from "./Pagine/Homepage.vue";
import Info from "./Pagine/Info.vue";
import Contatti from "./Pagine/Contatti.vue";
import Campagne from "./Pagine/Campagne.vue";
import MyPerformance from "./Pagine/MyPerformance.vue";
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
      component: Campagne
    },
    {
      path: '/la-mia-performance/:id',
      name: 'myperformance',
      component: MyPerformance
    }
  ];

  export default routes;