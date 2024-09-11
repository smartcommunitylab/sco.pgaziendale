<template>
  <modal>
    <template v-slot:header> {{ popup.title }} </template>
    <template v-slot:body>
      <form action="" id="addLocation">
        <div class="mb-20">
          <div>
            <v-row>
              <v-col cols="12">
                <v-divider></v-divider>
                <p class="text-subtitle-1 mt-5">Dati Sede</p>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="6">
                <v-text-field
                  label="Identificativo"
                  placeholder="Identificativo *"
                  type="text"
                  name="campaignCode"
                  id="campaignCode"
                  autocomplete="null"
                  :disabled="disabled"
                  v-model.trim="$v.id.$model"
                  :error-messages="idErrors"
                  @input="$v.id.$touch()"
                  @blur="$v.id.$touch()"
                  outlined
                >
                  <template v-slot:append>
                    <v-tooltip left nudge-bottom="50px" v-if="$v.id.$model == ''">
                      <template v-slot:activator="{ on }">
                        <v-icon v-on="on"> mdi-help-circle-outline </v-icon>
                      </template>
                      <div class="tooltip">
                        <p>ATTENZIONE!!!</p>
                        <p>
                          Il codice identifcativo sede
                          <b>DEVE ESSERE UNIVOCO E DEVE CORRISPONDERE</b> esattamente a
                          quello utilizzato per associare i dipendenti ad una sede (sia
                          nell’edit manuale dei dipendenti sia nell’import dipendenti d
                          file)
                        </p>
                      </div>
                    </v-tooltip>
                  </template>
                </v-text-field>
              </v-col>
              <v-col cols="4">
                <v-text-field
                  label="Denominazione"
                  placeholder="Denominazione *"
                  type="text"
                  name="denominazione"
                  id="denominazione"
                  autocomplete="null"
                  v-model.trim="$v.name.$model"
                  :error-messages="nameErrors"
                  required
                  @input="$v.name.$touch()"
                  @blur="$v.name.$touch()"
                  outlined
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                <v-divider></v-divider>
                <p class="text-subtitle-1 mt-5">Indirizzo</p>
              </v-col>
            </v-row>

            <v-row>
              <v-col cols="4">
                <v-text-field
                  label="Indirizzo"
                  placeholder="Indirizzo *"
                  type="text"
                  name="campaignAddress"
                  id="campaignAddress"
                  autocomplete="null"
                  v-model.trim="$v.address.$model"
                  :error-messages="addressErrors"
                  required
                  @input="
                    $v.address.$touch();
                    changeAddress();
                  "
                  @blur="$v.address.$touch()"
                  outlined
                ></v-text-field>
              </v-col>
              <v-col cols="4">
                <v-text-field
                  label="Numero"
                  placeholder="Numero *"
                  type="text"
                  name="campaignstreetNumber"
                  id="campaignstreetNumber"
                  autocomplete="null"
                  v-model.trim="$v.streetNumber.$model"
                  :error-messages="streetNumberErrors"
                  required
                  @input="
                    $v.streetNumber.$touch();
                    changeAddress();
                  "
                  @blur="$v.streetNumber.$touch()"
                  outlined
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="4">
                <v-text-field
                  label="CAP"
                  placeholder="CAP *"
                  type="text"
                  name="campaignZip"
                  id="campaignZip"
                  autocomplete="null"
                  v-model.trim="$v.zip.$model"
                  :error-messages="zipErrors"
                  required
                  @input="
                    $v.zip.$touch();
                    changeAddress();
                  "
                  @blur="$v.zip.$touch()"
                  outlined
                ></v-text-field>
              </v-col>
              <v-col cols="4">
                <v-text-field
                  label="Cittá"
                  placeholder="Cittá *"
                  type="text"
                  name="campaignCity"
                  id="campaignCity"
                  autocomplete="null"
                  v-model.trim="$v.city.$model"
                  :error-messages="cityErrors"
                  required
                  @input="
                    $v.city.$touch();
                    changeAddress();
                  "
                  @blur="$v.city.$touch()"
                  outlined
                ></v-text-field>
              </v-col>
              <v-col cols="4">
                <v-autocomplete
                  label="Provincia"
                  placeholder="Provincia *"
                  name="campaignProvince"
                  id="campaignProvince"
                  autocomplete="null"
                  v-model.trim="$v.province.$model"
                  :items="listaProvince"
                  :error-messages="provinceErrors"
                  required
                  @input="
                    $v.province.$touch();
                    changeAddress();
                  "
                  @blur="$v.province.$touch()"
                  outlined
                ></v-autocomplete>
              </v-col>
            </v-row>
            <v-row>
              <v-col>
                <v-autocomplete
                  label="Regione"
                  placeholder="Regione *"
                  name="campaignRegion"
                  id="campaignRegion"
                  v-model.trim="$v.region.$model"
                  :items="listaRegioni"
                  :error-messages="regionErrors"
                  required
                  @input="
                    $v.region.$touch();
                    changeAddress();
                  "
                  @blur="$v.region.$touch()"
                  outlined
                ></v-autocomplete>
              </v-col>
              <v-col>
                <v-text-field
                  label="Stato"
                  placeholder="Stato *"
                  type="text"
                  name="campaignCountry"
                  id="campaignCountry"
                  autocomplete="null"
                  v-model.trim="$v.country.$model"
                  :error-messages="countryErrors"
                  required
                  @input="
                    $v.country.$touch();
                    changeAddress();
                  "
                  @blur="$v.country.$touch()"
                  outlined
                ></v-text-field>
              </v-col>
            </v-row>
          </div>
          <v-row>
            <v-col cols="12" class="mb-0">
              <p class="text-subtitle-1 mb-0">Localizzazione</p>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="8">
              <div class="map-style" v-if="locationSelected">
                <geolocation-selector
                  v-model="locationSelected"
                  ref="geolocationSelector"
                  :key="key"
                  :radius="radius"
                  v-on:poschanged="locationChanged"
                  :initialAddresIsValid="addresIsValid"
                  :inputAddress="{ address: inputAddress }"
                  :latLng="{ lat: latitude, lng: longitude }"
                  v-on:returnGeosearch="geoSearchResult"
                />
              </div>
            </v-col>
            <v-col cols="4">
              <div class="tab-container">
                <p v-if="!showErrorLocation">
                  Per poter impostare una posizione è necessario indicare l’indirizzo
                  della sede.
                </p>
                <p v-if="showErrorLocation">L'indirizzo inserito non permette il posizionamento automatico. Si prega di utilizzare il posizionamento manuale.
