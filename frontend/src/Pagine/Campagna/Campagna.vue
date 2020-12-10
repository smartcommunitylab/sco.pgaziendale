<template>
  <div class="capagna flex flex-col">
    <div class="flex flex-col ml-8 mt-8">
      <div class="text-4xl mt-8 text-black text-center">
        Campagna:{{ campagna.title }}
      </div>
    </div>
    <div class="flex flex-col">
      <img class="object-none h-48 w-full" :src="campagna.logo" />
    </div>
    <div v-if="companies && companies.length && !campagna.userInCampaign">
      <div class="flex flex-col">
        <div class="text-4xl mt-8 text-black text-center">Organizzata da</div>
      </div>
      <div
        class="flex flex-col"
        v-for="company in companies"
        v-bind:key="company.id"
      >
        <img class="object-contain h-48 w-full" :src="company.logo" />
      </div>
    </div>
    <div v-if="campagna.userInCampaign">
      <div class="flex flex-col">
        <div class="text-4xl mt-8 text-black text-center">Iscritto con</div>
      </div>
      <div class="flex flex-col">
        <img class="object-contain h-48 w-full" :src="myCompany.logo" />
        {{campagna.subscribedCompany}}
      </div>
    </div>
    <button
      v-if="!campagna.userInCampaign"
      type="button"
      @click="subscribe"
      class="my-1 inline-flex items-center lg:m-auto bg-transparent hover:bg-green-600 font-semibold hover:text-white py-1 px-4 border-2 border-green-600 hover:border-transparent rounded"
    >
      <info-outline-icon />Iscriviti
    </button>
    <div class="flex flex-col m-8 text-justify">
      {{ campagna.description }}
    </div>
    <div v-if="companies && companies.length > 1">
      <div class="flex flex-col">
        <div class="text-4xl mt-8 text-black text-center">
          A questa campagna partecipa anche:
        </div>
      </div>
      <div
        class="flex flex-col"
        v-for="otherCompany in companies"
        v-bind:key="otherCompany.id"
      >
        <img class="object-contain h-48 w-full" :src="otherCompany.logo" />
      </div>
    </div>
    <card-modal
      :showing="modalSubscribeShowing"
      @close="modalSubscribeShowing = false"
    >
      <form v-on:submit.prevent="onSubmit"
        action=""
        class="form flex flex-col bg-white p-6 relative lg:rounded-xl justify-center"
      >
        <h2 class="text-xl font-bold text-gray-900">
          Iscrizione alla campagna
        </h2>
        <div class="flex flex-row">
        <p>Quale azienda scegli per l'iscrizione</p>
        <select v-model="selectedCompany" @change="onChange($event)" required>
          <option disabled value="" >Seleziona un'azienda</option>
          <option
            v-for="company in companies"
            v-bind:value="company"
            v-bind:key="company.code"
          >
            {{ company.name }}
          </option>
        </select>
        </div>
        <label class="block">
          <span class="text-gray-700">Codice utente</span>
          <input
            required
            class="form-input mt-1 block w-full"
            placeholder="Codice"
            v-model="key"
          />
        </label>
        <div class="flex justify-center">
          <div class="p-5" x-data="{ active: false }">
            <button
              v-if="!campagna.userInCampaign"
              class="bg-blue-600 text-white px-4 py-2 text-sm uppercase tracking-wide font-bold rounded-lg"
              @click="confirm"
            >
              Iscriviti
            </button>
          </div>
          <div class="p-5" x-data="{ active: false }">
            <button
              class="bg-blue-600 text-white px-4 py-2 text-sm uppercase tracking-wide font-bold rounded-lg"
              @click="modalSubscribeShowing = false"
            >
              Chiudi
            </button>
          </div>
        </div>
      </form>
    </card-modal>
    <card-modal
      :showing="modalUnsubscribeShowing"
      @close="modalUnsubscribeShowing = false"
    >
      <h2 class="text-xl font-bold text-gray-900">Iscrizione alla campagna</h2>
      <p>Quale azienda scegli per la disiscrizione</p>
      <select v-model="selectedCompany" @change="onChange($event)" required>
        <option disabled value="">Seleziona un'azienda</option>
        <option
          v-for="company in companies"
          v-bind:value="company"
          v-bind:key="company.code"
        >
          {{ company.name }}
        </option>
      </select>
      <label class="block">
        <span class="text-gray-700">Codice utente</span>
        <input
          class="form-input mt-1 block w-full"
          placeholder="Codice"
          v-model="key"
        />
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
import EventBus from "../../communication/eventBus";

import CardModal from "../../Components/GenericModal.vue";
export default {
  name: "Campagna",
  components: {
    CardModal,
  },

  data() {
    return {
      companies: [],
      myCompany:{},
      selectedCompany: null,
      modalUnsubscribeShowing: false,
      modalSubscribeShowing: false,
      key: "",
    };
  },
  methods: {
    setMyCompany(company) {
      this.myCompany =company;
    },
    onChange(event) {
      console.log(event.target.value);
    },
    subscribe: function () {
      this.modalSubscribeShowing = true;
    },

    confirm: function () {
      if (this.key && this.selectedCompany)
        DataApi.subscribeCampaign(
          this.campagna.id,
          this.selectedCompany.code,
          this.key
        ).then(
          (res) => {
            //change campaign in store (subscribed)
            console.log(res);
            this.modalSubscribeShowing = false;
            this.campagna.userInCampaign = true;
            this.campagna.subscribedCompany=this.selectedCompany;
            this.$store.dispatch("storeCampagna", this.campagna)
             DataApi.getUser().then((res) => {
                this.$store.dispatch("storeUser", res.data)
            })
          },
          (err) => {
            console.log(err);
          }
        );
    },
    confirmLeave: function () {
      DataApi.unsubrscribeCampaign(
        this.campagna.id,
        this.selectedCompany.code,
        this.key
      ).then(
        (res) => {
          //change campaign in store (subscribed)
          console.log(res);
          this.modalUnsubscribeShowing = false;
          //todo change campaign in store
          //toast and subscribed
        },
        (err) => {
          console.log(err);
        }
      );
    },
  },
  created: function () {
    DataApi.getCompaniesOfCampaign(this.campagna.id).then(
      (res) => {
        this.companies = res.data;
        if (this.campagna.userInCampaign){
          this.setMyCompany(this.companies.find((x) => {
            return x.code == this.campagna.subscribedCompany.companyCode
            }))
        }
        console.log(this.campaigns);
      },
      (err) => {
        console.log(err);
      }
    );
    EventBus.$on("LEAVE_CAMPAIGN", () => {
      this.modalUnsubscribeShowing = true;
    });
  },
  computed: {

    campagna() {
      return this.$store.getters.campagna;
    },
    user() {
      return this.$store.getters.user;
    },
  },
};
</script>

<style></style>
