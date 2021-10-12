<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          x-large
          color="secondary"
          rounded
          elevation="6"
          @click="openModal({type:'employeeFormAdd', object:null})"
          class="mr-4"
        >
          <v-icon left>mdi-plus</v-icon>
          AGGIUNGI
        </v-btn>
        <v-btn
          x-large
          color="secondary"
          rounded
          elevation="6"
          @click="openModal({type:'employeeImport', object:null})"
        >
          <v-icon left>mdi-file-import</v-icon>
          IMPORTA
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col :cols="nColsTable_calculator">
        <!-- TODO: Tabella -->
        <div v-if="allEmployees && allEmployees.items && allEmployees.items.length > 0">
          <generic-table
            :items="allEmployees.items"
            :headers="headerColumns"
            :title="tableTitle"
            :method="showEmployeeInfo"
          >
          </generic-table>
        </div>
        <div v-else class="empty-list">Non ci sono Dipendenti</div>
      </v-col>
      <profilo-employee v-if="actualEmployee && actualEmployee.item"></profilo-employee>
      
      <!-- TODO: Modale Dipendente -->
      <modal v-show="deleteModalVisible">
        <template v-slot:header> <div class="text-danger"> Cancella Dipendente </div> </template>
        <template v-slot:body>
          <p class="text-subtitle-1">Sei sicuro di voler cancellare il dipendente?</p>
        </template>
        <template v-slot:footer>
          <v-btn
            text
            @click="closeDeleteModal"
            class="py-8 ml-8"
          >
            Annulla
          </v-btn>
          <v-btn
            color="error"
            text
            @click="deleteConfirm"
            class="py-8 ml-8"
          >
            Conferma
          </v-btn>
        </template>
      </modal>
      <modal v-show="editModalVisible">
        <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
          <employee-form />
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
            @click="saveEmployee"
            class="py-8 ml-8"
          >
            Salva
          </v-btn>
        </template>
      </modal>
      <modal v-show="modalImportEmployeesOpen">

        <template v-slot:header> Importa dipendenti </template>


        <template v-slot:body> 
          <v-row
            justify="center"
            class="mt-5 mb-8"
          >
            <v-btn
              outlined
              color="primary"
            >
              <a href="/files/exampleEmployee.csv" download>Scarica file di esempio</a>
            </v-btn>
          </v-row>
          <v-row>
            <v-col
              cols="12"
            >
              <v-file-input
                label="Clicca qui per caricare il file .csv"
                type="file"
                ref="file"
                v-model="fileUploaded"
                accept=".csv"
                @change="onFileUploaderChange"
                outlined
                dense
              ></v-file-input>
            </v-col>
          </v-row>
        </template>


        <template v-slot:footer>
          <v-btn
              text
              @click="closeImportModal"
              class="py-8 ml-8"
            >
              Annulla
            </v-btn>
            <v-btn
              color="primary"
              text
              @click="importEmployees"
              class="py-8 ml-8"
            >
              Importa dipendenti
            </v-btn>
        </template>
      </modal>



    </v-row>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ProfiloEmployee from "./ProfiloEmployee.vue";
import Modal from "@/components/Modal.vue";
import EventBus from "@/components/eventBus";
import GenericTable from "@/components/GenericTable.vue";
import EmployeeForm from "./EmployeeForm.vue";

