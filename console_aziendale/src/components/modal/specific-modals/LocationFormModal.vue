<template>
  <modal>
    <template v-slot:header> {{ popup.title }} </template>
    <template v-slot:body>
      <form action="" id="addLocation" @submit="saveLocation">
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
                  label="Codice sede"
                  placeholder="Codice sede *"
                  type="text"
                  name="campaignCode"
                  id="campaignCode"
                  autocomplete="null"
                  :disabled="typeCall == 'edit' ? true : false"
                  v-model.trim="$v.id.$model"
                  :error-messages="idErrors"
                  @input="$v.id.$touch()"
                  @blur="$v.id.$touch()"
                  outlined
                >
                </v-text-field>
              </v-col>
              <v-col cols="6">
                <v-text-field
                  label="Denominazione"
                  placeholder="Denominazione"
                  type="text"
                  name="denominazione"
                  id="denominazione"
                  autocomplete="null"
                  v-model.trim="$v.name.$model"
                  :error-messages="denominazioneErrors"
                  @input="$v.name.$touch()"
                  @blur="$v.name.$touch()"
                  outlined
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row class="mt-0 pt-0">
              <v-col cols="6" class="mt-0 pt-0">
                <p>
                  Il codice sede deve essere <b>UNIVOCO</b>. Verrà utilizzato per
                  associare i dipendenti ad una sede - nell'import dei dipendenti deve
                  essere <b>IDENTICO</b>
                </p>
              </v-col>
              <v-col cols="6" class="mt-0 pt-0">
                <p>
                  La denomiazione verrà visualizzata nell'app (se non specificata, verrà
                  usato il codice sede).
                </p>
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
                  label="Stato"
                  placeholder="Stato *"
                  name="campaignCountry"
                  id="campaignCountry"
                  autocomplete="null"
                  v-model.trim="$v.country.$model"
                  item-text="name"
                  item-value="name"
                  :items="listaStati"
                  :error-messages="countryErrors"
                  required
                  @input="
                    $v.country.$touch();
                    changeAddress();
                  "
                  @blur="$v.country.$touch()"
                  outlined
                ></v-autocomplete>
              </v-col>
            </v-row>
            <v-row v-if="$v.country.$model === 'Italia'">
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
          </div>
          <v-row>
            <v-col cols="12" class="mb-0">
              <v-divider></v-divider>

              <p class="text-subtitle-1 mt-5">Localizzazione</p>
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
              <v-row v-if="tmpLocationSelected">
                <v-col cols="12">
                  <p class="text-subtitle-1 mt-5 bg-warning p-2 position-warning">
                    <b>ATTENZIONE!</b> La posizione sarà utilizzata per la validazione dei
                    viaggi dei dipendenti. Saranno validi solo i viaggi che iniziano o
                    finiscono all'interno della zona delimitata dal cerchio rosso.
                  </p>
                </v-col>
              </v-row>
            </v-col>
            <v-col cols="4">
              <div class="tab-container">
                <div v-if="showInfoEdit()">
                  <p>
                    E’ possibile impostare la posizione della sede manualmente oppure
                    automaticamente in base all’indirizzo inserito.
                  </p>
                </div>

                <p v-if="!showErrorLocation && !addresIsValid">
                  Per poter impostare una posizione è necessario indicare l’indirizzo
                  della sede.
                </p>
                <p v-if="showErrorLocation" class="wrong-address">
                  <v-icon class="wrong-address-icon">mdi-alert</v-icon>
                  <span
                    >L'indirizzo inserito
                    <b>non permette il posizionamento automatico</b>. Si prega di
                    utilizzare il posizionamento manuale.</span
                  >
                </p>
                <p v-if="pointIsFar && manualEnabling" class="wrong-address">
                  <v-icon class="wrong-address-icon">mdi-alert</v-icon>
                  <span>
                    La posizione <b>non sembra corrispondere </b>all’indirizzo. Si prega
                    di verificarla e se necessario correggerla.
                  </span>
                </p>
                <v-tabs v-model="tab" align-with-title>
                  <v-tab key="1" class="text-none" @click="setTab(1)">
                    In automatico
                  </v-tab>
                  <v-tab key="2" class="text-none" @click="setTab(2)"> A mano </v-tab>
                </v-tabs>
                <v-tabs-items v-model="tab" class="mt-5">
                  <v-tab-item key="1">
                    <div>
                      <i
                        >Imposta automaticamente la posizione in base all’indirizzo
                        inserito</i
                      >
                    </div>
                    <div class="text-center">
                      <v-btn
                        color="primary"
                        :disabled="autoPositionButtonDisabled()"
                        @click="autoPosition()"
                        >Imposta automaticamente</v-btn
                      >
                    </div>
                  </v-tab-item>
                  <v-tab-item key="2">
                    <div>
                      <i>
                        Imposta manualmente la posizione trascinando il Pin o cliccando
                        sulla mappa. Prima di confermare la nuova posizione verifica che
                        corrisponda alla reale posizione della sede
                      </i>
                    </div>
                    <div v-if="!manualEnabling" class="text-center">
                      <v-btn
                        color="primary"
                        @click="manualPosition()"
                        :disabled="!addressFormIsValid()"
                        >Imposta manualmente</v-btn
                      >
                    </div>
                    <div v-if="manualEnabling" class="text-center">
                      <v-btn
                        color="error"
                        outlined
                        @click="cancelManualPosition()"
                        class="m-2"
                        >Annulla</v-btn
                      >
                      <v-btn
                        color="error"
                        @click="setManualPosition()"
                        :disabled="
                          !tmpLocationSelected?.pos?.lat && !tmpLocationSelected?.pos?.lng
                        "
                        >Conferma</v-btn
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
          <v-divider></v-divider>

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
            <v-col cols="8" class="mt-3">
              <p class="text-subtitle-1">Giorni di chiusura</p>
              <v-row v-if="!copyDate" class="mt-5 mb-5">
                <v-col cols="12">
                  <v-btn rounded @click="enableCopy()" color="primary">
                    Copia date da altra sede
                  </v-btn>
                </v-col>
              </v-row>
              <v-row v-if="copyDate">
                <v-col cols="6">
                  <v-divider></v-divider>
                  <v-select
                    label="Sede"
                    name="location"
                    v-model="location"
                    id="locationId"
                    :items="allLocations?.items"
                    :item-text="
                      (item) =>
                        `Id: ${item.id}${item.name ? ' - Nome: ' + item.name : ''}`
                    "
                    item-value="id"
                    outlined
                  ></v-select>
                </v-col>
                <v-col cols="6">
                  <div class="text-center">
                    <v-btn color="error" outlined @click="cancelDates()" class="m-2"
                      >Annulla</v-btn
                    >
                    <v-btn color="error" @click="copyDates()">Copia date</v-btn>
                  </div>
                  <!-- <v-btn rounded @click="cancelDates()" color="primary">
                      Annulla
                    </v-btn>
                    <v-btn rounded @click="copyDates()" color="primary">
                      Copia date
                    </v-btn> -->
                </v-col>
              </v-row>
              <div class="pt-5">
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
                  </template>
                  <v-date-picker v-model="nonWorkingDays" multiple scrollable no-title>
                    <v-spacer></v-spacer>
                    <v-btn text @click="menu = false"> Annulla </v-btn>
                    <v-btn text color="primary" @click="$refs.menu.save(nonWorkingDays)">
                      Salva
                    </v-btn>
                  </v-date-picker>
                </v-menu>
              </div>
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
        type="submit"
        @click="saveLocation"
        class="py-8 ml-8"
        :disabled="
          $v.$dirty && (!addresIsValid || $v.$invalid || !latitude || !longitude)
        "
      >
        Salva
      </v-btn>
      <app-confirm ref="confirm"></app-confirm>
    </template>
  </modal>
