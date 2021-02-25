<template>
  <div class="flex flex-col lg:flex-row">
    <div
      class="lg:w-4/6 mx-2 my-2 flex flex-col bg-white"
      v-if="allCampaigns && allCampaigns.items && allCampaigns.items.length > 0"
    >
      <table class="table-auto rounded relative w-full">
        <thead class="text-center justify-between">
          <tr class="truncate px-2 flex border-b border-background text-center">
            <th class="w-1/6">Nome</th>

            <th class="w-1/6">Inizio</th>
            <th class="w-1/6">Fine</th>
            <th class="w-1/6">Regola</th>
            <th class="w-1/6">Status</th>
            <th class="w-1/6"></th>
          </tr>
        </thead>
        <tbody class="bg-white text-center justify-between">
          <template v-for="(campaign, index) in allCampaigns.items">
            <tr
              class="text-center m-auto truncate px-2 select-none cursor-pointer flex items-center border-b border-background hover:bg-background transition ease-in duration-100"
              :key="campaign.id"
              tag="tr"
              :class="campaign.active ? '' : 'bg-background'"
              @click="showCampaignInfo(campaign)"
            >
              <td class="w-1/6">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ campaign.title }}
                </p>
              </td>
              <td class="w-1/6">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ formatDate(campaign.startDate) }}
                </p>
              </td>
              <td class="w-1/6">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ formatDate(campaign.endDate) }}
                </p>
              </td>
              <td class="w-1/6">
                <p class="text-gray-800 text-sm font-semibold text-center truncate">
                  {{ getMeans(index) }}
                </p>
              </td>
              <td class="w-1/6">
                <p class="text-gray-800 text-sm font-semibold text-center truncate">
                  {{ getStatus(campaign.active) }}
                </p>
              </td>
              <td class="flex items-end w-1/6 pr-12">
                <dots-h-icon class="dots-icon" />
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
    <div v-else class="text-center">Non ci sono campagne</div>
    <div class="ml-auto pt-4 pr-4">
      <button
        @click="showModal('Aggiungi campagna')"
        class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
      >
        <add-icon class="add-icon" />
      </button>
    </div>
    <!-- <infobox /> -->
    <profilo-campagna />
    <modal v-show="deleteModalVisible">
      <template v-slot:header> Cancella Campagna </template>
      <template v-slot:body> Sei sicuro di voler cancellare la campagna? </template>
      <template v-slot:footer>
        <button
          type="button"
          class="btn-close"
          @click="deleteConfirm"
          aria-label="Close modal"
        >
          Conferma
        </button>
        <button
          type="button"
          class="btn-close"
          @click="closeDeleteModal"
          aria-label="Close modal"
        >
          Annulla
        </button>
      </template>
    </modal>
    <modal v-show="editModalVisible">
      <template v-slot:header> {{ popup.title }} </template>
      <template v-slot:body>
        <form action="" id="addCampaign">
          <div class="mb-4 flex flex-wrap justify-between">
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.campaign.logo.$error }"
              >
                <label class="field-label" for="first_name">Logo </label>
                <input
                  type="text"
                  name="campaignLogo"
                  placeholder="Logo *"
                  v-model.trim="$v.campaign.logo.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="campaignLogo"
                />
              </div>
              <div v-if="$v.campaign.logo.$error">
                <div class="error" v-if="!$v.campaign.logo.required">
                  Il campo Logo e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.campaign.title.$error }"
              >
                <label class="field-label" for="first_name">Titolo </label>
                <input
                  type="text"
                  name="campaignTitle"
                  placeholder="Titolo *"
                  v-model.trim="$v.campaign.title.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="campaignTitle"
                />
              </div>
              <div v-if="$v.campaign.title.$error">
                <div class="error" v-if="!$v.campaign.title.required">
                  Il campo titolo e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.campaign.description.$error }"
              >
                <label class="field-label" for="first_name">Descrizione</label>
                <input
                  type="text"
                  name="campaignDescription"
                  id="campaignDescription"
                  placeholder="Descrizione *"
                  v-model.trim="$v.campaign.description.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.campaign.description.$error">
                <div class="error" v-if="!$v.campaign.description.required">
                  Il campo descrizione e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.campaign.from.$error }"
              >
                <label class="field-label" for="password">Da </label>
                <input
                  type="text"
                  name="campaignFrom"
                  id=""
                  required
                  placeholder="Da *"
                  v-model.trim="$v.campaign.from.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.campaign.from.$error">
                <div class="error" v-if="!$v.campaign.from.required">
                  Il campo da e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.campaign.to.$error }"
              >
                <label class="field-label" for="password">A </label>
                <input
                  type="text"
                  name="campaignTo"
                  id=""
                  required
                  placeholder="A *"
                  v-model.trim="$v.campaign.to.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.campaign.to.$error">
                <div class="error" v-if="!$v.campaign.to.required">
                  Il campo a e' richiesto.
                </div>
              </div>
            </div>
          </div>
        </form>
      </template>
      <template v-slot:footer>
        <button
          type="button"
          class="btn-close"
          @click="saveCampaign"
          aria-label="Close modal"
        >
          Salva
        </button>
        <button
          type="button"
          class="btn-close"
          @click="closeModal"
          aria-label="Close modal"
        >
          Annulla
        </button>
        <p class="typo__p" v-if="submitStatus === 'ERROR'">
          Riempire i dati nel modo corretto
        </p>
      </template>
    </modal>
  </div>
