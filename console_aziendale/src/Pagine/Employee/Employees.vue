<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          class="fab mr-4"
          fab
          color="cyan accent-2"
          @click="showModal('Aggiungi dipendente')"
        >
          <v-icon>mdi-plus</v-icon>
        </v-btn>
        <v-btn
          class="fab"
          fab
          color="cyan accent-2"
          @click="modalImportEmployeesOpen = true"
        >
          <v-icon>mdi-file-import</v-icon>
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
      </v-col>
      <!-- TODO: ProfiloDipendente -->
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

      <transition
        enter-active-class="transition duration-300 ease-out transform"
        enter-class="scale-95 opacity-0"
        enter-to-class="scale-100 opacity-100"
        leave-active-class="transition duration-150 ease-in transform"
        leave-class="scale-100 opacity-100"
        leave-to-class="scale-95 opacity-0"
      >
        <div
          class="fixed z-10 inset-0 overflow-y-auto shadow-md"
          v-if="modalInsertEmployeeOpen"
        >
          <div
            class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0"
          >
            <div class="fixed inset-0 transition-opacity" aria-hidden="true">
              <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
            </div>

            <span
              class="hidden sm:inline-block sm:align-middle sm:h-screen"
              aria-hidden="true"
              >&#8203;</span
            >

            <div
              class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full"
              role="dialog"
              aria-modal="true"
              aria-labelledby="modal-headline"
            >
              <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div class="sm:flex sm:items-start">
                  <div
                    class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10"
                  >
                    <add-employee id="add-employee-icon" />
                  </div>
                  <div class="mt-3 text-center sm:mt-0 sm:ml-4">
                    <h3
                      class="text-lg leading-6 font-medium text-gray-900 text-left"
                      id="modal-headline"
                    >
                      Aggiungi Dipendente
                    </h3>
                    <div class="mt-2">
                      <form
                        id="addEmployeeForm"
                        v-on:submit.prevent="onAddEmployeeSubmit"
                        action=""
                        name="addEmployee"
                        class="form flex flex-col bg-white p-6 pl-0 relative lg:rounded-xl justify-center"
                      >
                        <div
                          class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
                        >
                          <input
                            type="text"
                            name="employeeName"
                            id=""
                            required
                            placeholder="Nome *"
                            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                          />

                          <input
                            type="text"
                            name="employeeSurname"
                            id=""
                            required
                            placeholder="Cognome *"
                            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                          />

                          <input
                            type="number"
                            name="employeeID"
                            id=""
                            required
                            placeholder="Matricola *"
                            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                          />

                          <input
                            type="email"
                            name="employeeEmail"
                            id=""
                            required
                            placeholder="Email *"
                            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                          />
                        </div>
                      </form>
                    </div>
                  </div>
                </div>
              </div>
              <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                <input
                  class="mx-2 inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-primary rounded shadow ripple hover:shadow-lg hover:bg-primary_light focus:outline-none"
                  type="submit"
                  form="addEmployeeForm"
                  value="Aggiungi Dipendente"
                />
                <button
                  @click="modalInsertEmployeeOpen = false"
                  class="mx-2 inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-danger rounded shadow ripple hover:shadow-lg focus:outline-none"
                >
                  Annulla
                </button>
              </div>
            </div>
          </div>
        </div>
      </transition>

      <transition
        enter-active-class="transition duration-300 ease-out transform"
        enter-class="scale-95 opacity-0"
        enter-to-class="scale-100 opacity-100"
        leave-active-class="transition duration-150 ease-in transform"
        leave-class="scale-100 opacity-100"
        leave-to-class="scale-95 opacity-0"
      >
        <div
          class="fixed z-10 inset-0 overflow-y-auto shadow-md"
          v-if="modalImportEmployeesOpen"
        >
          <div
            class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0"
          >
            <div class="fixed inset-0 transition-opacity" aria-hidden="true">
              <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
            </div>

            <span
              class="hidden sm:inline-block sm:align-middle sm:h-screen"
              aria-hidden="true"
              >&#8203;</span
            >

            <div
              class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full"
              role="dialog"
              aria-modal="true"
              aria-labelledby="modal-headline"
            >
              <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div class="sm:flex sm:items-start">
                  <div
                    class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10"
                  >
                    <import-icon id="import-icon" />
                  </div>
                  <div class="mt-3 text-center sm:mt-0 sm:ml-4">
                    <h3
                      class="text-lg leading-6 font-medium text-gray-900 text-left"
                      id="modal-headline"
                    >
                      Importa dipendenti
                    </h3>
                    <button
                      class="mx-2 inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-primary rounded shadow ripple hover:shadow-lg hover:bg-primary_light focus:outline-none"
                    >
                      <a href="/files/exampleEmployee.csv" download>Scarica file di esempio</a>
                    </button>
                    <template v-if="fileUploaded != null"
                      ><div class="pt-2">
                        <span class="text-left text-lg"> {{ fileName }} </span
                        ><span @click="removeFile()" class="text-danger cursor-pointer">
                          rimuovi</span
                        >
                      </div></template
                    >
                    <template v-else>
                      <div class="mt-2">
                        <div
                          class="p-12 border-gray-300 border-dashed border-8 border-primary"
                          :class="inDragArea ? ' bg-primary_light' : 'bg-background'"
                          @dragover.prevent="dragover"
                          @dragleave.prevent="dragleave"
                          @drop.prevent="drop"
                        >
                          <input
                            type="file"
                            name="fileUploader"
                            id="fileUploader"
                            class="w-px h-px opacity-0 overflow-hidden absolute"
                            @change="onFileUploaderChange"
                            ref="file"
                            accept=".csv"
                          />

                          <label for="fileUploader" class="block cursor-pointer">
                            <div>
                              Trascina qui il file
                              <span class="font-semibold">.csv</span> oppure
                              <span class="text-primary">clicca qui</span> per caricarlo
                            </div>
                          </label>
                        </div>
                      </div></template
                    >
                  </div>
                </div>
              </div>
              <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                <button
                  @click="importEmployees"
                  class="mx-2 inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-primary rounded shadow ripple hover:shadow-lg hover:bg-primary_light focus:outline-none"
                >
                  Importa dipendenti
                </button>
                <button
                  @click="modalImportEmployeesOpen = false"
                  class="mx-2 inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-danger rounded shadow ripple hover:shadow-lg focus:outline-none"
                >
                  Annulla
                </button>
              </div>
            </div>
          </div>
        </div>
      </transition>

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
  methods: {
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

    copyFormValues() {
      for (const [key] of Object.entries(this.employee)) {
        this[key] = this.employee[key];
      }
    },

    saveEmployee() {
      EventBus.$emit("CHECK_EMPLOYEE_FORM");

      //check fields
      // eslint-disable-next-line no-constant-condition
      // console.log("submit!");
      // this.$v.$touch();
      // if (this.$v.$invalid) {
      //   this.submitStatus = "ERROR";
      //   return;
      // } else {
      //   this.createEmployee();
      //   if (this.newEmployee) {
      //     this.addEmployeeCall({
      //       companyId: this.actualCompany.item.id,
      //       employee: this.employee,
      //     });
      //   } else {
      //     this.updateEmployeeCall({
      //       companyId: this.actualCompany.item.id,
      //       employee: this.employee,
      //     });
      //   }
      //   this.$v.$reset();
      // }

      // this.editModalVisible = false;
      // this.newEmployee = false;
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteEmployee({
        companyId: this.actualCompany.item.id,
        employeeId: this.actualEmployee.item.id,
      });
    },
    // updateEmployee() {
    //   //check fields
    //   // eslint-disable-next-line no-constant-condition
    //   if (true) {
    //     this.updateEmployeeCall(this.employee);
    //     this.editModalVisible = false;
    //   }
    // },
    // goToEmployee: function (employeeName) {
    //   this.$router.push("/azienda/" + employeeName);
    // },
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
    removeUploadedFile: function () {
      this.fileUploaded = null;
    },

    dragover: function () {
      this.inDragArea = true;
    },
    dragleave: function () {
      this.inDragArea = false;
    },
    drop: function (event) {
      event.preventDefault();
      this.inDragArea = false;

      this.$refs["file"].files = event.dataTransfer.files;
      this.onFileUploaderChange();
    },
    importEmployees: function () {
      console.log(this.fileUploaded);
      this.modalImportEmployeesOpen = false;
      var formData = new FormData();
      formData.append("file", this.fileUploaded.item(0));
      this.importData({ companyId: this.actualCompany.item.id, file: formData });
    },
    removeFile: function () {
      this.fileUploaded = null;
    },
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
