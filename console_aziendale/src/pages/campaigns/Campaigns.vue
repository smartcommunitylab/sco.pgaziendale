<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          v-if="
            !(role == 'ROLE_COMPANY_ADMIN' ||
            (role == 'ROLE_ADMIN' && adminCompany != null) ||
            (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null))
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
import ProfiloCampagna from "./Campaign.vue";
import Modal from "@/components/modal/ModalStructure.vue";
import GenericTable from "@/components/data-table/GenericTable.vue";
 
export default {
  components: { ProfiloCampagna, Modal, GenericTable },

  name: "GestioneCampagne",

  data: function () {
    return {
      tableTitle: "Campagne",
      headerColumns: [{text:"Nome", value:"title"}, {text:"Inizio", value:"from"}, {text:"Fine", value:"to"}, {text:"Stato", value:"active"}],
      currentCampaignSelected: undefined,
      popup: {
        title: "",
      },
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

    //IMPORTANTE PER LA TRADUZIONE
    getStatus: function (status) {
      let toRtn = "";
      if (status) {
        toRtn = "Attiva";
      } else {
        toRtn = "Non attiva";
      }

      return toRtn;
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
    this.changePage({ title: "Lista campagne", route: "/GestioneCampagne" });
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
