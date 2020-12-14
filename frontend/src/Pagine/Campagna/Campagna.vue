<template>
  <div class="capagna flex flex-col">
    <div class="flex flex-col ml-8 mt-8">
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
      <h2 class="text-xl font-bold text-gray-900">
        Iscrizione alla campagna
      </h2>

      <form
        name="sub"
        action=""
        v-on:submit.prevent="onSubmit"
        class="bg-white form flex flex-col p-6 relative lg:rounded-xl justify-center "
      >
        <div
          class="flex flex-col md:flex-row  mt-3 justify-stretch lg:flex-col"
        >
          <label for="sub_select"
            >Quale azienda scegli per la iscrizione</label
          >
          <select
            class="focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none"
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
            class="focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none"
            placeholder="Codice"
            v-model="key"
            required
          />
          <div class="flex items-baseline space-x-2 mt-2">
            <input
              type="checkbox"
              name="policy"
              id=""
              required
              class="inline-block"
            />
            <label @click="showPolicy" for="policy">
              <span
                class="font-bold underline pointer-events-auto cursor-pointer"
                >Policy*</span
              >
              <p v-show="show_policy" class="text-gray-600 text-xs ">
                Ai sensi dell'art. 13 del Regolamento EU n. 2016/679 (GDPR), i
                dati personali forniti saranno trattati per poter dare riscontro
                alla sua richiesta tramite strumenti manuali, informatici e
                telematici, comunque ideonei a garantire la sicurezza e la
                riservatezza dei dati stessi. L'informativa Privacy completa è
                disponibile al seguente link {LINK}. Dichiaro di aver letto e
                compreso l'informativa sul trattamento dei dati personali.
              </p>
            </label>
          </div>
          <div class="flex items-baseline space-x-2 mt-2">
            <input
              type="checkbox"
              name="regolamento"
              id=""
              required
              class="inline-block"
            />
            <label for="regolamento">
              <span class="font-bold underline">Regolamento*</span>
            </label>
          </div>
          <button
            class="mt-6 bg-primary hover:bg-blue-500 text-white font-semibold p-3  flex-1"
            @click="confirm"
          >
            Conferma
          </button>
          <button
            class="mt-6 bg-primary hover:bg-blue-500 text-white font-semibold p-3  flex-1"
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
// import EventBus from "../../communication/eventBus";

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
      selectedCompany: null,
      // modalUnsubscribeShowing: false,
      modalSubscribeShowing: false,
      key: "",
      show_policy: false,
    };
  },
  methods: {
    setMyCompany(company) {
      this.myCompany = company;
    },
    onChange(event) {
      console.log(event.target.value);
    },
    subscribe: function() {
      this.modalSubscribeShowing = true;
    },
    showPolicy: function() {
      this.show_policy = !this.show_policy;
    },

    confirm: function() {
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
            this.campagna.subscribedCompany = this.selectedCompany;
            this.$store.dispatch("storeCampagna", this.campagna);
            DataApi.getUser().then((res) => {
              this.$store.dispatch("storeUser", res.data);
            });
          },
          (err) => {
            console.log(err);
          }
        );
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
  created: function() {
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
      }
    );
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

<style></style>
