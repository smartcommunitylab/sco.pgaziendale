<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          x-large
          color="secondary"
          rounded
          elevation="6"
          @click="
            [
              selectActualLocation(null),
              openModal({ type: 'locationFormAdd', object: null }),
            ]
          "
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
          @click="openModal({ type: 'locationImport', object: null })"
        >
          <v-icon left>mdi-file-import</v-icon>
          Aggiungi da file
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
      <profilo-location v-if="actualLocation && actualLocation.item" />
    </v-row>
  </div>
</template>

<script>
import ProfiloLocation from "./Location.vue";
import { mapState, mapActions } from "vuex";
import GenericTable from "@/components/data-table/GenericTable.vue";
// import Modal from "@/components/modal/ModalStructure.vue";

export default {
  name: "Locations",
  components: { ProfiloLocation, GenericTable },
  data: function () {
    return {
      headerColumns: [
        { text: "Codice sede", value: "id" },
        { text: "Denomiazione", value: "name" },
        { text: "Cittá", value: "city" },
        { text: "Indirizzo", value: "address" },
        { text: "Numero", value: "streetNumber" },
      ],
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
      // fileUploaded: null,
      inDragArea: false,
      oldLocation: null,
    };
  },

  methods: {
    ...mapActions("modal", { openModal: "openModal" }),
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

          //this.actualLocation = undefined;
        } else {
          this.selectActualLocation(location);
          //this.actualLocation = location;
        }
      } else {
        this.selectActualLocation(location);
        //this.actualLocation = location;
      }
    },
    showModal(title) {
      this.editModalVisible = true;
      this.newLocation = true;
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
      this.modalImportLocationsOpen = false;
    },

    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteLocation({
        companyId: this.actualCompany.item.id,
        locationId: this.actualLocation.item.id,
      });
    },
    // importLocations: function () {
    //   console.log(this.fileUploaded);
    //   this.modalImportLocationsOpen = false;
    //   var formData = new FormData();
    //   formData.append("file", this.fileUploaded.item(0));
    //   this.importData({ companyId: this.actualCompany.item.id, file: formData });
    // },
  },

  computed: {
    ...mapState("location", ["allLocations", "actualLocation"]),
    ...mapState("company", ["actualCompany"]),
    ...mapState("account", ["user"]),

    // fileName() {
    //   return this.fileUploaded.item(0).name;
    // },
    nColsTable_calculator: function () {
      if (
        this.actualLocation != null &&
        this.actualLocation != undefined &&
        this.actualLocation.item != null
      ) {
        return 8;
      } else {
        return 12;
      }
    },
  },

  created() {
    this.loadLocations();
  },

  mounted() {
    this.changePage({ title: "Lista sedi", route: "/GestioneSedi" });
  },
};
</script>

<style></style>
