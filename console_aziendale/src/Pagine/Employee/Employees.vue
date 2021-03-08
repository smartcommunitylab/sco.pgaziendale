<template>
  <div class="flex flex-col lg:flex-row">
    <div class="lg:w-4/6 mx-2 my-2 flex flex-col bg-white">
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
                      <a href="/files/example.csv" download>Scarica file di esempio</a>
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
      <div v-if="allEmployees && allEmployees.items && allEmployees.items.length > 0">
          <generic-table
        :data="allEmployees.items"
        :columns="gridColumns"
        :header="headerColumns"
        :method="showEmployeeInfo"
      >
      </generic-table>
        <!-- <table class="table-auto rounded relative w-full">
          <thead class="text-center justify-between">
            <tr class="truncate px-2 flex border-b border-background text-center">
              <th class="w-1/5">Nome</th>

              <th class="w-1/5">Cognome</th>

              <th class="w-1/5">Email</th>
              <th class="w-1/5">Codice</th>
              <th class="w-1/5"></th>
            </tr>
          </thead>
          <tbody class="bg-white text-center justify-between" v-if="allEmployees">
            <template v-for="employee in allEmployees.items">
              <template
                v-if="
                  (selectedHq == 0 || employee.idSede == selectedHq) &&
                  (selectedCampaign == 0 || isEmployeeInCampaign(employee))
                "
              >
                <tr
                  class="text-center m-auto truncate px-2 select-none cursor-pointer flex items-center border-b border-background hover:bg-background transition ease-in duration-100"
                  :key="employee.id"
                  tag="tr"
                  :id="employee.id"
                  @click="showEmployeeInfo(employee)"
                >
                  <td class="w-1/5">
                    <p class="text-gray-800 text-sm font-semibold text-center">
                      {{ employee.name }}
                    </p>
                  </td>
                  <td class="w-1/5">
                    <p class="text-gray-800 text-sm font-semibold text-center">
                      {{ employee.surname }}
                    </p>
                  </td>

                  <td class="w-1/5">
                    <p class="text-gray-800 text-sm font-semibold text-center truncate">
                      {{ employee.companyEmail }}
                    </p>
                  </td>
                  <td class="w-1/5">
                    <p class="text-gray-800 text-sm font-semibold text-center">
                      {{ employee.code }}
                    </p>
                  </td>
                  <td class="flex items-end w-1/5 pr-12">
                    <eye-icon />
                  </td></tr
              ></template>
            </template>
          </tbody>
        </table> -->
      </div>
      <div v-else class="text-center">Non ci sono dipendenti</div>
      <div class="flex flex-row justify-center py-4">
        <!-- <div class="px-2">
          <select
            class="focus:border-primary border-2 p-2 mb-2 md:mb-0 lg:mb-2  md:mr-2 lg:mr-0 appearance-none "
            v-model="selectedHq"
            required
          >
            <option value="0" selected disabled hidden
              >Seleziona una sede</option
            >
            <template v-for="hq in filter_hqs">
              <option :key="hq.id" :value="hq.id">{{ hq.address }}</option>
            </template>
          </select>
        </div> -->

        <!-- <div class="px-2">
          <select
            class="focus:border-primary border-2 p-2 mb-2 md:mb-0 lg:mb-2  md:mr-2 lg:mr-0 appearance-none "
            v-model="selectedCampaign"
            required
          >
            <option value="0" selected disabled hidden
              >Seleziona una Campagna</option
            >
            <template v-for="campaign in filter_campaigns">
              <template v-if="hasCampaignNotEnded(campaign)">
                <option :key="campaign.id" :value="campaign.id">{{
                  campaign.title
                }}</option></template
              >
            </template>
          </select>
        </div> -->

        <div class="px-2">
          <button
            @click="modalImportEmployeesOpen = true"
            class="inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-primary rounded shadow ripple hover:shadow-lg hover:bg-primary_light focus:outline-none"
          >
            Importa CSV
          </button>
        </div>
      </div>
      <div class="ml-auto pt-4 pr-4">
        <button
          @click="showModal('Aggiungi dipendente')"
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <add-icon class="add-icon" />
        </button>
      </div>
    </div>
    <!-- <infobox ref="infobox" /> -->
    <profilo-employee v-if="actualEmployee"></profilo-employee>

    <modal v-show="deleteModalVisible">
      <template v-slot:header> Cancella Dipendente </template>
      <template v-slot:body> Sei sicuro di voler cancellare il dipendente? </template>
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
    <modal v-show="editModalVisible">
      <template v-slot:header> {{ popup.title }} </template>
      <template v-slot:body>
        <form action="" id="addEmployee">
          <div class="mb-4 flex flex-wrap justify-between">
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.name.$error }">
                <label class="field-label" for="first_name">Nome </label>
                <input
                  type="text"
                  name="employeeName"
                  placeholder="Nome *"
                  v-model.trim="$v.name.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="employeeName"
                />
              </div>
              <div v-if="$v.name.$error">
                <div class="error" v-if="!$v.name.required">
                  Il campo nome e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.surname.$error }">
                <label class="field-label" for="first_name">Cognome </label>
                <input
                  type="text"
                  name="employeeSurname"
                  placeholder="Cognome *"
                  v-model.trim="$v.surname.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="employeeName"
                />
              </div>
              <div v-if="$v.surname.$error">
                <div class="error" v-if="!$v.surname.required">
                  Il campo Cognome e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.code.$error }">
                <label class="field-label" for="first_name">Codice</label>
                <input
                  type="text"
                  name="employeeCode"
                  id="employeeCode"
                  placeholder="Codice *"
                  v-model.trim="$v.code.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.code.$error">
                <div class="error" v-if="!$v.code.required">
                  Il campo codice e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.companyEmail.$error }"
              >
                <label class="field-label" for="password">Email </label>
                <input
                  type="text"
                  name="employeeEmail"
                  id=""
                  required
                  placeholder="Email *"
                  v-model.trim="$v.companyEmail.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.companyEmail.$error">
                <div class="error" v-if="!$v.companyEmail.required">
                  Il campo email e' richiesto.
                </div>
              </div>
            </div>
          </div>
        </form>
      </template>
      <template v-slot:footer>
        <button
          type="button"
          class="btn-close"
          @click="saveEmployee"
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
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ProfiloEmployee from "./ProfiloEmployee.vue";
import Modal from "@/components/Modal.vue";
import EventBus from "@/components/eventBus";
import { required } from "vuelidate/lib/validators";
import GenericTable from "@/components/GenericTable.vue"
// import { employees } from "../tmp-data/employees.js";
// import { headquarters } from "../tmp-data/hqs.js";
// import { campaigns } from "../tmp-data/campaigns.js";
export default {
  components: { ProfiloEmployee, Modal,GenericTable },
  name: "Dipendenti",
  data: function () {
    return {
      gridColumns: ["name", "surname","companyEmail","code"],
      headerColumns: ["Nome", "Cognome","Email","Codice"],
      editModalVisible: false,
      deleteModalVisible: false,
      currentEmployeeSelected: undefined,
      employee: {},
      name: "",
      surname: "",
      code: "",
      companyEmail: "",
      employees: [],
      popup: {
        title: "",
      },
      submitStatus: null,
      selectedHq: 0,
      selectedCampaign: 0,
      // filter_campaigns: [],
      // filter_hqs: [],
      modalInsertEmployeeOpen: false,
      modalImportEmployeesOpen: false,
      fileUploaded: null,
      inDragArea: false,
    };
  },
  validations: {
    name: {
      required,
    },
    surname: {
      required,
    },
    code: {
      required,
    },
    companyEmail: {
      required,
    },
  },
  mounted: function () {
     this.changePage({title: 'Lista dipendenti',
                route: '/dipendenti'})
    if (this.actualCompany && this.actualCompany.item)
      this.getAllEmployees(this.actualCompany.item.id);
    // this.employees = employees;
    // console.log(this.$refs["infobox"].$el.getBoundingClientRect());
    // this.filter_hqs = null;
    // this.filter_campaigns = null;
    EventBus.$on("EDIT_EMPLOYEE", (employee) => {
      this.editModalVisible = true;
      this.employee = employee.item;
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
  },
  computed: {
    ...mapState("employee", ["allEmployees", "actualEmployee"]),
    ...mapState("company", ["actualCompany"]),
    fileName() {
      return this.fileUploaded.item(0).name;
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
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.editModalVisible = false;
      this.newEmployee = false;
      this.$v.$reset();
    },
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
    createEmployee() {
      this.employee = {
        name: this.name,
        surname: this.surname,
        code: this.code,
        companyEmail: this.companyEmail,
      };
    },
    saveEmployee() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      // console.log("submit!");
      this.$v.$touch();
      if (this.$v.$invalid) {
        this.submitStatus = "ERROR";
        return;
      } else {
        this.createEmployee();
        if (this.newEmployee) {
          this.addEmployeeCall({
            companyId: this.actualCompany.item.id,
            employee: this.employee,
          });
        } else {
          this.updateEmployeeCall({
            companyId: this.actualCompany.item.id,
            employee: this.employee,
          });
        }
        this.$v.$reset();
      }

      this.editModalVisible = false;
      this.newEmployee = false;
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteEmployee({
        companyId: this.actualCompany.item.id,
        employeeId: this.actualEmployee.item.id,
      });
    },
    updateEmployee() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      if (true) {
        this.updateEmployeeCall(this.employee);
        this.editModalVisible = false;
      }
    },
    goToEmployee: function (employeeName) {
      this.$router.push("/azienda/" + employeeName);
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
    // showInfoBox: function(index) {
    //   this.$refs["infobox"].showEmployeeDetails(this.employees[index]);
    // },

    //   hasCampaignNotEnded: function(campaign) {
    //     console.log(campaign);
    //     return new Date(campaign.endDate) > new Date();
    //   },
    //   isEmployeeInCampaign: function(employee) {
    //     let toRtn = false;
    //     if (this.selectedCampaign == 0) return true;
    //     employee.campagne.forEach((campaign) => {
    //       if (campaign.id == this.selectedCampaign) {
    //         toRtn = true;
    //       }
    //     });

    //     return toRtn;
    //   },
    //   onAddEmployeeSubmit: function(submitEvent) {
    //     console.log(submitEvent);
    //     let employee = {};
    //     let tmp = submitEvent.target.elements;
    //     employee.first_name = tmp.employeeName.value;
    //     employee.last_name = tmp.employeeSurname.value;
    //     employee.id = this.employees.length + 1;
    //     employee.email = tmp.employeeEmail.value;
    //     employee.matricola = tmp.employeeID.value;
    //     employee.campagne = [];
    //     employee.idSede = 0;
    //     console.log(employee);
    //     this.employees.push(employee);
    //     this.modalInsertEmployeeOpen = false;
    //   },

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
      formData.append("image", this.fileUploaded.item(0));

      this.importData(this.actualCompany.id, formData);
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