</template>

<script>
import { validationMixin } from "vuelidate";
import { required, requiredIf, numeric } from "vuelidate/lib/validators";
import { locationService } from "@/services";
import GeoLocationSelectorMapVue from "@/components/leaflet-map/GeoLocationSelectorMap.vue";
import Modal from "@/components/modal/ModalStructure.vue";
import { mapActions, mapState } from "vuex";
import Confirm from "@/components/Confirm.vue";
export default {
  components: {
    "geolocation-selector": GeoLocationSelectorMapVue,
    "app-confirm": Confirm,
    modal: Modal,
  },

  props: { typeCall: String },

  mixins: [validationMixin],

  validations: {
    id: {
      required,
      // valid: function(value) {
      //  return /^\S*$/.test(value);
      // },
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
      unique() {
        return (
          (this.actualLocation &&
            this.actualLocation.item &&
            this.name === this.actualLocation.item.name) ||
          !this.allLocations.items.find((l) => l.name === this.name)
        );
      },
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
      required: requiredIf(function (model) {
        return model.country === "Italia";
      }),
    },
    region: {
      required: requiredIf(function (model) {
        return model.country === "Italia";
      }),
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
      copyDate: false,
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
      tmpLatitude: "",
      longitude: "",
      tmpLongitude: "",
      country: "",
      radius: 200,
      nonWorkingDays: [],
      nonWorking: [],
      arrayDays: [],
      datepicker: null,
      menu: false,
      pointIsFar: false,
      autoPositionLocation: null,
      zoom: 13,
      url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
      attribution:
        '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
      currentZoom: 11.5,
      showParagraph: false,
      mapOptions: {
        zoomSnap: 0.5,
      },
      location: {},
      selectedPosition: false,
      key: 1,
      tmpLocationSelected: null,
      locationSelected: null,
      listaProvince: listaProvince,
      listaRegioni: listaRegioni,
      listaStati: listaStati,
      giorniSettimana: giorniSettimana,
      disabled: false,
      manualEnabling: false,
    };
  },

  methods: {
    ...mapActions("modal", { closeModal: "closeModal" }),
    ...mapActions("location", {
      addLocation: "addLocation",
      updateLocation: "updateLocation",
      getAllLocations: "getAllLocations",
    }),
    showInfoEdit() {
      return (
        this.addresIsValid &&
        !this.showErrorLocation &&
        !this.tmpLocationSelected &&
        !(this.typeCall == "edit")
      );
    },
    autoPositionButtonDisabled() {
      console.log("autoPositionButtonDisabled", this.autoPositionLocation);
      console.log("latitude", this.latitude);
      console.log("longitude", this.longitude);
      if (
        this.autoPositionLocation?.position &&
        this.autoPositionLocation?.position?.lat &&
        this.autoPositionLocation?.position?.lng &&
        this.autoPositionLocation?.position?.lat === this.latitude &&
        this.autoPositionLocation?.position?.lng === this.longitude
      )
        return true;
      return !this.addresIsValid;
    },
    loadLocations() {
      if (this.actualCompany) this.getAllLocations(this.actualCompany.item.id);
    },
    enableCopy() {
      this.copyDate = true;
    },
    cancelDates() {
      this.copyDate = false;
    },
    async copyDates() {
      if (
        await this.$refs.confirm.open(
          "Copia date",
          "In caso di conferma verranno sovrascritte le date. Sei sicuro?",
          { color: "primary" }
        )
      ) {
        const loc = this.allLocations.items.find(
          (location) => location.id == this.location
        );
        if (loc && loc.nonWorkingDays) this.nonWorkingDays = loc.nonWorkingDays;
        else this.nonWorkingDays = [];
      }

      this.copyDate = false;
    },
    addressFormIsValid() {
      return (
        this.$v.address.$invalid == false &&
        this.$v.streetNumber.$invalid == false &&
        this.$v.zip.$invalid == false &&
        this.$v.city.$invalid == false &&
        this.$v.province.$invalid == false &&
        this.$v.region.$invalid == false &&
        this.$v.country.$invalid == false
      );
    },
    geoSearchResult(results) {
      console.log(results);
      if (this.addressFormIsValid()) {
        if (results.length > 0) {
          this.addresIsValid = true;
          this.geoResults = results;
          this.showErrorLocation = false;
        } else {
          this.showErrorLocation = true;
          this.addresIsValid = false;
        }
      } else {
        this.addresIsValid = false;
      }
    },
    setTab(value) {
      if (value == 1) {
        this.$refs.geolocationSelector.disableMap();
        if (!this.$v.$invalid) {
          if (this.geoResults.length > 0) {
            this.addresIsValid = true;
            this.showErrorLocation = false;
          } else {
            this.showErrorLocation = true;
            this.addresIsValid = false;
          }
        }
      }
      // else {
      //   this.$refs.geolocationSelector.enableMap();
      // }
    },
    autoPosition() {
      if (this.geoResults.length > 0) {
        this.autoPositionLocation =  null;
        this.$refs.geolocationSelector.onSearch({ location: this.geoResults[0] });
      }
    },
    manualPosition() {
      console.log("manual");
      this.$refs.geolocationSelector.enableMap();
      this.addresIsValid = true;
      // this.manualPositionSet = true;
      this.manualEnabling = true;
    },
    setManualPosition() {
      //confirm location
      this.locationSelected = this.tmpLocationSelected;
      this.latitude = this.tmpLocationSelected?.pos?.lat
        ? this.tmpLocationSelected?.pos?.lat
        : this.tmpLatitude;
      this.longitude = this.tmpLocationSelected?.pos?.lng
        ? this.tmpLocationSelected?.pos?.lng
        : this.tmpLongitude;
      this.manualEnabling = false;
      this.$refs.geolocationSelector.disableMap();
    },
    cancelManualPosition() {
      //return to previous position
      this.tmpLocationSelected = null;
      this.$refs.geolocationSelector.resetPosition(this.latitude, this.longitude);
      this.$refs.geolocationSelector.disableMap();
      this.manualEnabling = false;
      this.pointIsFar = false;
    },
    locationChanged(input) {
      console.log("input", input);
      console.log("addresIsValid", this.addresIsValid);
      console.log("autoPositionLocation", this.autoPositionLocation);
      if (this.manualEnabling) {
        //using tmp
        this.tmpLocationSelected = input?.address;
        this.tmpLatitude = this.locationSelected?.pos?.lat;
        this.tmpLongitude = this.locationSelected?.pos?.lng;
        if (this.isFarFromInput(input)) {
          //show warning far
          this.pointIsFar = true;
        } else {
          this.pointIsFar = false;
        }
      } else {
        if (!this.autoPositionLocation)
          this.autoPositionLocation = input;
        this.latitude = input?.position?.lat;
        this.longitude = input?.position?.lng;
      }
    },
    isFarFromInput(input) {
      if (this.zip != input?.address?.structuredValue?.postcode) return true;
      return false;
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
            if (this.country==="Italy" ) this.country = "Italia";

      this.checkArrayInit();
      this.createLocation();
    },
    checkArrayInit() {
      if (!this.nonWorkingDays) this.nonWorkingDays = [];
      if (!this.nonWorking) this.nonWorking = [];
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
      this.country = "Italia";
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
        nonWorking: this.nonWorking ? this.nonWorking : [],
        nonWorkingDays: this.nonWorkingDays,
        country: this.country ? this.country : "Italia",
        radius: Number.parseInt(this.radius),
      };
    },
    changeAddress() {
      if (this.country == "Italia") {
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
      } else {
        this.province = "";
        this.region = "";
        this.inputAddress =
          this.address +
          " " +
          this.streetNumber +
          ", " +
          this.zip +
          ", " +
          this.city +
          ", " +
          this.country;
      }
      this.$refs.geolocationSelector.changeAddress(this.inputAddress);
    },
    fixCountry() {
      if (this.$v.country.$model==="Italy" || this.$v.country.$model==="") this.$v.country.$model = "Italia";
    },
    setModalData() {
      this.fixCountry();
      if (this.typeCall == "add") {
        this.initLocation();
        this.popup.title = "Aggiungi Sede";
      } else if (this.typeCall == "edit") {
        if (this.actualLocation.item) this.copyFormValues(this.actualLocation.item);
        this.popup.title = "Modifica Sede";
        this.addresIsValid = true;
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
    denominazioneErrors() {
      const errors = [];
      if (!this.$v.name.$dirty) return errors;
      !this.$v.name.unique && errors.push("Valore gia' in uso.");
      return errors;
    },
    addressErrors() {
      const errors = [];
      if (!this.$v.address.$dirty) return errors;
      !this.$v.address.required && errors.push("Campo richiesto.");
      return errors;
    },
    // nameErrors() {
    //   const errors = [];
    //   if (!this.$v.name.$dirty) return errors;
    //   !this.$v.name.required && errors.push("Campo richiesto.");
    //   return errors;
    // },
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
    this.loadLocations();
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
const listaProvince = [
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
];
const listaRegioni = [
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
];
const listaStati = [
  { name: "Afghanistan", code: "AF" },
  { name: "Åland Islands", code: "AX" },
  { name: "Albania", code: "AL" },
  { name: "Algeria", code: "DZ" },
  { name: "American Samoa", code: "AS" },
  { name: "Andorra", code: "AD" },
  { name: "Angola", code: "AO" },
  { name: "Anguilla", code: "AI" },
  { name: "Antarctica", code: "AQ" },
  { name: "Antigua and Barbuda", code: "AG" },
  { name: "Argentina", code: "AR" },
  { name: "Armenia", code: "AM" },
  { name: "Aruba", code: "AW" },
  { name: "Australia", code: "AU" },
  { name: "Austria", code: "AT" },
  { name: "Azerbaijan", code: "AZ" },
  { name: "Bahamas", code: "BS" },
  { name: "Bahrain", code: "BH" },
  { name: "Bangladesh", code: "BD" },
  { name: "Barbados", code: "BB" },
  { name: "Belarus", code: "BY" },
  { name: "Belgium", code: "BE" },
  { name: "Belize", code: "BZ" },
  { name: "Benin", code: "BJ" },
  { name: "Bermuda", code: "BM" },
  { name: "Bhutan", code: "BT" },
  { name: "Bolivia", code: "BO" },
  { name: "Bosnia and Herzegovina", code: "BA" },
  { name: "Botswana", code: "BW" },
  { name: "Bouvet Island", code: "BV" },
  { name: "Brazil", code: "BR" },
  { name: "British Indian Ocean Territory", code: "IO" },
  { name: "Brunei Darussalam", code: "BN" },
  { name: "Bulgaria", code: "BG" },
  { name: "Burkina Faso", code: "BF" },
  { name: "Burundi", code: "BI" },
  { name: "Cambodia", code: "KH" },
  { name: "Cameroon", code: "CM" },
  { name: "Canada", code: "CA" },
  { name: "Cape Verde", code: "CV" },
  { name: "Cayman Islands", code: "KY" },
  { name: "Central African Republic", code: "CF" },
  { name: "Chad", code: "TD" },
  { name: "Chile", code: "CL" },
  { name: "China", code: "CN" },
  { name: "Christmas Island", code: "CX" },
  { name: "Cocos (Keeling) Islands", code: "CC" },
  { name: "Colombia", code: "CO" },
  { name: "Comoros", code: "KM" },
  { name: "Congo", code: "CG" },
  { name: "Congo, The Democratic Republic of the", code: "CD" },
  { name: "Cook Islands", code: "CK" },
  { name: "Costa Rica", code: "CR" },
  { name: "Cote D'Ivoire", code: "CI" },
  { name: "Croatia", code: "HR" },
  { name: "Cuba", code: "CU" },
  { name: "Cyprus", code: "CY" },
  { name: "Czech Republic", code: "CZ" },
  { name: "Denmark", code: "DK" },
  { name: "Djibouti", code: "DJ" },
  { name: "Dominica", code: "DM" },
  { name: "Dominican Republic", code: "DO" },
  { name: "Ecuador", code: "EC" },
  { name: "Egypt", code: "EG" },
  { name: "El Salvador", code: "SV" },
  { name: "Equatorial Guinea", code: "GQ" },
  { name: "Eritrea", code: "ER" },
  { name: "Estonia", code: "EE" },
  { name: "Ethiopia", code: "ET" },
  { name: "Falkland Islands (Malvinas)", code: "FK" },
  { name: "Faroe Islands", code: "FO" },
  { name: "Fiji", code: "FJ" },
  { name: "Finland", code: "FI" },
  { name: "France", code: "FR" },
  { name: "French Guiana", code: "GF" },
  { name: "French Polynesia", code: "PF" },
  { name: "French Southern Territories", code: "TF" },
  { name: "Gabon", code: "GA" },
  { name: "Gambia", code: "GM" },
  { name: "Georgia", code: "GE" },
  { name: "Germany", code: "DE" },
  { name: "Ghana", code: "GH" },
  { name: "Gibraltar", code: "GI" },
  { name: "Greece", code: "GR" },
  { name: "Greenland", code: "GL" },
  { name: "Grenada", code: "GD" },
  { name: "Guadeloupe", code: "GP" },
  { name: "Guam", code: "GU" },
  { name: "Guatemala", code: "GT" },
  { name: "Guernsey", code: "GG" },
  { name: "Guinea", code: "GN" },
  { name: "Guinea-Bissau", code: "GW" },
  { name: "Guyana", code: "GY" },
  { name: "Haiti", code: "HT" },
  { name: "Heard Island and Mcdonald Islands", code: "HM" },
  { name: "Holy See (Vatican City State)", code: "VA" },
  { name: "Honduras", code: "HN" },
  { name: "Hong Kong", code: "HK" },
  { name: "Hungary", code: "HU" },
  { name: "Iceland", code: "IS" },
  { name: "India", code: "IN" },
  { name: "Indonesia", code: "ID" },
  { name: "Iran, Islamic Republic Of", code: "IR" },
  { name: "Iraq", code: "IQ" },
  { name: "Ireland", code: "IE" },
  { name: "Isle of Man", code: "IM" },
  { name: "Israel", code: "IL" },
  { name: "Italia", code: "IT" },
  { name: "Jamaica", code: "JM" },
  { name: "Japan", code: "JP" },
  { name: "Jersey", code: "JE" },
  { name: "Jordan", code: "JO" },
  { name: "Kazakhstan", code: "KZ" },
  { name: "Kenya", code: "KE" },
  { name: "Kiribati", code: "KI" },
  { name: "Korea, Democratic People'S Republic of", code: "KP" },
  { name: "Korea, Republic of", code: "KR" },
  { name: "Kuwait", code: "KW" },
  { name: "Kyrgyzstan", code: "KG" },
  { name: "Lao People'S Democratic Republic", code: "LA" },
  { name: "Latvia", code: "LV" },
  { name: "Lebanon", code: "LB" },
  { name: "Lesotho", code: "LS" },
  { name: "Liberia", code: "LR" },
  { name: "Libyan Arab Jamahiriya", code: "LY" },
  { name: "Liechtenstein", code: "LI" },
  { name: "Lithuania", code: "LT" },
  { name: "Luxembourg", code: "LU" },
  { name: "Macao", code: "MO" },
  { name: "Macedonia, The Former Yugoslav Republic of", code: "MK" },
  { name: "Madagascar", code: "MG" },
  { name: "Malawi", code: "MW" },
  { name: "Malaysia", code: "MY" },
  { name: "Maldives", code: "MV" },
  { name: "Mali", code: "ML" },
  { name: "Malta", code: "MT" },
  { name: "Marshall Islands", code: "MH" },
  { name: "Martinique", code: "MQ" },
  { name: "Mauritania", code: "MR" },
  { name: "Mauritius", code: "MU" },
  { name: "Mayotte", code: "YT" },
  { name: "Mexico", code: "MX" },
  { name: "Micronesia, Federated States of", code: "FM" },
  { name: "Moldova, Republic of", code: "MD" },
  { name: "Monaco", code: "MC" },
  { name: "Mongolia", code: "MN" },
  { name: "Montserrat", code: "MS" },
  { name: "Morocco", code: "MA" },
  { name: "Mozambique", code: "MZ" },
  { name: "Myanmar", code: "MM" },
  { name: "Namibia", code: "NA" },
  { name: "Nauru", code: "NR" },
  { name: "Nepal", code: "NP" },
  { name: "Netherlands", code: "NL" },
  { name: "Netherlands Antilles", code: "AN" },
  { name: "New Caledonia", code: "NC" },
  { name: "New Zealand", code: "NZ" },
  { name: "Nicaragua", code: "NI" },
  { name: "Niger", code: "NE" },
  { name: "Nigeria", code: "NG" },
  { name: "Niue", code: "NU" },
  { name: "Norfolk Island", code: "NF" },
  { name: "Northern Mariana Islands", code: "MP" },
  { name: "Norway", code: "NO" },
  { name: "Oman", code: "OM" },
  { name: "Pakistan", code: "PK" },
  { name: "Palau", code: "PW" },
  { name: "Palestinian Territory, Occupied", code: "PS" },
  { name: "Panama", code: "PA" },
  { name: "Papua New Guinea", code: "PG" },
  { name: "Paraguay", code: "PY" },
  { name: "Peru", code: "PE" },
  { name: "Philippines", code: "PH" },
  { name: "Pitcairn", code: "PN" },
  { name: "Poland", code: "PL" },
  { name: "Portugal", code: "PT" },
  { name: "Puerto Rico", code: "PR" },
  { name: "Qatar", code: "QA" },
  { name: "Reunion", code: "RE" },
  { name: "Romania", code: "RO" },
  { name: "Russian Federation", code: "RU" },
  { name: "RWANDA", code: "RW" },
  { name: "Saint Helena", code: "SH" },
  { name: "Saint Kitts and Nevis", code: "KN" },
  { name: "Saint Lucia", code: "LC" },
  { name: "Saint Pierre and Miquelon", code: "PM" },
  { name: "Saint Vincent and the Grenadines", code: "VC" },
  { name: "Samoa", code: "WS" },
  { name: "San Marino", code: "SM" },
  { name: "Sao Tome and Principe", code: "ST" },
  { name: "Saudi Arabia", code: "SA" },
  { name: "Senegal", code: "SN" },
  { name: "Serbia and Montenegro", code: "CS" },
  { name: "Seychelles", code: "SC" },
  { name: "Sierra Leone", code: "SL" },
  { name: "Singapore", code: "SG" },
  { name: "Slovakia", code: "SK" },
  { name: "Slovenia", code: "SI" },
  { name: "Solomon Islands", code: "SB" },
  { name: "Somalia", code: "SO" },
  { name: "South Africa", code: "ZA" },
  { name: "South Georgia and the South Sandwich Islands", code: "GS" },
  { name: "Spain", code: "ES" },
  { name: "Sri Lanka", code: "LK" },
  { name: "Sudan", code: "SD" },
  { name: "Suriname", code: "SR" },
  { name: "Svalbard and Jan Mayen", code: "SJ" },
  { name: "Swaziland", code: "SZ" },
  { name: "Sweden", code: "SE" },
  { name: "Switzerland", code: "CH" },
  { name: "Syrian Arab Republic", code: "SY" },
  { name: "Taiwan, Province of China", code: "TW" },
  { name: "Tajikistan", code: "TJ" },
  { name: "Tanzania, United Republic of", code: "TZ" },
  { name: "Thailand", code: "TH" },
  { name: "Timor-Leste", code: "TL" },
  { name: "Togo", code: "TG" },
  { name: "Tokelau", code: "TK" },
  { name: "Tonga", code: "TO" },
  { name: "Trinidad and Tobago", code: "TT" },
  { name: "Tunisia", code: "TN" },
  { name: "Turkey", code: "TR" },
  { name: "Turkmenistan", code: "TM" },
  { name: "Turks and Caicos Islands", code: "TC" },
  { name: "Tuvalu", code: "TV" },
  { name: "Uganda", code: "UG" },
  { name: "Ukraine", code: "UA" },
  { name: "United Arab Emirates", code: "AE" },
  { name: "United Kingdom", code: "GB" },
  { name: "United States", code: "US" },
  { name: "United States Minor Outlying Islands", code: "UM" },
  { name: "Uruguay", code: "UY" },
  { name: "Uzbekistan", code: "UZ" },
  { name: "Vanuatu", code: "VU" },
  { name: "Venezuela", code: "VE" },
  { name: "Viet Nam", code: "VN" },
  { name: "Virgin Islands, British", code: "VG" },
  { name: "Virgin Islands, U.S.", code: "VI" },
  { name: "Wallis and Futuna", code: "WF" },
  { name: "Western Sahara", code: "EH" },
  { name: "Yemen", code: "YE" },
  { name: "Zambia", code: "ZM" },
  { name: "Zimbabwe", code: "ZW" },
];
const giorniSettimana = [
  {
    1: "Lunedì",
    2: "Martedì",
    3: "Mercoledì",
    4: "Giovedì",
    5: "Venerdì",
    6: "Sabato",
    7: "Domenica",
  },
];
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
.wrong-address {
  display: flex;
}
.wrong-address-icon {
  align-self: baseline;
}
.position-warning {
  border: 1px solid black;
}
</style>
