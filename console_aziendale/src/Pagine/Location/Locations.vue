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
        <form action="" id="addLocation">
          <div class="mb-20 flex flex-wrap justify-between">
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.id.$error }">
                <label class="field-label" for="first_name">Identificativo </label>
                <input
                  type="text"
                  name="campaignCode"
                  placeholder="Identificativo *"
                  v-model.trim="$v.id.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="campaignCode"
                />
              </div>
              <div v-if="$v.id.$error">
                <div class="error" v-if="!$v.id.required">
                  Il campo Identificativo e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.address.$error }">
                <label class="field-label" for="first_name">Indirizzo </label>
                <input
                  type="text"
                  name="campaignAddress"
                  placeholder="Indirizzo *"
                  v-model.trim="$v.address.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="campaignTitle"
                />
              </div>
              <div v-if="$v.address.$error">
                <div class="error" v-if="!$v.address.required">
                  Il campo indirizzo e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.streetNumber.$error }"
              >
                <label class="field-label" for="first_name">Numero</label>
                <input
                  type="text"
                  name="campaignstreetNumber"
                  id="campaignstreetNumber"
                  placeholder="Number *"
                  v-model.trim="$v.streetNumber.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.streetNumber.$error">
                <div class="error" v-if="!$v.streetNumber.required">
                  Il campo number e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.zip.$error }">
                <label class="field-label" for="password">CAP </label>
                <input
                  type="text"
                  name="campaignZip"
                  id=""
                  required
                  placeholder="CAP *"
                  v-model.trim="$v.zip.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.zip.$error">
                <div class="error" v-if="!$v.zip.required">
                  Il campo CAP e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.city.$error }">
                <label class="field-label" for="password">Cittá </label>
                <input
                  type="text"
                  name="campaignCity"
                  id=""
                  required
                  placeholder="Cittá *"
                  v-model.trim="$v.city.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.city.$error">
                <div class="error" v-if="!$v.city.required">
                  Il campo Cittá e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.province.$error }"
              >
                <label class="field-label" for="password">Provincia</label>
                <input
                  type="text"
                  name="campaignProvince"
                  id=""
                  required
                  placeholder="Provincia *"
                  v-model.trim="$v.province.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.province.$error">
                <div class="error" v-if="!$v.province.required">
                  Il campo Provincia e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.region.$error }">
                <label class="field-label" for="password">Regione</label>
                <input
                  type="text"
                  name="campaignRegion"
                  id=""
                  required
                  placeholder="Regione *"
                  v-model.trim="$v.region.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.region.$error">
                <div class="error" v-if="!$v.region.required">
                  Il campo Regione e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.country.$error }">
                <label class="field-label" for="password">Stato</label>
                <input
                  type="text"
                  name="campaignCountry"
                  id=""
                  required
                  placeholder="Stato *"
                  v-model.trim="$v.country.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.country.$error">
                <div class="error" v-if="!$v.country.required">
                  Il campo Stato e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.radius.$error }">
                <label class="field-label" for="password">Raggio</label>
                <input
                  type="text"
                  name="campaignRadius"
                  id=""
                  required
                  placeholder="Raggio *"
                  v-model.trim="$v.radius.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.radius.$error">
                <div class="error" v-if="!$v.radius.required">
                  Il campo Raggio e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.latitude.$error }"
              >
                <label class="field-label" for="password">Latitudine</label>
                <input
                  type="text"
                  name="campaignMeans"
                  id=""
                  required
                  placeholder="Mezzi *"
                  v-model.trim="$v.latitude.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.latitude.$error">
                <div class="error" v-if="!$v.latitude.required">
                  Il campo Latitudine e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.longitude.$error }"
              >
                <label class="field-label" for="password">Longitudine</label>
                <input
                  type="text"
                  name="campaignLongitude"
                  id=""
                  required
                  placeholder="Longitudine *"
                  v-model.trim="$v.longitude.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.longitude.$error">
                <div class="error" v-if="!$v.longitude.required">
                  Il campo Longitudine e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.nonWorking.$error }"
              >
                <label class="field-label" for="password">Giorni NON lavorativi</label>
                <input
                  type="text"
                  name="campaignNonWorking"
                  id=""
                  required
                  placeholder="Giorni NON lavorativi *"
                  v-model.trim="$v.nonWorking.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.nonWorking.$error">
                <div class="error" v-if="!$v.nonWorking.required">
                  Il campo Giorni NON lavorativi e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.nonWorkingDays.$error }"
              >
                <label class="field-label" for="password">Giorni di chiusura</label>
                <input
                  type="text"
                  name="campaignNonWorkingDays"
                  id=""
                  required
                  placeholder="Giorni di chiusura *"
                  v-model.trim="$v.nonWorkingDays.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.nonWorkingDays.$error">
                <div class="error" v-if="!$v.nonWorkingDays.required">
                  Il campo Giorni di chiusura e' richiesto.
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
import { required } from "vuelidate/lib/validators";
import Modal from "@/components/Modal.vue";
import EventBus from "@/components/eventBus";

