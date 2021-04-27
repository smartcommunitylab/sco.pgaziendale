import Vue from 'vue'
import App from './App.vue'
import Back from "vue-material-design-icons/ArrowLeft";
import LoginIcon from 'vue-material-design-icons/LoginVariant.vue';
import PencilOutlineIcon from 'vue-material-design-icons/PencilOutline.vue';
import ListCampaingsIcon from 'vue-material-design-icons/FormatListText.vue';
import LogoutIcon from 'vue-material-design-icons/LogoutVariant.vue';
import InformationOutlineIcon from 'vue-material-design-icons/InformationOutline.vue';
import Factory from "vue-material-design-icons/Factory";
import Chart from "vue-material-design-icons/ChartLine";
import Users from "vue-material-design-icons/AccountGroup";
import Sedi from "vue-material-design-icons/HomeGroup";
import Podio from "vue-material-design-icons/Podium";
import Web from "vue-material-design-icons/Web";
import Address from "vue-material-design-icons/MapMarker";
import Email from "vue-material-design-icons/Email";
import Phone from "vue-material-design-icons/Phone";
import Add from "vue-material-design-icons/Plus";
import AddEmployess from "vue-material-design-icons/AccountPlus";
import Import from "vue-material-design-icons/Import";
import DotsHorizzontal from "vue-material-design-icons/DotsHorizontal";
import Eye from "vue-material-design-icons/Eye";
import EyeOff from "vue-material-design-icons/EyeOff";
import Delete from "vue-material-design-icons/Delete";
import AccountCog from "vue-material-design-icons/AccountCog";
import CrosshairsGps from "vue-material-design-icons/CrosshairsGps";
import CalendarRemove from "vue-material-design-icons/CalendarRemove";
import Code from "vue-material-design-icons/FormTextboxPassword";
import Help from "vue-material-design-icons/HelpCircle";

import axios from 'axios'
import {router} from "./routes.js"
import { store } from './store'
import './assets/styles/index.css';
import Vuelidate from 'vuelidate'
import { VueEditor } from "vue2-editor";
import { LMap, LTileLayer, LMarker,LPopup,LCircle} from 'vue2-leaflet';


// import VGeosearch from 'vue2-leaflet-geosearch';

import 'leaflet/dist/leaflet.css';
import { Icon }  from 'leaflet'
delete Icon.Default.prototype._getIconUrl;

Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});
import moment from 'moment'

Vue.prototype.moment = moment

Vue.component('l-map', LMap);
Vue.component('l-tile-layer', LTileLayer);
Vue.component('l-marker', LMarker);
Vue.component('vue-editor',VueEditor);
Vue.component('l-popup',LPopup);
Vue.component('l-circle',LCircle);
// Vue.component('v-geosearch',VGeosearch);
Vue.config.productionTip = false
Vue.component('pencil-outline-icon', PencilOutlineIcon);
Vue.component('back-icon',Back);
Vue.component('login-icon', LoginIcon);
Vue.component('info-outline-icon', InformationOutlineIcon);
Vue.component('logout-icon', LogoutIcon);
Vue.component('list-campaigns-icon', ListCampaingsIcon);
Vue.component('factory-icon', Factory);
Vue.component('chart-icon', Chart);
Vue.component('users-icon', Users);
Vue.component('sedi-icon', Sedi);
Vue.component('podio-icon', Podio);
Vue.component('web-icon', Web);
Vue.component('address-icon', Address);
Vue.component('email-icon', Email);
Vue.component('phone-icon', Phone);
Vue.component('add-icon', Add);
Vue.component('add-employee', AddEmployess);
Vue.component('import-icon',Import);
Vue.component('dots-h-icon',DotsHorizzontal);
Vue.component('eye-icon',Eye);
Vue.component('eye-off-icon',EyeOff);
Vue.component('delete-icon',Delete);
Vue.component('account-cog-icon',AccountCog);
Vue.component('gps-icon',CrosshairsGps);
Vue.component('calendar-remove-icon',CalendarRemove);
Vue.component('code-icon',Code);
Vue.component('help-icon',Help);
Vue.directive('init', {
  bind: function(el, binding, vnode) {
    vnode.context[binding.arg] = binding.value;
  }
});
axios.defaults.showLoader = true;

axios.interceptors.request.use(
  (config) => {
    let token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = 'Bearer '+ token;
    }
    if (config.showLoader) {
      store.dispatch('loader/pending');
  }
    return config;
  }, 

  (error) => {
    if (error.config.showLoader) {
      store.dispatch('loader/done');
  }
    return Promise.reject(error);
  }
);
axios.interceptors.response.use(
  response => {
      if (response.config.showLoader) {
          store.dispatch('loader/done');
      }

      return response;
  },
  error => {
      let response = error.response;

      if (response.config.showLoader) {
          store.dispatch('loader/done');
      }

      return Promise.reject(error);
  }
)
Vue.use(Vuelidate)

new Vue({
  render: h => h(App),
  router,
  store:store,
}).$mount('#app')
