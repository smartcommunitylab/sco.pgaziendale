<template>
  <div class="capagna flex flex-col" v-if="campagna">
    <div
      :class="campagna.userInCampaign ? 'flex flex-row' : 'flex flex-col'"
      class="align-middle justify-center pt-4 flex flex-col"
    >
      <img
        v-if="campagna.logo"
        class="object-contain h-48 w-2/3 m-auto"
        :src="campagna.logo"
      />

      <div v-if="campagna.userInCampaign" class="w-2/6 mx-auto">
        <div class="">
          <div class="text-sm text-black text-center pb-4">Iscritto con</div>
        </div>
        <div>
          <img class="object-contain m-auto" :src="myCompany.logo" />
        </div>
      </div>
    </div>
    <div v-if="companies && companies.length && !campagna.userInCampaign">
      <div class="flex flex-col pt-8">
        <h2 class="text-xl pl-2 pb-2 text-center">A questa campagna partecipano</h2>
      </div>
      <div
        class="flex flex-row overflow-x-auto pb-4"
        :class="companies.length >= 3 ? 'justify-start' : 'justify-center'"
      >
        <div v-for="company in companies" v-bind:key="company.id" class="flex-shrink-0">
          <img class="object-contain h-40 w-full mx-2" :src="company.logo" />
        </div>
      </div>
    </div>
    <div
      v-if="campagna.userInCampaign"
      class="flex flex-col py-4 pt-0 justify-center align-middle"
    >
      <h2 class="text-2xl pl-2 pb-2 text-center md:text-3xl md:pt-5">
        Il tuo ultimo mese
      </h2>
      <div class="flex flex-col lg:flex-row">
        <div
          class="flex-none bg-primary text-white pl-20 sm:pl-48 m-2 sm:pt-2 text-left md:text-center md:p-0 md:my-2 md:w-3/4 lg:w-1/4 md:m-auto rounded-md md:shadow-lg lg:m-auto"
        >
          <p class="font-semibold text-6xl -mb-4">
            {{ (total_distance / 1000).toFixed(2) }}
          </p>
          <p class="font-semibold text-lg pb-2">km percorsi</p>
        </div>
        <div
          class="flex-none bg-secondary text-white pr-20 sm:pr-48 m-2 sm:pt-2 text-right md:text-center md:p-0 md:my-2 md:w-3/4 lg:w-1/4 md:m-auto rounded-md md:shadow-lg lg:m-auto"
        >
          <p class="font-semibold text-6xl -mb-4">{{ (co2_saved).toFixed(2) }}g</p>
          <p class="font-semibold text-lg pb-2">CO2 salvata</p>
        </div>
        <div
          class="flex-none bg-primary text-white pl-20 sm:pl-48 m-2 sm:pt-2 text-left md:text-center md:p-0 md:my-2 md:w-3/4 lg:w-1/4 md:m-auto rounded-md md:shadow-lg lg:m-auto"
        >
          <p class="font-semibold text-6xl -mb-4">{{ totalTracks }}</p>
          <p class="font-semibold text-lg pb-2">viaggi validi</p>
        </div>
      </div>

      <router-link
        :to="{ name: 'myperformance', params: { id: campagna.id } }"
        tag="button"
        type="button"
        class="text-center m-auto pt-2 text-primary rounded-md my-1 inline-flex items-center bg-transparent font-semibold py-1 px-2 lg:hidden"
      >
        <performance-icon class="pr-1" />Mostra dettagli
      </router-link>
    </div>
    <div v-if="!campagna.userInCampaign" class="flex">
      <button
        @click="subscribe"
        class="text-center mx-auto pt-2 text-white bg-primary w-full items-center bg-transparent font-semibold py-2 px-2 md:w-3/5 md:mx-auto md:my-10 lg:w-1/6"
      >
        Iscriviti
      </button>
    </div>
    <div class="px-2 flex flex-col">
      <h2 class="text-2xl pb-2 text-center md:text-3xl md:pt-5">
        Riguardo {{ campagna.title }}
      </h2>
      <p class="pr-2 break-words md:text-lg md:px-16 lg:px-48 text-center">
        {{ campagna.description }}
      </p>
      <router-link
        :to="{ name: 'rules', params: { id: campagna.id } }"
        tag="button"
        class="lg:hidden text-center m-auto pt-2 text-primary rounded-md my-1 inline-flex items-center bg-transparent font-semibold py-1 px-2"
      >
        <rules-icon class="pr-1" /> Leggi il regolamento
      </router-link>
    </div>
    <div class="px-12 py-4">
      <p class="text-xs md:px-20 lg:px-56">
        Ai sensi dell’art. 13 del Regolamento EU n.2016/679 (GDPR), i dati personali
        forniti saranno trattati per poter dare riscontro alla sua richiesta tramite
        strumenti manuali, informatici e telematici, comunque idonei a garantire la
        sicurezza e la riservatezza dei dati stessi. L’informativa Privacy completa è
        disponibile al seguente link
      </p>
    </div>

    <div v-if="companies && companies.length && campagna.userInCampaign">
      <div class="flex flex-col pt-8">
        <h2 class="text-xl pl-2 pb-2 text-center">A questa campagna partecipano</h2>
      </div>
      <div
        class="flex flex-row overflow-x-auto pb-4"
        :class="
          companies.length >= 3 ? 'justify-start lg:justify-center' : 'justify-center'
        "
      >
        <div v-for="company in companies" v-bind:key="company.id" class="flex-shrink-0">
          <img class="object-contain h-40 w-full mx-2" :src="company.logo" />
        </div>
      </div>
    </div>
    <div v-if="campagna.userInCampaign" class="flex lg:hidden">
      <button
        @click="leaveCampaign"
        class="text-center mx-auto pt-2 text-white bg-danger w-full items-center bg-transparent font-semibold py-2 px-2 md:w-3/5 md:mx-auto md:my-10"
      >
        Abbandona Campagna
      </button>
    </div>
    <!--<div class="flex flex-col ml-8 mt-8">
      <div class="text-4xl mt-8 text-black text-center">
        Campagna: {{ campagna.title }}
      </div>
    </div>
    <div class="flex flex-col">
      <img class="object-none h-48 w-full" :src="campagna.logo" />
    </div>
    <div v-if="companies && companies.length && !campagna.userInCampaign">
      <div class="flex flex-col">
        <div class="text-4xl mt-8 text-black text-center">Organizzata da</div>
      </div>
      <div class="flex flex-col" v-for="company in companies" v-bind:key="company.id">
        <img class="object-contain h-48 w-full" :src="company.logo" />
      </div>
    </div>
    <div v-if="campagna.userInCampaign">
      <div class="flex flex-col">
        <div class="text-4xl mt-8 text-black text-center">Iscritto con</div>
      </div>
      <div class="flex flex-col">
        <img class="object-contain h-48 w-full" :src="myCompany.logo" />
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
    </div>-->

    <card-modal :showing="modalSubscribeShowing" @close="modalSubscribeShowing = false">
      <h2 class="text-xl font-bold text-gray-900">Iscrizione alla campagna</h2>

      <form
        name="sub"
        action=""
        v-on:submit.prevent=""
        class="bg-white form flex flex-col p-6 relative lg:rounded-xl justify-center"
      >
        <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col">
          <label for="sub_select">Quale azienda scegli per la iscrizione</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none"
            name="sub_select"
            id="cars"
            form="send_request"
            v-model="selectedCompany"
            @change="onChange($event)"
            required
          >
            <option disabled value="">Seleziona un'azienda</option>
            <option
              v-for="company in companies"
              v-bind:value="company"
              v-bind:key="company.code"
            >
              {{ company.name }}
            </option>
          </select>
          <label for="code">Codice Utente</label>
          <input
            name="code"
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none"
            placeholder="Codice"
            v-model="key"
            required
          />
          <div class="flex items-baseline space-x-2 mt-2">
            <input
              type="checkbox"
              name="policy"
              v-model="policy"
              id=""
              required
              class="inline-block check-campaign"
            />
            <label @click="showPolicy" for="policy">
              <span class="font-bold underline pointer-events-auto cursor-pointer"
                >Privacy*</span
              >
              <p v-show="show_policy" class="text-gray-600 text-xs">
                Ho letto e compreso
                <router-link :to="{ name: 'privacy', params: { id: campagna.id } }">
