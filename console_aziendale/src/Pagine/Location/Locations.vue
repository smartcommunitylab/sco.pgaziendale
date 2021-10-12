<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          x-large
          color="secondary"
          rounded
          elevation="6"
          @click="openModal({type:'locationFormAdd', object:null})"
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
          @click="openModal({type:'locationImport', object:null})"
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
    </v-row>

    <modal v-show="modalImportLocationsOpen">

      <template v-slot:header> Importa Sedi </template>


      <template v-slot:body> 
        <v-row
          justify="center"
          class="mt-5 mb-8"
        >
          <v-btn
            outlined
            color="primary"
          >
            <a href="/files/exampleLocations.csv" download>Scarica file di esempio</a>
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
              @change="/*onFileUploaderChange*/"
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
            @click="importLocations"
            class="py-8 ml-8"
          >
            Importa Sedi
          </v-btn>
      </template>
    </modal>
  </div>
</template>

<script>
import ProfiloLocation from "./ProfiloLocation.vue";
import { mapState, mapActions } from "vuex";
import GenericTable from "@/components/GenericTable.vue";
import Modal from "@/components/Modal.vue";
import EventBus from "@/components/eventBus";

export default {
  name: "Locations",
  components: { ProfiloLocation, GenericTable, Modal },
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

  methods: {
    ...mapActions("modal", {openModal:'openModal'}),
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
      this.$v.$reset();
      //this.initLocation();
    },
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
    closeImportModal() {
      this.modalImportLocationsOpen = false
    },
    saveLocation() {
      //check fields
      if (!this.$v.$invalid) {
          EventBus.$emit("CHECK_LOCATION_FORM");      
      }else{
        this.$v.$touch();
      }
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteLocation({
        companyId: this.actualCompany.item.id,
        locationId: this.actualLocation.item.id,
      });
    },
    importLocations: function () {
      console.log(this.fileUploaded);
      this.modalImportLocationsOpen = false;
      var formData = new FormData();
      formData.append("file", this.fileUploaded.item(0));
      this.importData({ companyId: this.actualCompany.item.id, file: formData });
    },
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
};
</script>

<style>
</style>