export default {
  name: "Locations",
  components: { ProfiloLocation, GenericTable, Modal },
  data: function () {
    return {
      gridColumns: ["city", "address"],
      headerColumns: ["Cittá", "Indirizzo"],
      newLocation: false,
      location: {},
      popup: {
        title: "",
      },
      id: "",
      address: "",
      streetNumber: "",
      zip: "",
      city: "",
      province: "",
      region: "",
      latitude: 0,
      longitude: 0,
      country: "",
      radius: 200,
      nonWorkingDays: [],
      nonWorking: [],
      deleteModalVisible: false,
      editModalVisible: false,
      submitStatus: null,
    };
  },
  validations: {
    id: {
      required,
    },
    address: {
      required,
    },
    streetNumber: {
      required,
    },
    zip: {
      required,
    },
    city: {
      required,
    },
    province: {
      required,
    },
    region: {
      required,
    },
    country: {
      required,
    },
    radius: {
      required,
    },
    latitude: {
      required,
    },
    longitude: {
      required,
    },
    nonWorkingDays: {
      required,
    },
    nonWorking: {
      required,
    },
  },
  created() {
    this.loadLocations();
  },
  mounted() {
    this.changePage({ title: "Lista sedi", route: "/locations" });
    EventBus.$on("EDIT_LOCATION", (location) => {
      this.editModalVisible = true;
      this.location = location.item;
            this.copyFormValues();

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
      this.initLocation();
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
            initLocation() {
 this.id="";
       this.address="";
       this.streetNumber="";
       this.zip="";
       this.city="";
       this.province="";
       this.region="";
       this.latitude= 0;
       this.longitude= 0;
       this.country="";
       this.radius=200;
       this.nonWorkingDays=[];
       this.nonWorking=[];
      },
      copyFormValues() {
      for (const [key] of Object.entries(this.location)) {
        this[key]=this.location[key]}
},

    createLocation() {
      this.location = {
        id: this.id,
        address: this.address,
        streetNumber: this.streetNumber,
        zip: this.zip,
        city: this.city,
        province: this.province,
        region: this.region,
        latitude: Number.parseFloat(this.latitude),
        longitude: Number.parseFloat(this.longitude),
        // nonWorkingDays:this.nonWorkingDays,
        // nonWorking:this.nonWorking
        nonWorkingDays: [],
        nonWorking: [],
        country: this.country,
        radius: Number.parseInt(this.radius),
      };
    },
    saveLocation() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      // console.log("submit!");
      this.$v.$touch();
      if (this.$v.$invalid) {
        this.submitStatus = "ERROR";
        return;
      } else {
        this.createLocation();
        if (this.newLocation) {
          this.addLocationCall({
            companyId: this.actualCompany.item.id,
            location: this.location,
          });
        } else {
          this.updateLocationCall({
            companyId: this.actualCompany.item.id,
            location: this.location,
          });
        }
        this.$v.$reset();
      }

      this.editModalVisible = false;
      this.newLocation = false;
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteLocation({
        companyId: this.actualCompany.item.id,
        locationId: this.actualLocation.item.id,
      });
    },
    updateLocation() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      if (true) {
        this.updateLocationCall(this.location);
        this.editModalVisible = false;
      }
    },
  },
};
</script>

<style></style>
