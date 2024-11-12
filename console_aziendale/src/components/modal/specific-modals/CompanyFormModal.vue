<template>
  <modal>
    <template v-slot:header> {{ popup.title }} </template>
    <template v-slot:body>
      <form action="" id="addAzienda">
        <div class="mb-20 flex flex-wrap justify-between">
          <v-row class="pt-7 rounded-lg bg-background">
            <v-col cols="6">
              <v-text-field
                label="Nome"
                placeholder="Nome *"
                type="text"
                name="companyName"
                id="companyName"
                v-model.trim="$v.name.$model"
                :error-messages="nameErrors"
                @input="$v.name.$touch()"
                @blur="$v.name.$touch()"
                :disabled="!user.canDo('manage', 'companies') || !user.permissions.admin"
                outlined
              ></v-text-field>
            </v-col>
            <v-col cols="3">
              <v-text-field
                label="Codice Azienda"
                placeholder="Codice *"
                type="text"
                name="companyCode"
                id="companyCode"
                v-model.trim="$v.code.$model"
                :error-messages="codeErrors"
                :disabled="!user.canDo('manage', 'companies') || !user.permissions.admin"
                @input="$v.code.$touch()"
                @blur="$v.code.$touch()"
                outlined
              >
                <template v-slot:append>
                  <v-tooltip left nudge-bottom="50px">
                    <template v-slot:activator="{ on }">
                      <v-icon v-on="on"> mdi-help-circle-outline </v-icon>
                    </template>
                    Codice univoco dell'azienda con cui verrá poi identificata nelle
                    campagne
                  </v-tooltip>
                </template>
              </v-text-field>
            </v-col>
            <v-col cols="3">
              <v-select
                label="Territorio"
                name="territoryId"
                id="territoryId"
                v-model.trim="$v.territoryId.$model"
                :items="territoryIds"
                item-text="name.it"
                item-value="territoryId"
                :error-messages="territoryErrors"
                :disabled="!user.canDo('manage', 'companies') || !user.permissions.admin"
                outlined
              ></v-select>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="6">
              <v-text-field
                label="Indirizzo"
                :placeholder="user.permissions.admin ? 'Indirizzo' : 'Indirizzo *'"
                type="text"
                name="companyAddress"
                id="companyAddress"
                v-model.trim="$v.address.$model"
                :error-messages="addressErrors"
                @input="$v.address.$touch()"
                @blur="$v.address.$touch()"
                outlined
              ></v-text-field>
            </v-col>
            <v-col cols="2">
              <v-text-field
                label="Numero"
                :placeholder="user.permissions.admin ? 'Numero' : 'Numero *'"
                type="text"
                name="companyNumber"
                id="companyNumber"
                v-model.trim="$v.streetNumber.$model"
                :error-messages="numberErrors"
                @input="$v.streetNumber.$touch()"
                @blur="$v.streetNumber.$touch()"
                outlined
              ></v-text-field>
            </v-col>
            <v-col cols="4">
              <v-text-field
                label="CAP"
                :placeholder="user.permissions.admin ? 'CAP' : 'CAP *'"
                type="text"
                name="companyCap"
                id="companyCap"
                v-model.trim="$v.zip.$model"
                :error-messages="capErrors"
                @input="$v.zip.$touch()"
                @blur="$v.zip.$touch()"
                outlined
              ></v-text-field>
            </v-col>
            <v-col cols="6">
              <v-text-field
                label="Città"
                :placeholder="user.permissions.admin ? 'Città' : 'Città *'"
                type="text"
                name="companyCity"
                id="companyCity"
                v-model.trim="$v.city.$model"
                :error-messages="cityErrors"
                @input="$v.city.$touch()"
                @blur="$v.city.$touch()"
                outlined
              ></v-text-field>
            </v-col>

            <v-col cols="4">
              <v-autocomplete
                label="Stato"
                :placeholder="user.permissions.admin ? 'Stato' : 'Stato *'"
                name="companyCountry"
                id="companyCountry"
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
          <v-row v-if="$v.country.$model === 'Italy'">
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
         
          <v-col cols="6">
            <v-text-field
              label="Telefono"
              :placeholder="user.permissions.admin ? 'Telefono' : 'Telefono *'"
              type="text"
              name="companyPhone"
              id="companyPhone"
              v-model.trim="$v.contactPhone.$model"
              :error-messages="phoneErrors"
              @input="$v.contactPhone.$touch()"
              @blur="$v.contactPhone.$touch()"
              outlined
            ></v-text-field>
          </v-col>
          <v-col cols="6">
            <v-text-field
              label="E-mail"
              :placeholder="user.permissions.admin ? 'E-mail ' : 'E-mail  *'"
              type="text"
              name="companyEmail"
              id="companyEmail"
              v-model.trim="$v.contactEmail.$model"
              :error-messages="emailErrors"
              @input="$v.contactEmail.$touch()"
              @blur="$v.contactEmail.$touch()"
              outlined
            ></v-text-field>
          </v-col>
          <v-col cols="6">
            <v-text-field
              label="Web"
              :placeholder="user.permissions.admin ? 'Web' : 'Web *'"
              type="text"
              name="companyWeb"
              id="companyWeb"
              v-model.trim="$v.web.$model"
              :error-messages="webErrors"
              @input="$v.web.$touch()"
              @blur="$v.web.$touch()"
              outlined
            ></v-text-field>
          </v-col>
          <v-col cols="6">
            <v-text-field
              label="Logo"
              placeholder="Logo"
              type="text"
              name="companyLogo"
              id="companyLogo"
              v-model.trim="$v.logo.$model"
              outlined
            >
              <template v-slot:append>
                <v-tooltip left nudge-bottom="50px">
                  <template v-slot:activator="{ on }">
                    <v-icon v-on="on"> mdi-help-circle-outline </v-icon>
                  </template>
                  Inserisci un url che contenga il logo dell'azienda
                </v-tooltip>
              </template>
            </v-text-field>
          </v-col>
        </v-row>
        <!-- </div> -->
      </form>
    </template>
    <template v-slot:footer>
      <v-btn text @click="closeThisModal" class="py-8 ml-8"> Annulla </v-btn>
      <v-btn color="primary" text @click="saveCompany" class="py-8 ml-8"> Salva </v-btn>
    </template>
  </modal>
