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
          Aggiungi da file
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col :cols="nColsTable_calculator">
        <!-- TODO: Tabella -->
        <div v-if="employees && employees.length > 0">
          <generic-table
            :items="employees"
            :headers="headerColumns"
            :title="tableTitle"
            :method="showEmployeeInfo"
          >
          </generic-table>
        </div>
        <div v-else class="empty-list">Non ci sono Dipendenti</div>
      </v-col>
      <profilo-employee v-if="actualEmployee && actualEmployee.item"></profilo-employee>

    </v-row>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ProfiloEmployee from "./Employee.vue";
// import Modal from "@/components/modal/ModalStructure.vue";
import GenericTable from "@/components/data-table/GenericTable.vue";

export default {
  components: { ProfiloEmployee,  GenericTable },

  name: "Dipendenti",

  data: function () {
    return {
      tableTitle: "Dipendenti",
      headerColumns: [{text:"Nome", value:"name"}, {text:"Cognome", value:"surname"}, {text:"Sede", value:"location"}, {text:"Codice", value:"code"}, {text: 'Bloccato', value: 'blockedStr'}, {text: 'Iscrizioni', value: "employeeCampaigns"}],
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
      // fileUploaded: null,
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
    ...mapActions("campaign", {
      getAllCampaigns: "getAll"}),
    showModal(title) {
      this.editModalVisible = true;
      this.newEmployee = true;
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
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteEmployee({
        companyId: this.actualCompany.item.id,
        employeeId: this.actualEmployee.item.id,
      });
    },
   
    showEmployeeInfo(employee) {
      if (this.currentEmployeeSelected == employee) {
        this.getEmployee(null);

        this.currentEmployeeSelected = undefined;
      } else {
        this.getEmployee(employee);
        this.currentEmployeeSelected = employee;
      }
    },
    updateEmployeeCampaigns() {
        if (this.allEmployees && this.allEmployees.items && this.allCampaigns && this.allCampaigns.items) {
            let empList = this.allEmployees.items.slice();
            empList.forEach(e => {
              let list = (e.campaigns || []).concat(e.trackingRecord ? Object.keys(e.trackingRecord) : []);
              const arr = Array.from(new Set(list)).map(cId => {
                  let tr = e.trackingRecord && e.trackingRecord[cId] ? e.trackingRecord[cId] : {registration: new Date().getTime()}
                  tr.id = cId;
                  tr.title = (this.allCampaigns.items.find(c => c.id === cId) || {title: cId}).title;
                  return tr;
              }).map(c => c.title)
              arr.sort();
              e.employeeCampaigns = arr.join(', ');
              e.blockedStr = e.blocked ? 'Si' : 'No'
            });
            this.employees = empList;
        }

    },
    // onFileUploaderChange() {
    //   console.log(this.$refs["file"]);
    //   this.fileUploaded = this.$refs["file"].files;
    // },
    // importEmployees() {
    //   console.log(this.fileUploaded);
    //   this.modalImportEmployeesOpen = false;
    //   var formData = new FormData();
    //   formData.append("file", this.fileUploaded.item(0));
    //   this.importData({ companyId: this.actualCompany.item.id, file: formData });
    // },
  },
  
  computed: {
    ...mapState("employee", ["allEmployees", "actualEmployee"]),
    ...mapState("company", ["actualCompany"]),
     ...mapState("campaign", ["allCampaigns"]),

    // fileName() {
    //   return this.fileUploaded.item(0).name;
    // },
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
    this.changePage({ title: "", route: "/GestioneDipendenti" });
    if (this.actualCompany && this.actualCompany.item)
      this.getAllEmployees(this.actualCompany.item.id);
    this.getAllCampaigns();
  },
  watch: {
    allEmployees() {
       this.updateEmployeeCampaigns();
    },
    allCampaigns() {
       this.updateEmployeeCampaigns();
    }
  }
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
