<template>
  <div class="capagna flex flex-col" >
    <div class="flex flex-col ml-8 mt-8">
      <div class="text-4xl mt-8 text-black text-center">Campagna:{{campagna.title}}</div>
    </div>
    <div class="flex flex-col">
      <img class="object-none h-48 w-full" :src="campagna.logo" />
    </div>
    <div v-if="companies && companies.length">
    <div class="flex flex-col" >
      <div class="text-4xl mt-8 text-black text-center">Organizzata da</div>
    </div>
    <div class="flex flex-col">
      <img class="object-contain h-48 w-full" :src="companies[0].logo" />
    </div>
    </div>
    <button v-if="!campagna.userInCampaign"
            type="button"
            @click="subscribe"
            class="my-1 inline-flex items-center lg:m-auto bg-transparent hover:bg-green-600 font-semibold hover:text-white py-1 px-4 border-2 border-green-600 hover:border-transparent rounded"
          >
            <info-outline-icon />Iscriviti
          </button>
    <div class="flex flex-col m-8 text-justify">
      {{campagna.description}}
    </div>
    <div  v-if="companies && companies.length>1">
    <div class="flex flex-col">
      <div class="text-4xl mt-8 text-black text-center">
        A questa campagna partecipa anche:
      </div>
    </div>
    <div class="flex flex-col" v-for="otherCompany in companies" v-bind:key="otherCompany.id">
      <img class="object-contain h-48 w-full" :src="otherCompany.logo" />
    </div>
    </div>
<card-modal :showing="modalSubscribeShowing" @close="modalSubscribeShowing = false">
    <h2 class="text-xl font-bold text-gray-900">Iscrizione alla campagna</h2>
    <p>Vuoi iscriverti alla campagna?</p>
    <label class="block">
  <span class="text-gray-700">Codice utente</span>
  <input class="form-input mt-1 block w-full" placeholder="Codice" v-model="key">
</label>
        <button v-if="!campagna.userInCampaign"
      class="bg-blue-600 text-white px-4 py-2 text-sm uppercase tracking-wide font-bold rounded-lg"
      @click="confirm"
    >
      Iscriviti
    </button>
    <button
      class="bg-blue-600 text-white px-4 py-2 text-sm uppercase tracking-wide font-bold rounded-lg"
      @click="modalSubscribeShowing = false"
    >
      Chiudi
    </button>
</card-modal>
  <card-modal :showing="modalUnsubscribeShowing" @close="modalUnsubscribeShowing = false">
    <h2 class="text-xl font-bold text-gray-900">Iscrizione alla campagna</h2>
    <p>Vuoi abbandonare la campagna?</p>
        <label class="block">
  <span class="text-gray-700">Codice utente</span>
  <input class="form-input mt-1 block w-full" placeholder="Codice" v-model="key">
</label>
        <button 
      class="bg-blue-600 text-white px-4 py-2 text-sm uppercase tracking-wide font-bold rounded-lg"
      @click="confirmLeave"
    >
      Conferma
    </button>
    <button
      class="bg-blue-600 text-white px-4 py-2 text-sm uppercase tracking-wide font-bold rounded-lg"
      @click="modalUnsubscribeShowing = false"
    >
      Chiudi
    </button>
</card-modal>
  </div>
</template>

<script>
import DataApi from "../../communication/dataApi";
import EventBus from '../../communication/eventBus';

import CardModal from "../../Components/GenericModal.vue"
export default {
  name: "Campagna",
  components: {
    CardModal
  },

  data() {
    return {
      companies:[],
      modalUnsubscribeShowing:false,
      modalSubscribeShowing:false,
      key:''
    }
  },
  methods: {
    subscribe: function() {
      this.modalSubscribeShowing=true
    },
    confirm:function() {
      if (this.key)
      DataApi.subscribeCampaign(this.campagna.id,this.companies[0].code,this.key).then(res => {
        //change campaign in store (subscribed)
        console.log(res);
        this.modalSubscribeShowing=false;
      },err => {
        console.log(err)
      })
    },
    confirmLeave:function() {
            DataApi.unsubrscribeCampaign(this.campagna.id,this.companies[0].code,this.key).then(res => {
        //change campaign in store (subscribed)
        console.log(res);
        this.modalUnsubscribeShowing=false;
      },err => {
        console.log(err)
      })
      
    }
  },
  created: function () {
    DataApi.getCompaniesOfCampaign(this.campagna.id).then(
      (res) => {
        this.companies = res.data;
        console.log(this.campaigns);
      },
      (err) => {
        console.log(err);
      }
    );
    EventBus.$on('LEAVE_CAMPAIGN',  () => {
     this.modalUnsubscribeShowing=true;
    });
  },
  computed: {
    campagna() {
      return this.$store.getters.campagna;
    },
  },
};
</script>

<style></style>
