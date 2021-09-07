<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          x-large
          color="secondary"
          rounded
          elevation="6"
          @click="showModal('Aggiungi sede')"
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
          @click="modalImportLocationsOpen = true"
        >
          <v-icon left>mdi-file-import</v-icon>
          IMPORTA
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col :cols="nColsTable_calculator">
        <div v-if="allLocations && allLocations.items && allLocations.items.length > 0">
          <generic-table
            :items="allLocations.items"
            :headers="headerColumns"
            :title="tableTitle"
            :method="showLocationInfo"
          >
          </generic-table>
        </div>
        <div v-else class="empty-list">Non ci sono Sedi</div>
      </v-col>
      <!-- PROFILO LOCATION -->
      <profilo-location v-if="actualLocation && actualLocation.item"/> 
      <!-- MODALE LOCATION -->
      <modal v-show="deleteModalVisible">

      <template v-slot:header> <div class="text-danger">Cancella Sede </div></template>
      <template v-slot:body> 
        <p class="text-subtitle-1">Sei sicuro di voler cancellare la sede?</p>
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

      <template v-slot:header > {{ popup.title }} </template>

      <template v-slot:body>
        <location-form />
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
            @click="saveLocation"
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
          v-if="modalImportLocationsOpen"
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
                      Importa sedi
                    </h3>
                    <button
                      class="mx-2 inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-primary rounded shadow ripple hover:shadow-lg hover:bg-primary_light focus:outline-none"
                    >
                      <a href="/files/exampleLocations.csv" download>Scarica file di esempio</a>
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
                  @click="importLocations"
                  class="mx-2 inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-primary rounded shadow ripple hover:shadow-lg hover:bg-primary_light focus:outline-none"
                >
                  Importa sedi
                </button>
                <button
                  @click="modalImportLocationsOpen = false"
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
import ProfiloLocation from "./ProfiloLocation.vue";
import { mapState, mapActions } from "vuex";
import GenericTable from "@/components/GenericTable.vue";
import Modal from "@/components/Modal.vue";
import EventBus from "@/components/eventBus";
import LocationForm from "./LocationForm.vue";
export default {
  name: "Locations",
  components: { ProfiloLocation, GenericTable, Modal, LocationForm },
  data: function () {
    return {
      headerColumns: [{text:"Identificativo", value:"id"}, {text:"Cittá", value:"city"}, {text:"Indirizzo", value:"address"}, {text:"Numero", value:"streetNumber"}],
      tableTitle: "Sedi",
      newLocation: false,
      location: null,
      popup: {
        title: "",
      },
      modalImportLocationsOpen: false,
      deleteModalVisible: false,
      editModalVisible: false,
      submitStatus: null,
      fileUploaded: null,
      inDragArea: false,
      oldLocation:null
    };
  },

  created() {
    this.loadLocations();
  },
  mounted() {
    this.changePage({ title: "Lista sedi", route: "/locations" });
    EventBus.$on("EDIT_LOCATION", (location) => {
      this.editModalVisible = true;
      EventBus.$emit("EDIT_LOCATION_FORM", location.item);
      this.oldLocation=location.item;
      this.popup = {
        title: "Modifica",
      };
    });
    EventBus.$on("DELETE_LOCATION", (location) => {
      this.deleteModalVisible = true;
      this.location = location.item;
      this.popup = {
        title: "Cancella",
      };
    });
    EventBus.$on("OK_LOCATION_FORM", (location) => {
      if (this.newLocation) {
        this.addLocationCall({
          companyId: this.actualCompany.item.id,
          location: location,
        });
      } else {
        this.updateLocationCall({
          companyId: this.actualCompany.item.id,
          location: location,
          oldLocation:this.oldLocation
        });
      }
      this.editModalVisible = false;
      this.newLocation = false;
    });
    EventBus.$on("NO_LOCATION_FORM", () => {
      this.submitStatus = "ERROR";
    });
  },
  beforeDestroy() {
    EventBus.$off("NO_LOCATION_FORM");
    EventBus.$off("OK_LOCATION_FORM");
    EventBus.$off("DELETE_LOCATION");
    EventBus.$off("EDIT_LOCATION");
  },
  computed: {
    ...mapState("location", ["allLocations", "actualLocation"]),
    ...mapState("company", ["actualCompany"]),
    ...mapState("account", ["role"]),
        fileName() {
      return this.fileUploaded.item(0).name;
    },
    nColsTable_calculator: function() {
      if(this.actualLocation != null && this.actualLocation != undefined && this.actualLocation.item != null){
        return 8;
      } else{
        return 12;
      }
    },
  },
  methods: {
    ...mapActions("location", {
      getAllLocations: "getAllLocations",
      selectActualLocation: "selectActualLocation",
      addLocationCall: "addLocation",
      updateLocationCall: "updateLocation",
      deleteLocation: "deleteLocation",
      importData: "importLocations",
    }),
    ...mapActions("navigation", { changePage: "changePage" }),

    loadLocations() {
      if (this.actualCompany) this.getAllLocations(this.actualCompany.item.id);
    },

    showLocationInfo: function (location) {
      console.log("Questo è il valore di actualLocation:");
      console.log(this.actualLocation);
      console.log("Questo è il valore di location:");
      console.log(location);
      if (this.actualLocation != null) {
        if (this.actualLocation.item == location) {
          this.selectActualLocation(null);

          this.actualLocation = undefined;
        } else {
          this.selectActualLocation(location);
          this.actualLocation = location;
        }
      }else{
        this.selectActualLocation(location);
        this.actualLocation = location;
      }
      
    },
    showModal(title) {
      this.editModalVisible = true;
      this.newLocation = true;
      EventBus.$emit("NEW_LOCATION_FORM");
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.editModalVisible = false;
      this.newLocation = false;
    },
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },

    saveLocation() {
      //check fields
      EventBus.$emit("CHECK_LOCATION_FORM");
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteLocation({
        companyId: this.actualCompany.item.id,
        locationId: this.actualLocation.item.id,
      });
    },
    // updateLocation() {
    //   //check fields
    //   // eslint-disable-next-line no-constant-condition
    //   if (true) {
    //     this.updateLocationCall(this.location);
    //     this.editModalVisible = false;
    //   }
    // },
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
    importLocations: function () {
      console.log(this.fileUploaded);
      this.modalImportLocationsOpen = false;
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

<style></style>