</p>
                <v-tabs v-model="tab" align-with-title>
                  <v-tab
                    key="1"
                    class="text-none"
                    @click="setTab(1)"
                  >
                    In automatico
                  </v-tab>
                  <v-tab
                    key="2"
                    class="text-none"
                    @click="setTab(2)"
                  >
                    A mano
                  </v-tab>
                </v-tabs>
                <v-tabs-items v-model="tab" class="mt-5">
                  <v-tab-item key="1">
                    <div>
                      <i
                        >Imposta automaticamente la posizione in base all’indirizzo
                        inserito</i
                      >
                    </div>
                    <div>
                      <v-btn
                        color="primary"
                          :disabled="!addresIsValid"
                        @click="autoPosition()"
                        >Imposta automaticamente</v-btn
                      >
                    </div>
                  </v-tab-item>
                  <v-tab-item key="2">
                    <div>
                      Imposta manualmente la posizione trascinando il Pin o cliccando
                      sulla mappa. Prima di confermare la nuova posizione verifica che
                      corrisponda alla reale posizione della sede
                    </div>
                    <div>
                      <v-btn
                        color="primary"
                        @click="manualPosition()"
                        :disabled="$v.$invalid"
                        >Imposta manualmente</v-btn
                      >
                    </div>
                  </v-tab-item>
                </v-tabs-items>
              </div>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="8" class="mt-6">
              <v-row class="p-0 mx-1">
                <v-col cols="4">
                  <v-text-field
                    label="Latitudine"
                    placeholder="Latitudine *"
                    type="text"
                    name="campaignLatitude"
                    id="campaignLatitude"
                    v-model.trim="$v.latitude.$model"
                    :error-messages="latitudeErrors"
                    required
                    :disabled="true"
                    @input="$v.latitude.$touch()"
                    @blur="$v.latitude.$touch()"
                    outlined
                  ></v-text-field>
                </v-col>
                <v-col cols="4">
                  <v-text-field
                    label="Longitudine"
                    placeholder="Longitudine *"
                    type="text"
                    name="campaignLongitude"
                    id="campaignLongitude"
                    v-model.trim="$v.longitude.$model"
                    :error-messages="longitudeErrors"
                    required
                    :disabled="true"
                    @input="$v.longitude.$touch()"
                    @blur="$v.longitude.$touch()"
                    outlined
                  ></v-text-field>
                </v-col>
                <v-col cols="4">
                  <v-text-field
                    label="Raggio"
                    placeholder="Raggio *"
                    type="text"
                    name="campaignRadius"
                    id="campaignRadius"
                    v-model.trim="$v.radius.$model"
                    :error-messages="radiusErrors"
                    :disabled="$v.radius.$model == 200"
                    required
                    @input="$v.radius.$touch()"
                    @blur="$v.radius.$touch()"
                    outlined
                  >
                    <template v-slot:append>
                      <v-tooltip bottom nudge-bottom="10" nudge-left="75">
                        <template v-slot:activator="{ on }">
                          <v-icon v-on="on"> mdi-help-circle-outline </v-icon>
                        </template>
                        Distanza in metri dalla sede all'interno di cui i viaggi dei
                        dipendenti risultano essere validi
                      </v-tooltip>
                    </template>
                  </v-text-field>
                </v-col>
              </v-row>
            </v-col>
          </v-row>

          <v-row>
            <v-col cols="4" class="mt-3">
              <v-form>
                <p class="text-subtitle-1">Giorni NON lavorativi</p>
                <div v-for="day in arrayDays" v-bind:key="day.value">
                  <v-checkbox
                    v-model="$v.nonWorking.$model"
                    :label="day.text"
                    :value="day.value"
                    hide-details
                  ></v-checkbox>
                </div>
              </v-form>
            </v-col>
            <v-spacer></v-spacer>
            <v-col cols="8">
              <v-menu
                ref="menu"
                v-model="menu"
                :close-on-content-click="false"
                :return-value.sync="nonWorkingDays"
                transition="scale-transition"
                offset-y
                min-width="auto"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-combobox
                    v-model="nonWorkingDays"
                    multiple
                    chips
                    label="Giorni di chiusura"
                    placeholder="Scegli i giorni di chiusura:"
                    prepend-icon="mdi-calendar"
                    deletable-chips
                    v-bind="attrs"
                    v-on="on"
                    ref="datepick"
                  >
                    <template v-slot:append>
                      <v-btn rounded color="primary" @click="openDatePicker()">
                        Aggiungi
                      </v-btn>
                    </template>
                  </v-combobox>
                  <button class="profile-button" v-bind="attrs" v-on="on"></button>
                </template>
                <v-date-picker v-model="nonWorkingDays" multiple scrollable no-title>
                  <v-spacer></v-spacer>
                  <v-btn text @click="menu = false"> Annulla </v-btn>
                  <v-btn text color="primary" @click="$refs.menu.save(nonWorkingDays)">
                    Salva
                  </v-btn>
                </v-date-picker>
              </v-menu>
            </v-col>
          </v-row>
        </div>
      </form>
    </template>
    <template v-slot:footer>
      <v-btn text @click="closeThisModal" class="py-8 ml-8"> Annulla </v-btn>
      <v-btn
        color="primary"
        text
        @click="saveLocation"
        class="py-8 ml-8"
        :disabled="!addresIsValid || $v.$invalid"
      >
        Salva
      </v-btn>
    </template>
  </modal>
