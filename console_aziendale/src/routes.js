import Aziende from "./Pagine/Aziende.vue"
import HelloWorld from "./Pagine/HelloWorld.vue"
import Sedi from "./Pagine/Sedi.vue"
import GestioneDipendenti from "./Pagine/GestioneDipendenti.vue"
import NotFound from "./Pagine/NotFound.vue"
const routes = [
    {
      path: '/',
      name: 'homepage',
      component: HelloWorld
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
        path: '/azienda/:azienda/dipendenti',
        name: 'sede',
        component: GestioneDipendenti
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

  export default routes;