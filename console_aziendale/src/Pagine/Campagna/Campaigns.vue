<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2 pb-16 relative">
      <div v-if="allCampaigns && allCampaigns.items && allCampaigns.items.length > 0">
        <generic-table
          :data="allCampaigns.items"
          :columns="gridColumns"
          :header="headerColumns"
          :method="showCampaignInfo"
        >
        </generic-table>
      </div>

      <div v-else class="text-center">Non ci sono campagne</div>
      <div class="ml-auto pt-4 pr-4 absolute right-0">
        <button
          @click="showModal('Aggiungi campagna')"
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <add-icon class="add-icon" />
        </button>
      </div>
    </div>
    <!-- <infobox /> -->
    <profilo-campagna v-if="actualCampaign" />
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
              <div class="form-group" :class="{ 'form-group--error': $v.logo.$error }">
                <label class="field-label" for="first_name">Logo </label>
                <input
                  type="text"
                  name="campaignLogo"
                  placeholder="Logo *"
                  v-model.trim="$v.logo.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="campaignLogo"
                />
              </div>
              <div v-if="$v.logo.$error">
                <div class="error" v-if="!$v.logo.required">
                  Il campo Logo e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.title.$error }">
                <label class="field-label" for="first_name">Titolo </label>
                <input
                  type="text"
                  name="campaignTitle"
                  placeholder="Titolo *"
                  v-model.trim="$v.title.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="campaignTitle"
                />
              </div>
              <div v-if="$v.title.$error">
                <div class="error" v-if="!$v.title.required">
                  Il campo titolo e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.description.$error }"
              >
                <label class="field-label" for="first_name">Descrizione</label>
                <input
                  type="text"
                  name="campaignDescription"
                  id="campaignDescription"
                  placeholder="Descrizione *"
                  v-model.trim="$v.description.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.description.$error">
                <div class="error" v-if="!$v.description.required">
                  Il campo descrizione e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.from.$error }">
                <label class="field-label" for="password">Da </label>
                <input
                  type="text"
                  name="campaignFrom"
                  id=""
                  required
                  placeholder="Da *"
                  v-model.trim="$v.from.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.from.$error">
                <div class="error" v-if="!$v.from.required">
                  Il campo da e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.to.$error }">
                <label class="field-label" for="password">A </label>
                <input
                  type="text"
                  name="campaignTo"
                  id=""
                  required
                  placeholder="A *"
                  v-model.trim="$v.to.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.to.$error">
                <div class="error" v-if="!$v.to.required">Il campo a e' richiesto.</div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.rules.$error }">
                <label class="field-label" for="password">Regole</label>
                <input
                  type="text"
                  name="campaignRules"
                  id=""
                  required
                  placeholder="Regole *"
                  v-model.trim="$v.rules.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.rules.$error">
                <div class="error" v-if="!$v.rules.required">
                  Il campo privacy e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.privacy.$error }">
                <label class="field-label" for="password">Privacy</label>
                <input
                  type="text"
                  name="campaignPrivacy"
                  id=""
                  required
                  placeholder="Privacy *"
                  v-model.trim="$v.privacy.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.privacy.$error">
                <div class="error" v-if="!$v.privacy.required">
                  Il campo privacy e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.means.$error }">
                <label class="field-label" for="password">Mezzi</label>
                <input
                  type="text"
                  name="campaignMeans"
                  id=""
                  required
                  placeholder="Mezzi *"
                  v-model.trim="$v.means.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.means.$error">
                <div class="error" v-if="!$v.means.required">
                  Il campo Mezzi e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.active.$error }">
                <label class="field-label" for="password">Attiva</label>
                <input
                  type="checkbox"
                  name="campaignActive"
                  id=""
                  required
                  placeholder="Attiva *"
                  v-model.trim="$v.active.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.active.$error">
                <div class="error" v-if="!$v.active.required">
                  Il campo Attiva e' richiesto.
                </div>
              </div>
            </div>
                        <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.application.$error }">
                <label class="field-label" for="password">Applicazione</label>
                <input
                  type="text"
                  name="campaignApplication"
                  id=""
                  required
                  placeholder="Applicazione *"
                  v-model.trim="$v.application.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.application.$error">
                <div class="error" v-if="!$v.application.required">
                  Il campo Applicazione e' richiesto.
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
import { mapState, mapActions } from "vuex";
import ProfiloCampagna from "./ProfiloCampaign.vue";
import EventBus from "@/components/eventBus";
import { required } from "vuelidate/lib/validators";
import Modal from "@/components/Modal.vue";
import GenericTable from "@/components/GenericTable.vue";
export default {
  components: { ProfiloCampagna, Modal, GenericTable },
  name: "GestioneCampagne",
  data: function () {
    return {
      gridColumns: ["title", "from", "to", "active"],
      headerColumns: ["Nome", "Inizio", "Fine", "Status"],
      editModalVisible: false,
      deleteModalVisible: false,
      currentCampaignSelected: undefined,
      id:"",
      logo: "",
      title: "",
      description: "",
      from: "",
      to: "",
      rules: "",
      privacy: "",
      means: [],
      active:false,
      application:"",
      popup: {
        title: "",
      },
      submitStatus: null,
    };
  },
  validations: {
    id: {
      required
    },
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
    active: {
      required
    },
    application: {
      required
    }
  },
  computed: {
    ...mapState("company", ["actualCompany", "adminCompany"]),
    ...mapState("campaign", ["allCampaigns", "actualCampaign"]),
  },
  mounted: function () {
    this.changePage({ title: "Lista campagne", route: "/gestionecampagne" });
    // this.campaigns = campaigns;
    if (this.adminCompany) {
      this.getAllCampaigns(this.adminCompany.item.id);
    }
    if (this.actualCompany) {
      this.getAllCampaigns(this.actualCompany.item.id);
    }
    if (!this.adminCompany && !this.actualCompany) {
      this.getAllCampaigns(null);
      }
    EventBus.$on("EDIT_CAMPAIGN", (campaign) => {
      this.editModalVisible = true;
      this.campaign = campaign.item;
      this.copyFormValues();
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

  methods: {
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
      getCampaignCall: "getCampaign",
      addCampaignCall: "addCampaign",
      updateCampaignCall: "updateCampaign",
      deleteCampaignCall: "deleteCampaign",
    }),
    ...mapActions("navigation", { changePage: "changePage" }),

    showCampaignInfo: function (campaign) {
      if (this.currentCampaignSelected == campaign) {
        this.getCampaignCall(null);

        this.currentCampaignSelected = undefined;
      } else {
        this.getCampaignCall(campaign);
        this.currentCampaignSelected = campaign;
      }
    },
    // getMeans: function (index) {
    //   let toRtn = "";

    //   this.allCampaigns.items[index]["means"].map((el) => {
    //     toRtn += el + " - ";
    //   });
    //   return toRtn.slice(0, -3);
    // },
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

      return moment(date).format("DD MM YYYY");
    },
    showModal(title) {
      this.editModalVisible = true;
      this.newCampaign = true;
      this.campaign = {};
      this.newValues();
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.editModalVisible = false;
      this.newCampaign = false;
      this.$v.$reset();
    },
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
    initCampaign() {
      this.campaign={};
      this.id="";
        this.logo="";
      this.title="";
      this.description="";
      this.from="";
      this.to="";
      this.rules="";
      this.privacy="";
      this.means=[]
      this.active=false;
      this.application="";
      
  },
    copyFormValues() {
      for (const [key] of Object.entries(this.campaign)) {
        this[key]=this.campaign[key]}
},
    createCampaign() {
      this.campaign = {
        id:this.id,
        logo: this.logo,
        title: this.title,
        description: this.description,
        from: this.from,
        to: this.to,
        rules: this.rules,
        privacy: this.privacy,
        means: [],
        active:this.active,
        application:this.application
      };
    },

    saveCampaign() {
      //check fields
      this.$v.$touch();
      if (this.$v.$invalid) {
        this.submitStatus = "ERROR";
        return;
      } else {
        this.createCampaign();
        this.submitStatus = "SUCCESS";
        if (this.newCampaign) {
          this.addCampaignCall({
            companyId: this.adminCompan ? this.actualCompany.item.id : null,
            campaign: this.campaign,
          });
        } else {
          this.updateCampaignCall({
            companyId: this.adminCompan ? this.actualCompany.item.id : null,
            campaign: this.campaign,
          });
        }
        this.$v.$reset();
      }
      this.editModalVisible = false;
      this.newCampaign = false;
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteCampaignCall({
        companyId: this.adminCompan ? this.actualCompany.item.id : null,
        campaignId: this.actualCampaign.item.id,
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
