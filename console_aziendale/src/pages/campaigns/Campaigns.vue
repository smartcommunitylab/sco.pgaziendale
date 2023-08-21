<template>
  <div>
    <v-row>
      <v-col>
        <!-- <v-btn
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
        </v-btn> -->

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
            :items.sync="mappedCampaigns"
            :headers="headerColumns"
            :title="tableTitle"
            :method="showCampaignInfo"
          >
          </generic-table>
        </div>
        <div v-else class="empty-list">Non ci sono Campagne</div>
      </v-col>
      <!-- TODO: Profilo Campagna -->
      <v-col cols="5"> 
        <profilo-campagna v-if="actualCampaign &&  actualCampaign.item" />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ProfiloCampagna from "./Campaign.vue";
import GenericTable from "@/components/data-table/GenericTable.vue";
 
export default {
  components: { ProfiloCampagna, GenericTable },

  data: function () {
    return {
      tableTitle: "Campagne",
      headerColumns: [{text:"Nome", value:"title"}, {text:"Territorio", value:"territoryId"}, {text:"Inizio", value:"from"}, {text:"Fine", value:"to"}, {text:"Stato", value:"active"}],
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
    mappedCampaigns() {
      return this.allCampaigns && this.allCampaigns.items ? this.allCampaigns.items.map(c => {
        c.active = c.active ? 'Attiva' : 'Non attiva';
        return c;
      }) : [];
    },

    nColsTable_calculator() {
      if(this.actualCampaign){
        return 7;
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
