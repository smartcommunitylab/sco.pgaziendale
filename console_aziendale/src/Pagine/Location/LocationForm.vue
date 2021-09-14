<template>
  <form action="" id="addLocation">
    <div class="mb-20">
      <v-row>
        <v-col
          cols="8"
        >
          <div class="map-style">
            <geolocation-selector
              v-model="locationSelected"
              :key="key"
              :radius="radius"
              v-on:poschanged="locationChanged"
            />
          </div>
        </v-col>
        <v-col
          cols="4"
          align-self="center"
          class="mt-6"
          
        >
          <v-row
            class="p-0 mx-1"
            justify="center"
          >
            <v-text-field
              label="Raggio"
              placeholder="Raggio *"
              type="text"
              name="campaignRadius"
              :rules="raggioRules"
              id="campaignRadius"
              v-model.trim="$v.radius.$model"
              outlined
            >
              <template v-slot:append>
                <v-tooltip
                  bottom
                  nudge-bottom="10"
                  nudge-left="75"
                >
                  <template v-slot:activator="{ on }">
                    <v-icon v-on="on">
                      mdi-help-circle-outline
                    </v-icon>
                  </template>
                  Distanza in metri all'interno di cui i viaggi dei dipendenti risultano essere validi
                </v-tooltip>
              </template>
            </v-text-field>
          </v-row>           
          <v-row
            class="p-0 mx-1 my-7"
            justify="center"
          >
            <v-text-field
              label="Longitudine"
              placeholder="Longitudine *"
              type="text"
              name="campaignLongitude"
              :rules="[rules.required]"
              id="campaignLongitude"
              v-model.trim="$v.longitude.$model"
              outlined
            ></v-text-field>
          </v-row>
          <v-row
            class="p-0 mx-1"
            justify="center"
          >
            <v-text-field
              label="Latitudine"
              placeholder="Latitudine *"
              type="text"
              name="campaignLatitude"
              :rules="[rules.required]"
              id="campaignLatitude"
              v-model.trim="$v.latitude.$model"
              outlined
            ></v-text-field>
          </v-row>
        </v-col>
      </v-row>

      <v-row>
        <v-col
          cols="4"
        >
          <v-text-field
            label="Identificativo"
            placeholder="Identificativo *"
            type="text"
            name="campaignCode"
            :rules="[rules.required]"
            id="campaignCode"
            v-model.trim="$v.id.$model"
            outlined
            
            :disabled="edit"
          >
            <template v-slot:append>
              <v-tooltip
                left
                nudge-bottom="50px"
                v-if="$v.id.$model == ''"
              >
                <template v-slot:activator="{ on }">
                  <v-icon v-on="on">
                    mdi-help-circle-outline
                  </v-icon>
                </template>
                Codice univoco della sede
              </v-tooltip>
            </template>
          </v-text-field>
        </v-col>
        <v-col
          cols="4"
        >
          <v-text-field
            label="Indirizzo"
            placeholder="Indirizzo *"
            type="text"
            name="campaignAddress"
            :rules="[rules.required]"
            id="campaignAddress"
            v-model.trim="$v.address.$model"
            outlined            
          ></v-text-field>
        </v-col>
        <v-col
          cols="4"
        >
          <v-text-field
            label="Numero"
            placeholder="Numero *"
            type="text"
            name="campaignstreetNumber"
            :rules="[rules.required]"
            id="campaignstreetNumber"
            v-model.trim="$v.streetNumber.$model"
            outlined            
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row>
        <v-col
          cols="4"
        >
          <v-text-field
            label="CAP"
            placeholder="CAP *"
            type="text"
            name="campaignZip"
            :rules="[rules.required]"
            id="campaignZip"
            v-model.trim="$v.zip.$model"
            outlined            
          ></v-text-field>
        </v-col>
        <v-col
          cols="4"
        >
          <v-text-field
            label="Cittá"
            placeholder="Cittá *"
            type="text"
            name="campaignCity"
            :rules="[rules.required]"
            id="campaignCity"
            v-model.trim="$v.city.$model"
            outlined            
          ></v-text-field>
        </v-col>
        <v-col
          cols="4"
        >
          <v-autocomplete
            label="Provincia"
            placeholder="Provincia *"
            name="campaignProvince"
            :rules="provinceRules"
            id="campaignProvince"
            v-model.trim="$v.province.$model"
            :items="listaProvince"
            outlined
          ></v-autocomplete>
        </v-col>
      </v-row>
      <v-row>
        <v-col
        > 
          <v-autocomplete
            label="Regione"
            placeholder="Regione *"
            name="campaignRegion"
            :rules="[rules.required]"
            id="campaignRegion"
            v-model.trim="$v.region.$model"
            :items="listaRegioni"
            outlined
          ></v-autocomplete>
        </v-col>
        <v-col
        >
          <v-text-field
            label="Stato"
            placeholder="Stato *"
            type="text"
            name="campaignCountry"
            :rules="[rules.required]"
            id="campaignCountry"
            v-model.trim="$v.country.$model"
            outlined
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row>
        <v-col 
          cols="4"
          class="mt-3"
        >
          <v-form>
            <p class="text-subtitle-1">Giorni NON lavorativi</p>
            <div v-for="day in arrayDays" v-bind:key="day.value">
              <v-checkbox
                v-model="$v.nonWorking.$model"
                :label= day.text
                :value="day.value"
                hide-details
              ></v-checkbox>
            </div>
          </v-form>
        </v-col>
        <v-spacer></v-spacer>
        <v-col
          cols="8"
        >
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
                readonly
                v-bind="attrs"
                v-on="on"
              ></v-combobox>
            </template>
            <v-date-picker
              v-model="nonWorkingDays"
              multiple
              scrollable
              no-title
            >
              <v-spacer></v-spacer>
              <v-btn
                text
                @click="menu = false"
              >
                Annulla
              </v-btn>
              <v-btn
                text
                color="primary"
                @click="$refs.menu.save(nonWorkingDays)"
              >
                Salva
              </v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>
      </v-row>  
    </div>
  </form>
