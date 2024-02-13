<template>
  <div>
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
    ...mapState("account", ["user"]),
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
    if (this.adminCompany && this.adminCompany.item) {
      this.getAllCampaigns(this.actualCompany.item.id);
    } else {
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
