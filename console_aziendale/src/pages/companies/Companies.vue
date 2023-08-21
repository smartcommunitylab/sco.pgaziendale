<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          x-large
          color="secondary"
          rounded
          elevation="6"
          @click="openModal({type:'aziendaFormAdd', object:null})"
        >
          <v-icon left>mdi-plus</v-icon>
          AGGIUNGI
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col :cols="nColsTable_calculator">
        <div v-if="companyList && companyList.length > 0">
          <v-card>
            <v-card-title>
              {{tableTitle}}
              <v-spacer></v-spacer>
              <v-text-field
                v-model="search"
                append-icon="mdi-magnify"
                label="Cerca"
                single-line
                hide-details
              ></v-text-field>
              <v-select
                v-model="selectedTerritory"
                :items="territoryList"
                label="Territorio"
                item-text="name.it"
                item-value="territoryId"
                hide-details
              ></v-select>
              <v-select
                v-model="selectedCampaign"
                :items="campaignList"
                label="Campagna"
                item-text="title"
                item-value="id"
                hide-details
              ></v-select>
            </v-card-title>
            <v-data-table
              class="row-pointer elevation"
              :headers="headerColumns"
              :items="filteredList"
              :search="search"
              @click:row="showCompanyInfo"
              :header-props="{'sortByText': 'Ordina per'}"
              :footer-props="{
                'items-per-page-text':'righe per pagina',
                pageText: '{0}-{1} di {2}'
              }"
              no-results-text="La ricerca non ha dato risultati"
              no-data-text="Non ci sono dati inseriti"
            >
            <template v-slot:item.state="{ item }">
              {{ item.state ? 'Si' : 'No' }}
            </template>
            </v-data-table>
          </v-card>

          <!-- <generic-table
            :items="companyList"
            :headers="headerColumns"
            :title="tableTitle"
            :method="showCompanyInfo"
          >
          </generic-table> -->
        </div>
        <div v-else class="empty-list">Non ci sono Aziende</div>
      </v-col>
      <v-col cols="5">
        <profilo-azienda v-if="actualCompany"></profilo-azienda>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import ProfiloAzienda from "./Company.vue";
import { mapState, mapActions } from "vuex";

export default {
  components: { ProfiloAzienda },

  name: "Aziende",

  data: function () {
    return {
      tableTitle: "Aziende",
      headerColumns: [{text:"Nome", value:"name"}, {text:"Codice Azienda", value:"code"}, {text:"Territorio", value: "territoryName"}, {text: "Campagne", value: "campaignNames"}, {text: 'Verificato', value: "state"}, {text:"Indirizzo", value:"address"}],
      editModalVisible: false,
      deleteModalVisible: false,
      currentCompanySelected: undefined,
      companyList: null,
      territoryList: [],
      selectedTerritory: "",
      campaignList: [],
      selectedCampaign: "",
      search: "",
      popup: {
        title: "",
      },
      submitStatus: null,
    };
  },
  
  methods: {
    ...mapActions("company", {
      getAllCompanies: "getAll",
      addCompanyCall: "addCompany",
      updateCompanyCall: "updateCompany",
      getCompanyById: "getCompanyById",
      deleteCompany: "deleteCompany",
    }),
    ...mapActions("campaign", {getTerritories:"getTerritories", getAllCampaigns: "getAll"}),
    ...mapActions("navigation", { changePage: "changePage" }),
    showModal(title) {
      this.nColsTable = 8;
      this.editModalVisible = true;
      this.newCompany = true;
      this.popup = {
        title: title,
      };
    },
    ...mapActions("modal", { openModal:"openModal"}),

    closeModal() {
      this.editModalVisible = false;
      this.newCompany = false;
    },
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
    saveCompany() {
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteCompany(this.company);
    },  
    showCompanyInfo(company) {
      if (this.currentCompanySelected == company) {
        this.getCompanyById(null);

        this.currentCompanySelected = undefined;
      } else {
        this.getCompanyById(company.id);
        this.currentCompanySelected = company;
      }
    },
    updateList() {
      if (this.territories && this.territories.items && this.companyList && this.allCampaigns && this.allCampaigns.items) {
        this.companyList.forEach(c => {
          c.territoryName = (this.territories.items.find(t => t.territoryId === c.territoryId) || {name: {it: c.territoryId}}).name.it;
          c.campaignNames = this.allCampaigns.items.filter(cm => c.campaigns.indexOf(cm.id) >= 0).map(cm => cm.title).join(', ');
        });
      }
    }
  },
  watch: {
    allCompanies(list) {
      this.companyList = list.items;
      this.updateList();
    },
    territories() {
      this.updateList();
      let list = this.territories.items;
      if (list) {
        list.splice(0, 0, {territoryId: '', name: {'it': 'Tutti'}});
      }
      this.territoryList = list;
    },
    allCampaigns() {
      this.updateList();
      let list = this.allCampaigns.items;
      if (list) {
        list.splice(0, 0, {id: '', title: 'Tutte'});
      }
      this.campaignList = list;
    },
  },

  computed: {
    ...mapState("company", ["allCompanies", "actualCompany", "adminCompany"]),
    ...mapState("campaign", ["territories", "allCampaigns"]),

    nColsTable_calculator: function() {
      if(this.actualCompany){
        return 7;
      }else if(this.actualCompany == null){
        return 12;
      }else{
        return 12;
      }
    },
    filteredList() {
      return this.companyList.filter(c => {
        return (!this.selectedTerritory || c.territoryId == this.selectedTerritory) &&
               (!this.selectedCampaign || c.campaigns.indexOf(this.selectedCampaign) >= 0) 

      });
    }
  },

  mounted: function () {
    this.changePage({ title: "", route: "/GestioneAziende" });
    this.getAllCompanies();
    this.getTerritories();
    this.getAllCampaigns();
  },
};
</script>

<style>
.field-label {
  display: inline-block;
  width: 40%;
}

.form-group--error {
  /* color: red; */
  animation: shake 0.82s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}
.error {
  color: red;
  text-align: center;
}
@keyframes shake {
  10%,
  90% {
    transform: translate3d(-1px, 0, 0);
  }
  20%,
  80% {
    transform: translate3d(2px, 0, 0);
  }
  30%,
  50%,
  70% {
    transform: translate3d(-4px, 0, 0);
  }
  40%,
  60% {
    transform: translate3d(4px, 0, 0);
  }
}
</style>
