<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          class="fab"
          fab
          color="cyan accent-2"
          @click="showModal('Aggiungi azienda')"
        >
          <v-icon>mdi-plus</v-icon>
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
      <modal v-show="editModalVisible">
        <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
          <azienda-form />
        </template>
        <template v-slot:footer>
          <button
            type="button"
            class="btn-close"
            @click="saveCompany"
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
      <modal v-show="deleteModalVisible">
        <template v-slot:header> Cancella Azienda </template>
        <template v-slot:body>
          Sei sicuro di voler cancellare l'azienda selezionata?
        </template>
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
    </v-row>
  </div>
</template>

<script>
import ProfiloAzienda from "./ProfiloAzienda.vue";
import Modal from "@/components/Modal.vue";
import { mapState, mapActions } from "vuex";
import EventBus from "@/components/eventBus";
import GenericTable from "@/components/GenericTable.vue";
import AziendaForm from "./AziendaForm.vue"

export default {
  components: { ProfiloAzienda, Modal, GenericTable,AziendaForm },
  name: "Aziende",
  data: function () {
    return {
      tableTitle: "Aziende",
      headerColumns: [{text:"Nome", value:"name"}, {text:"Codice Azienda", value:"code"}],
      editModalVisible: false,
      deleteModalVisible: false,
      currentCompanySelected: undefined,
      popup: {
        title: "",
      },
      submitStatus: null,
    };
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
    this.changePage({ title: "Lista aziende", route: "/aziende" });
    this.getAllCompanies();
    EventBus.$on("EDIT_COMPANY", (company) => {
      this.editModalVisible = true;
      EventBus.$emit("EDIT_COMPANY_FORM", company.item);
      this.popup = {
        title: "Modifica",
      };
    });
    EventBus.$on("DELETE_COMPANY", (company) => {
      this.deleteModalVisible = true;
      this.company = company.item;
      this.popup = {
        title: "Cancella",
      };
    });
    EventBus.$on("OK_COMPANY_FORM", (company) => {
        if (this.newCompany) {
          this.addCompanyCall(company);
        } else {
          this.updateCompanyCall(company);
      }
      this.editModalVisible = false;
      this.newCompany = false;
    });
    EventBus.$on("NO_COMPANY_FORM", () => {
      this.submitStatus = "ERROR";
    });
  },
    beforeDestroy() {
    EventBus.$off("NO_COMPANY_FORM");
    EventBus.$off("OK_COMPANY_FORM");
    EventBus.$off("DELETE_COMPANY");
    EventBus.$off("EDIT_COMPANY");
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
      EventBus.$emit("NEW_COMPANY_FORM");
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.editModalVisible = false;
      this.newCompany = false;
    },

    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
   
    saveCompany() {
            EventBus.$emit("CHECK_COMPANY_FORM");
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteCompany(this.company);
    },
    
    showCompanyInfo: function (company) {
      this.nColsTable = 8;
      if (this.currentCompanySelected == company) {
        this.getCompanyById(null);

        this.currentCompanySelected = undefined;
      } else {
        this.getCompanyById(company.id);
        this.currentCompanySelected = company;
      }
    },
  },
};
</script>

<style>
.field-label {
  display: inline-block;
  width: 40%;
}
.selected {
  @apply bg-background;
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