</template>

<script>
import { mapActions, mapState } from "vuex";
import { validationMixin } from "vuelidate";
import { required, email,requiredIf } from "vuelidate/lib/validators";
import Modal from "@/components/modal/ModalStructure.vue";

export default {
  props: { typeCall: String },

  mixins: [validationMixin],

  components: { modal: Modal },

  validations() {
    if (!this.user.permissions.admin) {
      return {
        name: {
          required,
        },
        code: {
          required,
        },
        territoryId: {
          required,
        },
        address: {
          required,
        },
        streetNumber: {
          required,
        },
        city: {
          required,
        },
        province: {
      required: requiredIf(function (model) {
        return model.country === "Italy";
      }),
    },
    region: {
      required: requiredIf(function (model) {
        return model.country === "Italy";
      }),
    },
        country: {
          required,
        },
        zip: {
          required,
        },
        contactEmail: {
          required,
          email,
        },
        contactPhone: {
          required,
        },
        web: {
          required,
        },
        logo: {},
      };
    } else
      return {
        name: {
          required,
        },
        code: {
          required,
        },
        territoryId: {
          required,
        },
        address: {},
        streetNumber: {},
        city: {},
        province: {},
        region: {},
        country: {},
        zip: {},
        contactEmail: {
          email,
        },
        contactPhone: {},
        web: {},
        logo: {},
      };
  },

  data() {
    return {
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
       listaStati: [
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
  { name: "Italy", code: "IT" },
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
],
      company: {},
      id: null,
      name: "",
      code: "",
      territoryId: "",
      territoryIds: [],
      address: "",
      streetNumber: "",
      city: "",
      province: 1,
      region: "",
      country: "",
      zip: "",
      contactEmail: "",
      contactPhone: "",
      web: "",
      logo: "",
      popup: {
        title: "",
      },
    };
  },

  methods: {
    ...mapActions("modal", { closeModal: "closeModal" }),
    ...mapActions("company", {
      addCompany: "addCompany",
      updateCompany: "updateCompany",
    }),
    ...mapActions("campaign", { getTerritories: "getTerritories" }),

    copyFormValues(company) {
      if (!company) return;
      for (const [key] of Object.entries(company)) {
        this[key] = company[key];
      }
    },

    isURL(str) {
      let url;
      try {
        url = new URL(str);
      } catch (_) {
        return false;
      }
      return url.protocol === "http:" || url.protocol === "https:";
    },
    validateEmail(email) {
      var re = /\S+@\S+\.\S+/;
      return re.test(email);
    },
    initCompany() {
      this.company = {};
      this.id = null;
      this.name = "";
      this.code = "";
      this.territoryId = "";
      this.address = "";
      this.streetNumber = "";
      this.city = "";
      this.province = "";
      this.region = "";
      this.country = "";
      this.zip = "";
      this.contactEmail = "";
      this.contactPhone = "";
      this.web = "";
      this.logo = "";
    },
    createCompany() {
      this.company = {
        id: this.id,
        name: this.name,
        code: this.code,
        territoryId: this.territoryId,
        address: this.address,
        streetNumber: this.streetNumber,
        city: this.city,
        province: this.province,
        region: this.region,
        country: this.country,
        zip: this.zip,
        contactEmail: this.contactEmail,
        contactPhone: this.contactPhone,
        web: this.web,
        logo: this.logo,
      };
    },
    changeAddress() {
      if (this.country == "Italy") {
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
    },
    closeThisModal() {
      console.log(this.type);
      this.initCompany();
      this.$v.$reset();
      this.closeModal();
    },
    saveCompany() {
      if (!this.$v.$invalid) {
        this.createCompany();
        if (this.typeCall == "add") {
          this.addCompany(this.company);
          this.$v.$reset();
          this.closeModal();
        } else if (this.typeCall == "edit") {
          console.log(this.company);
          this.updateCompany(this.company);
          this.$v.$reset();
          this.closeModal();
        }
      } else {
        this.$v.$touch();
      }
    },
    setModalData() {
      if (this.typeCall == "add") {
        this.initCompany();
        this.popup.title = "Aggiungi Azienda";
        console.log("Modalità AGGIUNGI");
      } else if (
        this.typeCall == "edit" &&
        this.actualCompany &&
        this.actualCompany.item
      ) {
        this.copyFormValues(this.actualCompany.item);
        this.popup.title = "Modifica Azienda";
        console.log("Modalità MODIFICA");
      }
    },
  },

  computed: {
    ...mapState("company", ["actualCompany"]),
    ...mapState("campaign", ["territories"]),
    ...mapState("modal", ["type"]),
    ...mapState("account", ["user"]),

    //Controls for form validation
    nameErrors() {
      const errors = [];
      if (!this.$v.name.$dirty) return errors;
      !this.$v.name.required && errors.push("Campo richiesto.");
      return errors;
    },
    codeErrors() {
      const errors = [];
      if (!this.$v.code.$dirty) return errors;
      !this.$v.code.required && errors.push("Campo richiesto.");
      return errors;
    },
    addressErrors() {
      const errors = [];
      if (!this.$v.address.$dirty) return errors;
      !this.user.permissions.admin && !this.$v.address.required && errors.push("Campo richiesto.");
      return errors;
    },
    numberErrors() {
      const errors = [];
      if (!this.$v.streetNumber.$dirty) return errors;
      !this.user.permissions.admin && !this.$v.streetNumber.required && errors.push("Campo richiesto.");
      return errors;
    },
    capErrors() {
      const errors = [];
      if (!this.$v.zip.$dirty) return errors;
      !this.user.permissions.admin && !this.$v.zip.required && errors.push("Campo richiesto.");
      return errors;
    },
    cityErrors() {
      const errors = [];
      if (!this.$v.city.$dirty) return errors;
      !this.user.permissions.admin && !this.$v.city.required && errors.push("Campo richiesto.");
      return errors;
    },
    provinceErrors() {
      const errors = [];
      if (!this.$v.province.$dirty) return errors;
      !this.user.permissions.admin && !this.$v.province.required && errors.push("Campo richiesto.");
      return errors;
    },
    regionErrors() {
      const errors = [];
      if (!this.$v.region.$dirty) return errors;
      !this.user.permissions.admin && !this.$v.region.required && errors.push("Campo richiesto.");
      return errors;
    },
    countryErrors() {
      const errors = [];
      if (!this.$v.country.$dirty) return errors;
      !this.user.permissions.admin && !this.$v.country.required && errors.push("Campo richiesto.");
      return errors;
    },
    phoneErrors() {
      const errors = [];
      if (!this.$v.contactPhone.$dirty) return errors;
      !this.user.permissions.admin &&  !this.$v.contactPhone.required && errors.push("Campo richiesto.");
      return errors;
    },
    emailErrors() {
      const errors = [];
      if (!this.$v.contactEmail.$dirty) return errors;
      !this.$v.contactEmail.email && errors.push("E-mail non valida.");
      !this.user.permissions.admin && !this.$v.contactEmail.required && errors.push("E-mail richiesta.");
      return errors;
    },
    webErrors() {
      const errors = [];
      if (!this.$v.web.$dirty) return errors;
      !this.user.permissions.admin &&!this.$v.web.required && errors.push("Url richiesto.");
      !this.isURL(this.web) &&
        errors.push('Inserisci un url con "http://" o "https://".');
      return errors;
    },
    territoryErrors() {
      const errors = [];
      if (!this.$v.territoryId.$dirty) return errors;
       !this.$v.territoryId.required && errors.push("Campo richiesto.");
      return errors;
    },
  },

  watch: {
    type: function () {
      this.setModalData();
    },
    typeCall: function () {
      this.setModalData();
    },
    territories(res) {
      if (res.items) {
        this.territoryIds = res.items.filter((t) => t.territoryId);
      }
    },
  },

  mounted() {
    this.setModalData();
    this.getTerritories();
  },
};
</script>

<style></style>
