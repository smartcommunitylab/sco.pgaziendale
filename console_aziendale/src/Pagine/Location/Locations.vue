<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2 pb-16 relative">
      <div v-if="allLocations && allLocations.items && allLocations.items.length > 0">
        <generic-table
          :data="allLocations.items"
          :columns="gridColumns"
          :header="headerColumns"
          :method="showLocationInfo"
        >
        </generic-table>
      </div>
      <div v-else class="text-center">Non ci sono Sedi</div>

      <div class="ml-auto pt-4 pr-4 absolute right-0">
        <button
          @click="showModal('Aggiungi sede')"
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <add-icon class="add-icon" />
        </button>
      </div>
    </div>

    <profilo-location v-if="actualLocation" />
    <modal v-show="deleteModalVisible">
      <template v-slot:header> Cancella Sede </template>
      <template v-slot:body> Sei sicuro di voler cancellare la sede? </template>
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
        <location-form />
      </template>
      <template v-slot:footer>
        <button
          type="button"
          class="btn-close"
          @click="saveLocation"
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
      gridColumns: ["id", "city", "address"],
      headerColumns: ["Identificativo", "Cittá", "Indirizzo"],
      newLocation: false,
      location: null,
      popup: {
        title: "",
      },

      deleteModalVisible: false,
      editModalVisible: false,
      submitStatus: null,
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
  },
  methods: {
    ...mapActions("location", {
      getAllLocations: "getAllLocations",
      selectActualLocation: "selectActualLocation",
      addLocationCall: "addLocation",
      updateLocationCall: "updateLocation",
      deleteLocation: "deleteLocation",
    }),
    ...mapActions("navigation", { changePage: "changePage" }),

    loadLocations() {
      if (this.actualCompany) this.getAllLocations(this.actualCompany.item.id);
    },

    showLocationInfo(location) {
      this.selectActualLocation(location);
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
  },
};
</script>

<style></style>
