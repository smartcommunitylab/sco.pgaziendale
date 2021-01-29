<template>
  <div class="flex flex-col lg:flex-row ">
    <div class=" lg:w-4/6 mx-2 my-2 flex flex-col bg-white">
      <div class="flex flex-row justify-center py-4">
        <div class="px-2">
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
        </div>

        <div class="px-2">
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
        </div>

        <div class="px-2">
          <button
            @click="modalImportEmployeesOpen = true"
            class="inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-primary rounded shadow ripple hover:shadow-lg hover:bg-primary_light focus:outline-none"
          >
            Importa CSV
          </button>
        </div>
      </div>

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
                    <svg
                      version="1.1"
                      id="Layer_1"
                      xmlns="http://www.w3.org/2000/svg"
                      xmlns:xlink="http://www.w3.org/1999/xlink"
                      x="0px"
                      y="0px"
                      viewBox="0 0 122.88 115.48"
                      style="enable-background:new 0 0 122.88 115.48"
                      xml:space="preserve"
                    >
                      <g>
                        <path
                          class="st0"
                          d="M39.54,70.85l8.78,25.83l4.42-15.33l-2.17-2.37c-0.98-1.42-1.19-2.67-0.65-3.74c1.17-2.32,3.6-1.89,5.87-1.89 c2.38,0,5.31-0.45,6.05,2.52c0.25,0.99-0.06,2.04-0.76,3.11l-2.17,2.37l4.42,15.33l7.95-25.83c1.7,1.53,4.4,2.7,7.52,3.68 c0.47-0.56,0.97-1.11,1.49-1.63c4.51-4.51,10.75-7.31,17.64-7.31c6.89,0,13.12,2.79,17.64,7.31c4.51,4.51,7.31,10.75,7.31,17.64 s-2.79,13.12-7.31,17.64c-4.51,4.51-10.75,7.31-17.64,7.31c-5.22,0-10.07-1.61-14.08-4.35H55.83H6.07 c-3.81-0.29-5.75-2.26-6.07-5.67c0.97-6.57,1.24-15.16,5.24-20.45c1.45-1.92,3.26-3.34,5.26-4.45 C16.82,77.04,33.8,76.01,39.54,70.85L39.54,70.85z M94.2,75.99h7.46c0.49,0,0.89,0.4,0.89,0.89v9.04h9.04 c0.49,0,0.89,0.4,0.89,0.89v7.46c0,0.49-0.4,0.89-0.89,0.89h-9.04v9.04c0,0.49-0.4,0.89-0.89,0.89H94.2c-0.49,0-0.89-0.4-0.89-0.89 v-9.04h-9.04c-0.49,0-0.89-0.4-0.89-0.89v-7.46c0-0.49,0.4-0.89,0.89-0.89h9.04v-9.04C93.32,76.39,93.72,75.99,94.2,75.99 L94.2,75.99z M112.96,75.52c-3.84-3.84-9.15-6.22-15.02-6.22c-5.87,0-11.18,2.38-15.02,6.22c-3.84,3.84-6.22,9.15-6.22,15.02 c0,5.87,2.38,11.18,6.22,15.02c3.84,3.84,9.15,6.22,15.02,6.22c5.87,0,11.18-2.38,15.02-6.22c3.84-3.84,6.22-9.15,6.22-15.02 C119.18,84.67,116.8,79.36,112.96,75.52L112.96,75.52z M35.32,33.91c0.68,0.19,1.38,0.21,2.11,0.06l-1.25-9.91 c0.64-2.45,1.62-4.36,2.92-5.76c1.36-1.46,3.06-2.37,5.09-2.76c2.69-0.19,3.5,1.77,6.19,3.49c8.18,5.23,15.1,6.99,25.21,7.12 l-1.25,6.32c-0.17,0.26-0.26,0.58-0.23,0.91c0,0.05,0.01,0.1,0.02,0.16l-0.18,0.9c0.28,0.02,0.55,0.03,0.82,0.02 c0.25,0.15,0.55,0.23,0.87,0.2c1.76-0.16,2.83,0,3.12,0.59c0.4,0.8,0.06,2.43-1.09,5.05l-5.53,9.11c-2.06,3.39-4.14,6.78-6.79,9.25 c-2.54,2.38-5.67,3.96-9.94,3.95c-3.95-0.01-6.93-1.53-9.4-3.78c-2.56-2.34-4.63-5.53-6.59-8.66l-4.91-7.81h0l-0.02-0.03 c-1.51-2.25-2.29-4.19-2.33-5.68c-0.01-0.5,0.06-0.94,0.23-1.3c0.15-0.32,0.38-0.59,0.7-0.81C33.62,34.19,34.36,33.98,35.32,33.91 L35.32,33.91z M78.98,32.1l0.31-12.72c-0.37-5.17-2.08-9.08-4.81-12.03c-6.69-7.24-19.19-9.1-28.62-5.69 c-1.59,0.57-3.09,1.3-4.46,2.17C37.5,6.31,34.36,9.91,33.1,14.4c-0.3,1.06-0.5,2.12-0.61,3.18c-0.21,4.46-0.09,9.78,0.23,14.01 c-0.44,0.17-0.84,0.37-1.2,0.61c-0.76,0.51-1.33,1.18-1.7,1.97c-0.35,0.76-0.51,1.62-0.48,2.57c0.06,2.01,0.99,4.47,2.79,7.15 l4.91,7.8c2.07,3.28,4.24,6.64,7.08,9.24c2.94,2.69,6.53,4.51,11.3,4.53c5.11,0.01,8.84-1.88,11.87-4.71 c2.92-2.74,5.12-6.3,7.27-9.85l5.59-9.21c0.03-0.05,0.06-0.11,0.08-0.16l0,0c1.53-3.47,1.86-5.89,1.06-7.49 C80.84,33.05,80.06,32.43,78.98,32.1L78.98,32.1z"
                        />
                      </g>
                    </svg>
                  </div>
                  <div class="mt-3 text-center sm:mt-0 sm:ml-4 ">
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
                        class="form flex flex-col bg-white p-6 pl-0 relative lg:rounded-xl justify-center "
                      >
                        <div
                          class="flex flex-col md:flex-row  mt-3 justify-stretch lg:flex-col"
                        >
                          <input
                            type="text"
                            name="employeeName"
                            id=""
                            required
                            placeholder="Nome *"
                            class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                          />

                          <input
                            type="text"
                            name="employeeSurname"
                            id=""
                            required
                            placeholder="Cognome *"
                            class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                          />

                          <input
                            type="number"
                            name="employeeID"
                            id=""
                            required
                            placeholder="Matricola *"
                            class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                          />

                          <input
                            type="email"
                            name="employeeEmail"
                            id=""
                            required
                            placeholder="Email *"
                            class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                          />
                        </div>
                      </form>
                    </div>
                  </div>
                </div>
              </div>
              <div
                class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse"
              >
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
                    <svg
                      version="1.1"
                      id="Layer_1"
                      xmlns="http://www.w3.org/2000/svg"
                      xmlns:xlink="http://www.w3.org/1999/xlink"
                      x="0px"
                      y="0px"
                      viewBox="0 0 122.88 110.9"
                      style="enable-background:new 0 0 122.88 110.9"
                      xml:space="preserve"
                    >
                      <g>
                        <path
                          class="st0"
                          d="M13.09,35.65h30.58V23.2l34.49,0v12.45l31.47,0L61.39,82.58L13.09,35.65L13.09,35.65z M61.44,97.88l47.51-0.14 l4.54-21.51l9.38,0.31v34.36L0,110.9V76.55l9.39-0.31l4.54,21.51L61.44,97.88L61.44,97.88L61.44,97.88z M43.67,0h34.49v4.62H43.67 V0L43.67,0z M43.67,9.32h34.49v9.44H43.67V9.32L43.67,9.32z"
                        />
                      </g>
                    </svg>
                  </div>
                  <div class="mt-3 text-center sm:mt-0 sm:ml-4 ">
                    <h3
                      class="text-lg leading-6 font-medium text-gray-900 text-left"
                      id="modal-headline"
                    >
                      Importa dipendenti
                    </h3>
                    <template v-if="fileUploaded != null"
                      ><div class="pt-2">
                        <span class=" text-left text-lg"> {{ fileName }} </span
                        ><span
                          @click="removeFile()"
                          class="text-danger cursor-pointer"
                        >
                          rimuovi</span
                        >
                      </div></template
                    >
                    <template v-else>
                      <div class="mt-2">
                        <div
                          class="p-12  border-gray-300 border-dashed border-8 border-primary"
                          :class="
                            inDragArea ? ' bg-primary_light' : 'bg-background'
                          "
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

                          <label
                            for="fileUploader"
                            class="block cursor-pointer"
                          >
                            <div>
                              Trascina qui il file
                              <span class="font-semibold">.csv</span> oppure
                              <span class=" text-primary">clicca qui</span> per
                              caricarlo
                            </div>
                          </label>
                        </div>
                      </div></template
                    >
                  </div>
                </div>
              </div>
              <div
                class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse"
              >
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

      <table class="table-auto rounded relative w-full">
        <thead class="text-center justify-between">
          <tr
            class="truncate px-2  flex border-b border-background text-center"
          >
            <th class="w-1/5">
              Nome
            </th>

            <th class=" w-1/5">
              Cognome
            </th>
            <th class="w-1/5">
              Matricola
            </th>
            <th class="w-1/5">
              Email
            </th>
            <th class="w-1/5"></th>
          </tr>
        </thead>
        <tbody class="bg-white text-center justify-between">
          <template v-for="(employee, index) in employees">
            <template
              v-if="
                (selectedHq == 0 || employee.idSede == selectedHq) &&
                  (selectedCampaign == 0 || isEmployeeInCampaign(employee))
              "
            >
              <tr
                class=" text-center m-auto truncate px-2 select-none cursor-pointer flex items-center border-b border-background hover:bg-background transition ease-in duration-100 "
                :key="employee.id"
                tag="tr"
                :id="employee.id"
                @click="showInfoBox(index)"
              >
                <td class=" w-1/5">
                  <p class="text-gray-800 text-sm font-semibold text-center">
                    {{ employee.first_name }}
                  </p>
                </td>
                <td class=" w-1/5">
                  <p class="text-gray-800 text-sm font-semibold text-center">
                    {{ employee.last_name }}
                  </p>
                </td>
                <td class=" w-1/5">
                  <p class="text-gray-800 text-sm font-semibold text-center">
                    {{ employee.matricola }}
                  </p>
                </td>
                <td class=" w-1/5">
                  <p
                    class="text-gray-800 text-sm font-semibold text-center truncate"
                  >
                    {{ employee.email }}
                  </p>
                </td>
                <td class="flex  items-end w-1/5 pr-12">
                  <svg
                    class="mr-3 md:mr-1 h-12 w-6 fill-current text-grey-dark m-auto "
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 20 20"
                  >
                    <path
                      d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                    />
                  </svg>
                </td></tr
            ></template>
          </template>
        </tbody>
      </table>
      <div class="ml-auto pt-4 pr-4">
        <button
          @click="modalInsertEmployeeOpen = !modalInsertEmployeeOpen"
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <svg
            viewBox="0 0 20 20"
            enable-background="new 0 0 20 20"
            class="w-6 h-6 inline-block"
          >
            <path
              fill="#FFFFFF"
              d="M16,10c0,0.553-0.048,1-0.601,1H11v4.399C11,15.951,10.553,16,10,16c-0.553,0-1-0.049-1-0.601V11H4.601
                                    C4.049,11,4,10.553,4,10c0-0.553,0.049-1,0.601-1H9V4.601C9,4.048,9.447,4,10,4c0.553,0,1,0.048,1,0.601V9h4.399
                                    C15.952,9,16,9.447,16,10z"
            />
          </svg>
        </button>
      </div>
    </div>
    <infobox ref="infobox" />
  </div>
