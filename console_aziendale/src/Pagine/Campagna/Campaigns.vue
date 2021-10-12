<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          v-if="
            role == 'ROLE_COMPANY_ADMIN' ||
            (role == 'ROLE_ADMIN' && adminCompany != null) ||
            (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
          "
          class="mr-4"
          x-large
          color="secondary"
          rounded
          elevation="6"
          @click="openModal({type:'campaignFormAdd', object:null})"
        >
          <v-icon left>mdi-plus</v-icon>
          AGGIUNGI
        </v-btn>

        <v-btn
          v-if="
            role == 'ROLE_COMPANY_ADMIN' ||
            (role == 'ROLE_ADMIN' && adminCompany != null) ||
            (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
          "
          x-large
          color="secondary"
          rounded
          elevation="6"
          @click="openModal({type:'associateForm', object:null})"
        >
          <v-icon left>mdi-wrench</v-icon>
          ASSOCIA / DISASSOCIA
        </v-btn>

      </v-col>
    </v-row>
    <v-row>
      <v-col :cols="nColsTable_calculator">
        <div v-if="allCampaigns && allCampaigns.items && allCampaigns.items.length > 0">
          <generic-table
            :items.sync="allCampaigns.items"
            :headers="headerColumns"
            :title="tableTitle"
            :method="showCampaignInfo"
          >
          </generic-table>
        </div>
        <div v-else class="empty-list">Non ci sono Campagne</div>
      </v-col>
      <!-- TODO: Profilo Campagna -->
      <profilo-campagna v-if="actualCampaign &&  actualCampaign.item" />
      <!-- TODO: Modale Campagna -->
      <modal v-show="editModalVisible">
        <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
          <campaign-form />
        </template>
        <template v-slot:footer>
          <v-btn
            text
            @click="closeModal"
            class="py-8 ml-8"
          >
            Annulla
          </v-btn>
          <v-btn
            color="primary"
            text
            @click="saveCampaign"
            class="py-8 ml-8"
          >
            Salva
          </v-btn>
        </template>
      </modal>
      <modal v-show="associateCampaignModalVisible">
        <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
          <associate-form />
        </template>
        <template v-slot:footer>
          <v-btn
            text
            @click="closeModal"
            class="py-8 ml-8"
          >
            Chiudi
          </v-btn>
        </template>
      </modal>
    </v-row>
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
      tableTitle: "Campagne",
      headerColumns: [{text:"Nome", value:"title"}, {text:"Inizio", value:"from"}, {text:"Fine", value:"to"}, {text:"Stato", value:"active"}],
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
  
  methods: {
    ...mapActions("modal", {openModal:"openModal"}),
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

  computed: {
    ...mapState("company", ["actualCompany", "adminCompany"]),
    ...mapState("campaign", ["allCampaigns", "actualCampaign"]),
    ...mapState("account", ["role"]),

    nColsTable_calculator: function() {
      if(this.actualCampaign){
        return 8;
      }else if(this.actualCampaign == null){
        return 12;
      }else{
        return 12;
      }
    },
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
