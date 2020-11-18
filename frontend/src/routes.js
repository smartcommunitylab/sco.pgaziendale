

import Homepage from "./Pagine/Homepage.vue"
import Info from "./Pagine/Info.vue";
import Contatti from "./Pagine/Contatti.vue";
import Campagne from "./Pagine/Campagne.vue";

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
    }
  ];

  export default routes;