<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-3/6 mx-2 my-2 pb-16 relative">
      <div v-if="allCampaigns && allCampaigns.items && allCampaigns.items.length > 0">
        <generic-table class="text-center"
          :data.sync="allCampaigns.items"
          :columns="gridColumns"
          :header="headerColumns"
          :method="showCampaignInfo"
        >
        </generic-table>
      </div>

      <div v-else class="empty-list">Non ci sono campagne</div>
      <div class="ml-auto pt-4 pr-4 absolute right-0">
        <button
          @click="showModal('Aggiungi campagna')"
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <add-icon class="add-icon" />
        </button>
      </div>
    </div>
    <profilo-campagna v-if="actualCampaign &&  actualCampaign.item" />
        <div v-else class="select-element"> Seleziona una campagna per visualizzare i dettagli</div>
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
        <campaign-form />
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
    <modal v-show="associateCampaignModalVisible">
      <template v-slot:header> <div class="text-primary"> {{ popup.title }} </div> </template>
      <template v-slot:body>
        <associate-form />
      </template>
      <template v-slot:footer>

        <button
          type="button"
          class="mt-2 mb-2 btn-close text-xs font-medium mx-2 inline-block px-6 py-2 leading-6 text-center text-white transition bg-danger rounded ripple uppercase hover:bg-danger_light hover:shadow-lg focus:outline-none"
          @click="closeModal"
          aria-label="Close modal"
        >
          Chiudi
        </button>
      </template>
    </modal>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ProfiloCampagna from "./ProfiloCampaign.vue";
import EventBus from "@/components/eventBus";
import Modal from "@/components/Modal.vue";
import GenericTable from "@/components/GenericTable.vue";
import CampaignForm from "./CampaignForm.vue";
import AssociateForm from "./AssociateForm.vue";
 
export default {
  components: { ProfiloCampagna, Modal, GenericTable ,CampaignForm, AssociateForm},
  name: "GestioneCampagne",
  data: function () {
    return {
      gridColumns: ["title", "from", "to", "active"],
      headerColumns: ["Nome", "Inizio", "Fine", "Status"],
      associateCampaignModalVisible:false,
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
    ...mapState("campaign", ["allCampaigns", "actualCampaign"]),
    ...mapState("account", ["role"]),
  },
  watch: {
    adminCompany() {
    if (this.adminCompany && this.adminCompany.item) {
      this.getAllCampaigns(this.actualCompany.item.id);
    }
    },
  },
  mounted: function () {
    this.changePage({ title: "Lista campagne", route: "/gestionecampagne" });
    // console.log(this.adminCompany)
    // if (this.adminCompany && this.adminCompany.item) {
    //   this.getAllCampaigns(this.adminCompany.item.id);
    // }
    // console.log(this.adminCompany)
    if (this.adminCompany && this.adminCompany.item) {
      this.getAllCampaigns(this.actualCompany.item.id);
    }
    if (this.role == "ROLE_ADMIN" && this.adminCompany == null ) {
      this.getAllCampaigns(null);
    }
 EventBus.$on("EDIT_CAMPAIGN", (campaign) => {
      this.editModalVisible = true;
      EventBus.$emit("EDIT_CAMPAIGN_FORM", campaign.item);

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
    EventBus.$on("OK_CAMPAIGN_FORM", (campaign) => {
        if (this.newCampaign) {
          this.addCampaignCall({
            companyId: this.adminCompany ? this.actualCompany.item.id : null,
            campaign: campaign,
          });
        } else {
          this.updateCampaignCall({
            companyId: this.adminCompany ? this.actualCompany.item.id : null,
            campaign: campaign,
          });
        }
      this.editModalVisible = false;
      this.newCampaign = false;
    });
    EventBus.$on("NO_CAMPAIGN_FORM", () => {
      this.submitStatus = "ERROR";
    });
  },
  beforeDestroy() {
    EventBus.$off("NO_CAMPAIGN_FORM");
    EventBus.$off("OK_CAMPAIGN_FORM");
    EventBus.$off("DELETE_CAMPAIGN");
    EventBus.$off("EDIT_CAMPAIGN");
  },

  methods: {
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
      getAllCompaniesOfCampaignCall: "getAllCompaniesOfCampaign",
      addCampaignCall: "addCampaign",
      updateCampaignCall: "updateCampaign",
      deleteCampaignCall: "deleteCampaign",
    }),
    ...mapActions("navigation", { changePage: "changePage" }),

    showCampaignInfo: function (campaign) {
      if (this.currentCampaignSelected == campaign) {
        this.getAllCompaniesOfCampaignCall(null);

        this.currentCampaignSelected = undefined;
      } else {
        this.getAllCompaniesOfCampaignCall(campaign);
        this.currentCampaignSelected = campaign;
      }
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

      return moment(date).format("DD MM YYYY");
    },
    showModal(title) {
      if (this.role == 'ROLE_ADMIN' && !this.adminCompany)
      {
        this.editModalVisible = true;
        this.newCampaign = true;
        EventBus.$emit("NEW_CAMPAIGN_FORM");
      this.popup = {
        title: title,
      };
      }
      else {
        console.log('associa campagna ad azienda');
        this.popup = {
        title: "Campagne pubbliche",
      };
        this.associateCampaignModalVisible=true;
        EventBus.$emit("ASSOCIATE_CAMPAIGN_FORM");
      }
    },
    closeModal() {
      this.editModalVisible = false;
      this.associateCampaignModalVisible = false;
      this.newCampaign = false;
    },
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
    saveCampaign() {
      //check fields
      EventBus.$emit("CHECK_CAMPAIGN_FORM");
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