</template>

<script>
// import Infobox from "../components/Infobox.vue";
// import { campaigns } from "../tmp-data/campaigns.js";
import { mapState, mapActions } from "vuex";
import ProfiloCampagna from "../components/ProfiloCampagna.vue";
import EventBus from "../components/eventBus";
import { required } from "vuelidate/lib/validators";
import Modal from "../components/Modal.vue";

export default {
  components: { ProfiloCampagna, Modal },
  name: "GestioneCampagne",
  data: function () {
    return {
      editModalVisible: false,
      deleteModalVisible: false,
      currentCampaignSelected: undefined,
      popup: {
        title: "",
      },
      submitStatus: null,
    };
  },
  computed: {
    ...mapState("company", ["actualCompany", "adminCompany"]),
    ...mapState("campaign", ["allCampaigns"]),
  },
  mounted: function () {
    // this.campaigns = campaigns;
    if (this.adminCompany) this.getAllCampaigns(this.adminCompany.item.id);
    if (this.actualCompany) this.getAllCampaigns(this.actualCompany.item.id);
    if (!this.adminCompany && !this.actualCompany) this.getAllCampaigns(null);
    EventBus.$on("EDIT_CAMPAIGN", (campaign) => {
      this.editModalVisible = true;
      this.campaign = campaign.item;
      this.popup = {
        title: "Modifica",
      };
    });
    EventBus.$on("DELETE_CAMPAIGN", (campaign) => {
      this.deleteModalVisible = true;
      this.campaign = campaign.item;
      this.popup = {
        title: "Cancella",
      };
    });
  },

  validations: {
    campaign: {
      logo: {
        required,
      },
      title: {
        required,
      },
      description: {
        required,
      },
      from: {
        required,
      },
      to: {
        required,
      },
      rules: {
        required,
      },
      privacy: {
        required,
      },
      means: {
        required,
      },
    },
  },
  methods: {
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
      getCampaignCall: "getCampaign",
      addCampaignCall: "addCampaign",
      updateCampaignCall: "updateCampaign",
      deleteCampaignCall: "deleteCampaign",
    }),
    showCampaignInfo: function (campaign) {
      if (this.currentCampaignSelected == campaign) {
        this.getCampaignCall(null);

        this.currentCampaignSelected = undefined;
      } else {
        this.getCampaignCall(campaign);
        this.currentCampaignSelected = campaign;
      }
    },
    getMeans: function (index) {
      let toRtn = "";

      this.allCampaigns.items[index]["means"].map((el) => {
        toRtn += el + " - ";
      });
      return toRtn.slice(0, -3);
    },
    getStatus: function (status) {
      let toRtn = "";
      if (status) {
        toRtn = "Attiva";
      } else {
        toRtn = "Non attiva";
      }

      return toRtn;
    },

    formatDate: function (date) {
      const moment = require("moment");

      moment.locale("it");

      return moment(date).format("DD MMMM YYYY");
    },
    showModal(title) {
      this.editModalVisible = true;
      this.newEmployee = true;
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.editModalVisible = false;
      this.newCampaign = false;
    },
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
    saveCampaign() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      // console.log("submit!");
      this.$v.$touch();
      if (this.$v.$invalid) {
        this.submitStatus = "ERROR";
        return;
      } else {
        if (this.newCampaign) {
          this.addCampaignCall({ campaign: this.campaign });
        } else {
          this.updateCampaignCall({ campaign: this.campaign });
        }
      }
      this.editModalVisible = false;
      this.newCampaign = false;
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteCampaignCall({
        companyId: this.actualCompany.item.id,
        employeeId: this.actualCampaign.item.id,
      });
    },
  },
};
</script>

<style>
.dots-icon {
  width: 40px;
}
.dots-icon svg {
  width: 100%;
  height: 100%;
}
</style>