</template>

<script>
import { validationMixin } from "vuelidate";
import { required, numeric } from "vuelidate/lib/validators";
import { locationService } from "@/services";
import GeoLocationSelectorMapVue from "@/components/leaflet-map/GeoLocationSelectorMap.vue";
import Modal from "@/components/modal/ModalStructure.vue";
import { mapActions, mapState } from "vuex";
export default {
  components: {
    "geolocation-selector": GeoLocationSelectorMapVue,
    modal: Modal,
  },

  props: { typeCall: String },

  mixins: [validationMixin],

  validations: {
    id: {
      required,
      unique() {
        return (
          (this.actualLocation &&
            this.actualLocation.item &&
            this.id === this.actualLocation.item.id) ||
          !this.allLocations.items.find((l) => l.id === this.id)
        );
      },
    },
    name: {
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
      numeric,
    },
    latitude: {
      required,
    },
    longitude: {
      required,
    },
    newNonWorkingDay: {},
    nonWorkingDays: {},
    nonWorking: {},
  },

  data() {
    return {
      popup: {
        title: "",
      },
      showErrorLocation: false,
      timerId: null,
      geoResults: [],
      addresIsValid: false,
      inputAddress: "",
      tab: null,
      id: "",
      address: "",
      name: "",
      streetNumber: "",
      zip: "",
      city: "",
      province: "",
      region: "",
      latitude: "",
      longitude: "",
      country: "",
      radius: 200,
      nonWorkingDays: [],
      nonWorking: [],
      arrayDays: [],
      datepicker: null,
      menu: false,
      zoom: 13,
      url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
      attribution:
        '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
      currentZoom: 11.5,
      showParagraph: false,
      mapOptions: {
        zoomSnap: 0.5,
      },
      selectedPosition: false,
      key: 1,
      locationSelected: {},
      listaProvince: [
        "AG",
        "AL",
        "AN",
        "AO",
        "AR",
        "AP",
        "AT",
        "AV",
        "BA",
        "BT",
        "BL",
        "BN",
        "BG",
        "BI",
        "BO",
        "BZ",
        "BS",
        "BR",
        "CA",
        "CL",
        "CB",
        "CE",
        "CT",
        "CZ",
        "CH",
        "CO",
        "CS",
        "CR",
        "KR",
        "CN",
        "EN",
        "FM",
        "FE",
        "FI",
        "FG",
        "FC",
        "FR",
        "GE",
        "GO",
        "GR",
        "IM",
        "IS",
        "AQ",
        "SP",
        "LT",
        "LE",
        "LC",
        "LI",
        "LO",
        "LU",
        "MC",
        "MN",
        "MS",
        "MT",
        "ME",
        "MI",
        "MO",
        "MB",
        "NA",
        "NO",
        "NU",
        "OR",
        "PD",
        "PA",
        "PR",
        "PV",
        "PG",
        "PU",
        "PE",
        "PC",
        "PI",
        "PT",
        "PN",
        "PZ",
        "PO",
        "RG",
        "RA",
        "RC",
        "RE",
        "RI",
        "RN",
        "RM",
        "RO",
        "SA",
        "SS",
        "SV",
        "SI",
        "SR",
        "SO",
        "SU",
        "TA",
        "TE",
        "TR",
        "TO",
        "TP",
        "TN",
        "TV",
        "TS",
        "UD",
        "VA",
        "VE",
        "VB",
        "VC",
        "VR",
        "VV",
        "VI",
        "VT",
      ],
      listaRegioni: [
        "Abruzzo",
        "Basilicata",
        "Calabria",
        "Campania",
        "Emilia-Romagna",
        "Friuli-Venezia Giulia",
        "Lazio",
        "Liguria",
        "Lombardia",
        "Marche",
        "Molise",
        "Piemonte",
        "Puglia",
        "Sardegna",
        "Sicilia",
        "Toscana",
        "Trentino-Alto Adige",
        "Umbria",
        "Valle d'Aosta Veneto",
      ],
      giorniSettimana: [
        {
          1: "Lunedì",
          2: "Martedì",
          3: "Mercoledì",
          4: "Giovedì",
          5: "Venerdì",
          6: "Sabato",
          7: "Domenica",
        },
      ],
      disabled: false,
    };
  },

  methods: {
    ...mapActions("modal", { closeModal: "closeModal" }),
    ...mapActions("location", {
      addLocation: "addLocation",
      updateLocation: "updateLocation",
    }),
    geoSearchResult(results) {
      console.log(results);
      if (!this.$v.$invalid) {
        if (results.length > 0) {
          this.addresIsValid = true;
          this.geoResults = results;
          this.showErrorLocation=false;

        } else {
          this.showErrorLocation=true;
          this.addresIsValid = false;
        }
      }
    },
    setTab(value) {
      if (value == 1) {
        this.$refs.geolocationSelector.disableMap();
        if (!this.$v.$invalid) {
        if (this.geoResults.length > 0) {
          this.addresIsValid = true;
          this.showErrorLocation=false;

        } else {
          this.showErrorLocation=true;
          this.addresIsValid = false;
        }
      }
      } else {
        this.$refs.geolocationSelector.enableMap();
        }
    },
    autoPosition() {
      if (this.geoResults.length > 0) {
        console.log("AutoPosition", this.geoResults[0]);
        // this.locationChanged(this.geoResults[0]);
        this.$refs.geolocationSelector.onSearch({ location: this.geoResults[0] });
      }
    },
    manualPosition() {
      this.$refs.geolocationSelector.enableMap();
      this.addresIsValid =true;
      console.log("ManualPosition",this.addresIsValid);
    },
    locationChanged(input) {
      console.log("Changed", input.address);
      this.locationSelected = input?.address;
      this.latitude = this.locationSelected?.pos?.lat;
      this.longitude = this.locationSelected?.pos?.lng;
      if (!this.address && this.locationSelected && this.locationSelected.structuredValue)
        this.changeParamForm(this.locationSelected?.structuredValue);
    },
    changeParamForm(structuredValue) {
      if (structuredValue.road) this.address = structuredValue.road;
      if (structuredValue.house_number) this.streetNumber = structuredValue.house_number;
      if (structuredValue.city) this.city = structuredValue.city;
      if (structuredValue.country) this.country = structuredValue.country;
      if (structuredValue.postcode) this.zip = structuredValue.postcode;
      if (structuredValue.state) this.region = structuredValue.state;
      if (!this.region && structuredValue.county) this.region = structuredValue.county;
    },
    stopTheEvent(event) {
      console.log(event);
      event.stopPropagation();
    },
    addDays(day) {
      if (this.nonWorkingDays == null) this.nonWorkingDays = [];
      if (!this.nonWorkingDays.includes(day)) this.nonWorkingDays.push(day);
      else {
        //giorno gia' inserito
        console.log("giorno gia' inserito");
      }
    },
    removeDay(day) {
      this.nonWorkingDays = this.nonWorkingDays.filter((elem) => elem != day);
    },
    copyFormValues(location) {
      for (const [key] of Object.entries(location)) {
        this[key] = location[key];
      }
      this.createLocation();
    },
    initLocation() {
      this.locationSelected = {};
      this.id = "";
      this.address = "";
      this.streetNumber = "";
      this.zip = "";
      this.city = "";
      this.province = "";
      this.region = "";
      this.latitude = "";
      this.longitude = "";
      this.country = "";
      this.radius = 200;
      this.nonWorkingDays = [];
      this.nonWorking = [];
      this.locationSelected = {
        id: "",
        address: "",
        streetNumber: "",
        zip: "",
        city: "",
        province: "",
        region: "",
        latitude: "",
        longitude: "",
        nonWorking: [],
        nonWorkingDays: [],
        country: "",
        radius: 200,
      };
    },
    createLocation() {
      this.locationSelected = {
        id: this.id,
        address: this.address,
        name: this.name,
        streetNumber: this.streetNumber,
        zip: this.zip,
        city: this.city,
        province: this.province,
        region: this.region,
        latitude: Number.parseFloat(this.latitude),
        longitude: Number.parseFloat(this.longitude),
        nonWorking: this.nonWorking,
        nonWorkingDays: this.nonWorkingDays,
        country: this.country,
        radius: Number.parseInt(this.radius),
      };
    },
    changeAddress() {
      this.inputAddress =
        this.address +
        " " +
        this.streetNumber +
        ", " +
        this.zip +
        ", " +
        this.city +
        ", " +
        this.province +
        ", " +
        this.region +
        ", " +
        this.country;
      this.$refs.geolocationSelector.changeAddress(this.inputAddress);
    },
    setModalData() {
      if (this.typeCall == "add") {
        this.initLocation();
        this.popup.title = "Aggiungi Sede";
        console.log("Modalità AGGIUNGI");
      } else if (this.typeCall == "edit") {
        if (this.actualLocation.item) this.copyFormValues(this.actualLocation.item);
        this.popup.title = "Modifica Sede";
        console.log("Modalità MODIFICA");
      }
    },
    saveLocation() {
      if (!this.$v.$invalid) {
        this.createLocation();
        if (this.typeCall == "add") {
          this.addLocation({
            companyId: this.actualCompany.item.id,
            location: this.locationSelected,
          });
          this.closeModal();
        } else if (this.typeCall == "edit") {
          console.log(this.locationSelected);
          this.updateLocation({
            companyId: this.actualCompany.item.id,
            location: this.locationSelected,
            oldLocation: this.actualLocation.item,
          });
          this.closeModal();
        }
      } else {
        this.$v.$touch();
      }
    },
    closeThisModal() {
      this.$v.$reset();
      this.closeModal();
      this.initLocation();
    },
    openDatePicker() {
      this.menu = true;
    },
  },

  computed: {
    ...mapState("location", ["actualLocation", "allLocations"]),
    ...mapState("modal", ["active"]),
    ...mapState("company", ["actualCompany"]),

    //Controls for form validation
    radiusErrors() {
      const errors = [];
      if (!this.$v.radius.$dirty) return errors;
      !this.$v.radius.required && errors.push("Campo richiesto.");
      !this.$v.radius.numeric &&
        errors.push("Il campo Raggio deve contenere un valore numerico.");
      return errors;
    },
    latitudeErrors() {
      const errors = [];
      if (!this.$v.latitude.$dirty) return errors;
      !this.$v.latitude.required && errors.push("Campo richiesto.");
      return errors;
    },
    longitudeErrors() {
      const errors = [];
      if (!this.$v.longitude.$dirty) return errors;
      !this.$v.longitude.required && errors.push("Campo richiesto.");
      return errors;
    },
    idErrors() {
      const errors = [];
      if (!this.$v.id.$dirty) return errors;
      !this.$v.id.required && errors.push("Campo richiesto.");
      !this.$v.id.unique && errors.push("Valore gia' in uso.");
      return errors;
    },
    addressErrors() {
      const errors = [];
      if (!this.$v.address.$dirty) return errors;
      !this.$v.address.required && errors.push("Campo richiesto.");
      return errors;
    },
    nameErrors() {
      const errors = [];
      if (!this.$v.name.$dirty) return errors;
      !this.$v.name.required && errors.push("Campo richiesto.");
      return errors;
    },
    streetNumberErrors() {
      const errors = [];
      if (!this.$v.streetNumber.$dirty) return errors;
      !this.$v.streetNumber.required && errors.push("Campo richiesto.");
      return errors;
    },
    zipErrors() {
      const errors = [];
      if (!this.$v.zip.$dirty) return errors;
      !this.$v.zip.required && errors.push("Campo richiesto.");
      return errors;
    },
    cityErrors() {
      const errors = [];
      if (!this.$v.city.$dirty) return errors;
      !this.$v.city.required && errors.push("Campo richiesto.");
      return errors;
    },
    provinceErrors() {
      const errors = [];
      if (!this.$v.province.$dirty) return errors;
      !this.$v.province.required && errors.push("Campo richiesto.");
      return errors;
    },
    regionErrors() {
      const errors = [];
      if (!this.$v.region.$dirty) return errors;
      !this.$v.region.required && errors.push("Campo richiesto.");
      return errors;
    },
    countryErrors() {
      const errors = [];
      if (!this.$v.country.$dirty) return errors;
      !this.$v.country.required && errors.push("Campo richiesto.");
      return errors;
    },
  },

  watch: {
    active: {
      deep: true,
      async handler(value) {
        if (value) {
          this.setModalData();
          if (this.typeCall == "add") {
            this.disabled = false;
          } else if (this.typeCall == "edit") {
            this.disabled = true;
          }
        }
      },
    },
    typeCall: function () {
      this.setModalData();
      if (this.typeCall == "add") {
        this.disabled = false;
      } else if (this.typeCall == "edit") {
        this.disabled = true;
      }
    },

    actualLocation: function () {
      this.setModalData();
    },
  },

  created() {
    this.setModalData();
  },

  mounted() {
    this.arrayDays = locationService.getArrayDays();
    var inputNumber = document.getElementById("campaignstreetNumber");

    setTimeout(() => {
      if (inputNumber?.matches(":autofill")) {
        this.changeAddress();
      }
    }, 500);
  },
};
</script>

<style scoped>
.tooltip-day {
  transform: translateY(0px);
}
.container {
  position: relative;
  top: -30px;
}
.map-style {
  height: 300px;
}
.tooltip {
  max-width: 200px;
}
</style>