export default {
  components: { ProfiloEmployee, Modal, GenericTable, EmployeeForm },

  name: "Dipendenti",

  data: function () {
    return {
      tableTitle: "Dipendenti",
      headerColumns: [{text:"Nome", value:"name"}, {text:"Cognome", value:"surname"}, {text:"Sede", value:"location"}, {text:"Codice", value:"code"}],
      editModalVisible: false,
      deleteModalVisible: false,
      currentEmployeeSelected: undefined,
      employees: [],
      popup: {
        title: "",
      },
      submitStatus: null,
      selectedHq: 0,
      selectedCampaign: 0,
      modalInsertEmployeeOpen: false,
      modalImportEmployeesOpen: false,
      fileUploaded: null,
      inDragArea: false,
    };
  },
  
  methods: {
    ...mapActions("modal", {openModal: 'openModal'}),
    ...mapActions("employee", {
      getAllEmployees: "getAll",
      addEmployeeCall: "addEmployee",
      updateEmployeeCall: "updateEmployee",
      getEmployee: "getEmployee",
      deleteEmployee: "deleteEmployee",
      importData: "importEmployees",
    }),
    ...mapActions("navigation", { changePage: "changePage" }),

    showModal(title) {
      this.editModalVisible = true;
      this.newEmployee = true;
      EventBus.$emit("NEW_EMPLOYEE_FORM");
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.editModalVisible = false;
      this.newEmployee = false;
    },
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
    closeImportModal() {
      this.modalImportEmployeesOpen = false
      this.$v.$reset();
    },
    copyFormValues() {
      for (const [key] of Object.entries(this.employee)) {
        this[key] = this.employee[key];
      }
    },
    saveEmployee() {
      if (!this.$v.$invalid) {
        EventBus.$emit("CHECK_EMPLOYEE_FORM");       
      } else{
        this.$v.$touch();
      }
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteEmployee({
        companyId: this.actualCompany.item.id,
        employeeId: this.actualEmployee.item.id,
      });
    },
   
    showEmployeeInfo: function (employee) {
      if (this.currentEmployeeSelected == employee) {
        this.getEmployee(null);

        this.currentEmployeeSelected = undefined;
      } else {
        this.getEmployee(employee);
        this.currentEmployeeSelected = employee;
      }
    },
    onFileUploaderChange: function () {
      console.log(this.$refs["file"]);
      this.fileUploaded = this.$refs["file"].files;
    },
    importEmployees: function () {
      console.log(this.fileUploaded);
      this.modalImportEmployeesOpen = false;
      var formData = new FormData();
      formData.append("file", this.fileUploaded.item(0));
      this.importData({ companyId: this.actualCompany.item.id, file: formData });
    },
  },
  
  computed: {
    ...mapState("employee", ["allEmployees", "actualEmployee"]),
    ...mapState("company", ["actualCompany"]),

    fileName() {
      return this.fileUploaded.item(0).name;
    },
    nColsTable_calculator: function() {
      if(this.actualEmployee){
        return 8;
      }else if(this.actualEmployee == null){
        return 12;
      }else{
        return 12;
      }
    },
  },

  mounted: function () {
    this.changePage({ title: "Lista dipendenti", route: "/dipendenti" });
    if (this.actualCompany && this.actualCompany.item)
      this.getAllEmployees(this.actualCompany.item.id);
    EventBus.$on("EDIT_EMPLOYEE", (employee) => {
      this.editModalVisible = true;
      EventBus.$emit("EDIT_EMPLOYEE_FORM", employee.item);
      this.popup = {
        title: "Modifica",
      };
    });
    EventBus.$on("DELETE_EMPLOYEE", (employee) => {
      this.deleteModalVisible = true;
      this.employee = employee.item;
      this.popup = {
        title: "Cancella",
      };
    });
    EventBus.$on("OK_EMPLOYEE_FORM", (employee) => {
      if (this.newEmployee) {
        this.addEmployeeCall({
          companyId: this.actualCompany.item.id,
          employee: employee,
        });
      } else {
        this.updateEmployeeCall({
          companyId: this.actualCompany.item.id,
          employee: employee,
        });
      }
      this.editModalVisible = false;
      this.newEmployee = false;
    });
    EventBus.$on("NO_EMPLOYEE_FORM", () => {
      this.submitStatus = "ERROR";
    });
  },

  beforeDestroy() {
    EventBus.$off("NO_EMPLOYEE_FORM");
    EventBus.$off("OK_EMPLOYEE_FORM");
    EventBus.$off("DELETE_EMPLOYEE");
    EventBus.$off("EDIT_EMPLOYEE");
  },
};
</script>

<style>
.add-icon {
  color: white;
}
svg {
  margin: auto;
}
#add-employee-icon {
  width: 50px;
}
#add-employee-icon svg {
  width: 100%;
  height: 100%;
}
#import-icon {
  width: 50px;
}
#import-icon svg {
  width: 100%;
  height: 100%;
}
.dots-icon {
  width: 40px;
}
.dots-icon svg {
  width: 100%;
  height: 100%;
}
</style>