<a href="" class="underline text-primary"
                  >l'informativa sul trattamento dei dati personali</a></router-link>.   
                , acconsento al trattamento dei miei dati personali come in essa indicato
                e sono consapevole che i dati relativi ai miei spostamenti casa-lavoro
                saranno comunicati mensilmente in forma aggregata al mio datore di lavoro
              </p>
            </label>
          </div>
          <div class="flex items-baseline space-x-2 mt-2">
            <input
              type="checkbox"
              name="regolamento"
              id=""
              required
              class="inline-block check-campaign"
              v-model="regolamento"
            />

            <label @click="showRegolamento" for="regolamento">
              <span class="font-bold underline pointer-events-auto cursor-pointer"
                >Regolamento*</span
              >
              <p v-show="show_regolamento" class="text-gray-600 text-xs">
                Dichiaro di aver preso visione e di accettare integralmente il           <router-link :to="{ name: 'rules', params: { id: campagna.id } }">
<a href="" class="underline text-primary"
                  >Regolamento
                della campagna {{this.campagna.title}}</a></router-link>.   
              </p>
            </label>
          </div>
          <button
            class="mt-6 bg-primary hover:bg-blue-500 text-white font-semibold p-3 flex-1"
            @click="confirm"
          >
            Conferma
          </button>
          <button
            class="mt-6 bg-primary hover:bg-blue-500 text-white font-semibold p-3 flex-1"
            @click="modalSubscribeShowing = false"
          >
            Chiudi
          </button>
        </div>
      </form>
    </card-modal>
  </div>