</template>

<script>
import Infobox from "../components/Infobox.vue";
import { employees } from "../tmp-data/employees.js";
import { headquarters } from "../tmp-data/hqs.js";
import { campaigns } from "../tmp-data/campaigns.js";
export default {
  components: { Infobox },
  name: "Sede",
  data: function() {
    return {
      employees: [],
      selectedHq: 0,
      selectedCampaign: 0,
      filter_campaigns: [],
      filter_hqs: [],
      modalInsertEmployeeOpen: false,
      modalImportEmployeesOpen: false,
      fileUploaded: null,
      inDragArea: false,
    };
  },

  mounted: function() {
    this.employees = employees;
    console.log(this.$refs["infobox"].$el.getBoundingClientRect());
    this.filter_hqs = headquarters;
    this.filter_campaigns = campaigns;
  },
  computed: {
    fileName() {
      return this.fileUploaded.item(0).name;
    },
  },
  methods: {
    showInfoBox: function(index) {
      this.$refs["infobox"].showEmployeeDetails(this.employees[index]);
    },

    hasCampaignNotEnded: function(campaign) {
      console.log(campaign);
      return new Date(campaign.endDate) > new Date();
    },
    isEmployeeInCampaign: function(employee) {
      let toRtn = false;
      if (this.selectedCampaign == 0) return true;
      employee.campagne.forEach((campaign) => {
        if (campaign.id == this.selectedCampaign) {
          toRtn = true;
        }
      });

      return toRtn;
    },
    onAddEmployeeSubmit: function(submitEvent) {
      console.log(submitEvent);
      let employee = {};
      let tmp = submitEvent.target.elements;
      employee.first_name = tmp.employeeName.value;
      employee.last_name = tmp.employeeSurname.value;
      employee.id = this.employees.length + 1;
      employee.email = tmp.employeeEmail.value;
      employee.matricola = tmp.employeeID.value;
      employee.campagne = [];
      employee.idSede = 0;
      console.log(employee);
      this.employees.push(employee);
      this.modalInsertEmployeeOpen = false;
    },

    onFileUploaderChange: function() {
      console.log(this.$refs["file"]);
      this.fileUploaded = this.$refs["file"].files;
    },
    removeUploadedFile: function() {
      this.fileUploaded = null;
    },

    dragover: function() {
      this.inDragArea = true;
    },
    dragleave: function() {
      this.inDragArea = false;
    },
    drop: function(event) {
      event.preventDefault();
      this.inDragArea = false;

      this.$refs["file"].files = event.dataTransfer.files;
      this.onFileUploaderChange();
    },
    importEmployees: function() {
      console.log(this.fileUploaded);
      this.modalImportEmployeesOpen = false;
    },
    removeFile: function() {
      this.fileUploaded = null;
    },
  },
};
</script>

<style></style>