</template>

<script>

import { required, numeric } from "vuelidate/lib/validators";
import { locationService } from "../../services";
import EventBus from "@/components/eventBus";
import GeoLocationSelectorMapVue from "@/components/GeoLocationSelectorMap.vue";

export default {
  components: {
    "geolocation-selector": GeoLocationSelectorMapVue,
  },
  data() {
    return {
      id: "",
      address: "",
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
      edit: false,
      listaProvince: ['AG', 'AL', 'AN', 'AO', 'AR', 'AP', 'AT', 'AV', 'BA', 'BT',
      'BL', 'BN', 'BG', 'BI', 'BO', 'BZ', 'BS', 'BR', 'CA', 'CL',
      'CB', 'CE', 'CT', 'CZ', 'CH', 'CO', 'CS', 'CR', 'KR', 'CN',
      'EN', 'FM', 'FE', 'FI', 'FG', 'FC', 'FR', 'GE', 'GO', 'GR',
      'IM', 'IS', 'AQ', 'SP', 'LT', 'LE', 'LC', 'LI', 'LO', 'LU', 'MC',
      'MN', 'MS', 'MT', 'ME', 'MI', 'MO', 'MB', 'NA', 'NO', 'NU',
      'OR', 'PD', 'PA', 'PR', 'PV', 'PG', 'PU', 'PE', 'PC', 'PI', 'PT',
      'PN', 'PZ', 'PO', 'RG', 'RA', 'RC', 'RE', 'RI', 'RN', 'RM', 'RO',
      'SA', 'SS', 'SV', 'SI', 'SR', 'SO', 'SU', 'TA', 'TE', 'TR', 'TO', 'TP',
      'TN', 'TV', 'TS', 'UD', 'VA', 'VE', 'VB', 'VC', 'VR', 'VV', 'VI', 'VT'
      ],
      listaRegioni: ['Abruzzo', 'Basilicata', 'Calabria', 'Campania', 'Emilia-Romagna', 
      'Friuli-Venezia Giulia', 'Lazio', 'Liguria', 'Lombardia', 'Marche', 'Molise', 'Piemonte', 
      'Puglia', 'Sardegna', 'Sicilia', 'Toscana', 'Trentino-Alto Adige', 'Umbria', 'Valle d\'Aosta Veneto' 
      ],
      giorniSettimana: [{1:'Lunedì', 2:'Martedì', 3:'Mercoledì', 4:'Giovedì', 5:'Venerdì', 6:'Sabato', 7:'Domenica'}
      ],
      rules: {
        required: value => !!value || 'Campo richiesto.',
      },
      raggioRules: [
          v => !!v || 'Campo richiesto.', 
          v => !isNaN(v) || 'Il campo Raggio deve contenere un valore numerico.'
      ],
    };
  },
  computed: {},
  methods: {
    locationChanged(input) {
      console.log(input);
      this.locationSelected = input.address;
      this.latitude = this.locationSelected.pos.lat;
      this.longitude = this.locationSelected.pos.lng;
      if (this.locationSelected && this.locationSelected.structuredValue)
        this.changeParamForm(this.locationSelected.structuredValue);
    },
    changeParamForm(structuredValue) {
      if (structuredValue.road) this.address = structuredValue.road;
      if (structuredValue.house_number) this.streetNumber = structuredValue.house_number;
      if (structuredValue.city) this.city = structuredValue.city;
      if (structuredValue.country) this.country = structuredValue.country;
      if (structuredValue.postcode) this.zip = structuredValue.postcode;
      if (structuredValue.state) this.region = structuredValue.state;
      if (structuredValue.county) this.region = structuredValue.county;
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
    },

    createLocation() {
      this.locationSelected = {
        id: this.id,
        address: this.address,
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
  },
  mounted() {
    this.arrayDays = locationService.getArrayDays();
    EventBus.$on("EDIT_LOCATION_FORM", (location) => {
      this.edit = true;
      this.copyFormValues(location);
    });
    EventBus.$on("NEW_LOCATION_FORM", () => {
      this.edit = false;
      this.initLocation();
    });
    EventBus.$on("CHECK_LOCATION_FORM", () => {
      this.$v.$touch();
      if (this.$v.$invalid) {
        //generate event no
        EventBus.$emit("NO_LOCATION_FORM");
      } else {
        //   generate event ok
        this.createLocation();
        EventBus.$emit("OK_LOCATION_FORM", this.locationSelected);
        this.$v.$reset();
      }
    });
  },

  beforeDestroy() {
    EventBus.$off("CHECK_LOCATION_FORM");
    EventBus.$off("NEW_LOCATION_FORM");
    EventBus.$off("EDIT_LOCATION_FORM");
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
      numeric,
    },
    latitude: {
      required,
    },
    longitude: {
      required,
    },
    newNonWorkingDay: {},
    nonWorkingDays: {
    },
    nonWorking: {},
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
</style>