</template>

<script>
import DataApi from "../../communication/dataApi";
import EventBus from "../../communication/eventBus";
// import EventBus from "../../communication/eventBus";

import { MOMENT_DATE_FORMAT } from "../../variables";

import CardModal from "../../Components/GenericModal.vue";
export default {
  name: "Campagna",
  components: {
    CardModal,
  },

  data() {
    return {
      companies: [],
      myCompany: {},
      policy: false,
      regolamento: false,
      selectedCompany: null,
      // modalUnsubscribeShowing: false,
      modalSubscribeShowing: false,
      key: "",
      show_policy: true,
      show_regolamento: true,
      totalTracks: 0,
      co2_saved: 0,
      total_distance: 0,
    };
  },
  methods: {
    leaveCampaign: function () {
      EventBus.$emit("LEAVE_CAMPAIGN");
    },
    setMyCompany(company) {
      if (company) this.myCompany = company;
    },
    onChange(event) {
      console.log(event.target.value);
    },
    subscribe: function () {
      this.modalSubscribeShowing = true;
    },
    showPolicy: function () {
      this.show_policy = !this.show_policy;
    },
showRegolamento:function() {
        this.show_regolamento = !this.show_regolamento;

},
    confirm: function () {
      let loader = this.$loading.show({
        canCancel: false,
        backgroundColor: "#000",
        color: "#fff",
      });
      if (this.key && this.selectedCompany && this.regolamento && this.policy) {
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
            this.campagna.subscribedCompany = this.selectedCompany;
            this.$store.dispatch("storeCampagna", this.campagna);
            DataApi.getUser().then(
              (res) => {
                this.$store.dispatch("storeUser", res.data);
                //add iscritto con
                console.log(JSON.stringify(this.campagna));
                console.log(JSON.stringify(this.companies));
                this.setMyCompany(
                  this.companies.find((x) => {
                    return x.code == this.campagna.subscribedCompany.code;
                  })
                );
              },
              (err) => {
                EventBus.$emit("snack-open");
                console.log(err);
                loader.hide();
              }
            );
            loader.hide();
            EventBus.$emit(
              "snack-open",
              "Iscrizione Effettuata",
              "Ti sei iscritto con successo alla campagna " + this.campagna.title,
              0
            );
          },
          (err) => {
            EventBus.$emit("snack-open");
            console.log(err);
            loader.hide();
          }
        );
      } else {
        loader.hide();
      }
    },
    // confirmLeave: function() {
    //   DataApi.unsubrscribeCampaign(
    //     this.campagna.id

    //   ).then(
    //     (res) => {
    //       //change campaign in store (subscribed)
    //       console.log(res);
    //       this.modalUnsubscribeShowing = false;
    //       //todo change campaign in store
    //       //toast and subscribed
    //     },
    //     (err) => {
    //       console.log(err);
    //     }
    //   );
    // },
  },
  created: function () {
    this.$store.dispatch("storePage", { title: "Campagna", back: false });
    let loader = this.$loading.show({
      canCancel: false,
      backgroundColor: "#000",
      color: "#fff",
    });
    DataApi.getCompaniesOfCampaign(this.campagna.id).then(
      (res) => {
        this.companies = res.data;
        if (this.campagna.userInCampaign) {
          this.setMyCompany(
            this.companies.find((x) => {
              return x.code == this.campagna.subscribedCompany.companyCode;
            })
          );
        }
        console.log(this.campaigns);
      },
      (err) => {
        console.log(err);
        EventBus.$emit("snack-open");
      }
    );
    const moment = require("moment");
    let from = moment().subtract(30, "d").format(MOMENT_DATE_FORMAT);
    let to = moment().format(MOMENT_DATE_FORMAT);

    DataApi.getStats(this.campagna.id, from, to, "month", false).then((res) => {
      if (res.data.length != 0) {
        console.log(res.data);
        this.totalTracks = Object.keys(res.data[0].distances).map((key) => {
          this.total_distance += res.data[0].distances[key];
        });
        this.co2_saved = res.data[0].co2saved;
        this.totalTracks = res.data[0].trackCount;
      }
    });

    loader.hide();
    // EventBus.$on("LEAVE_CAMPAIGN", () => {
    //   this.modalUnsubscribeShowing = true;
    // });
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

<style scope>
.check-campaign{
  min-width: 15px;
}
</style>
