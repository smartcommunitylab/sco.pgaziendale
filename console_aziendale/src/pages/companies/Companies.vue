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
        <div v-if="allCompanies && allCompanies.items && allCompanies.items.length > 0">
          <generic-table
            :items="allCompanies.items"
            :headers="headerColumns"
            :title="tableTitle"
            :method="showCompanyInfo"
          >
          </generic-table>
        </div>
        <div v-else class="empty-list">Non ci sono Aziende</div>
      </v-col>
      <profilo-azienda v-if="actualCompany"></profilo-azienda>
    </v-row>
  </div>
</template>

<script>
import ProfiloAzienda from "./Company.vue";
import { mapState, mapActions } from "vuex";
import GenericTable from "@/components/data-table/GenericTable.vue";

export default {
  components: { ProfiloAzienda, GenericTable },

  name: "Aziende",

  data: function () {
    return {
      tableTitle: "Aziende",
      headerColumns: [{text:"Nome", value:"name"}, {text:"Codice Azienda", value:"code"}, {text:"Indirizzo", value:"address"}],
      editModalVisible: false,
      deleteModalVisible: false,
      currentCompanySelected: undefined,
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
    showCompanyInfo: function (company) {
      if (this.currentCompanySelected == company) {
        this.getCompanyById(null);

        this.currentCompanySelected = undefined;
      } else {
        this.getCompanyById(company.id);
        this.currentCompanySelected = company;
      }
    },
  },

  computed: {
    ...mapState("company", ["allCompanies", "actualCompany", "adminCompany"]),

    nColsTable_calculator: function() {
      if(this.actualCompany){
        return 8;
      }else if(this.actualCompany == null){
        return 12;
      }else{
        return 12;
      }
    },
  },

  mounted: function () {
    this.changePage({ title: "", route: "/GestioneAziende" });
    this.getAllCompanies();
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
