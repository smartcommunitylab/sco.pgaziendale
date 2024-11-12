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
                label="Provincia"
                :placeholder="user.permissions.admin ? 'Provincia' : 'Provincia *'"
                name="companyProvince"
                id="companyProvince"
                v-model.trim="$v.province.$model"
                :error-messages="provinceErrors"
                @input="$v.province.$touch()"
                @blur="$v.province.$touch()"
                :items="listaProvince"
                outlined
              ></v-autocomplete>
            </v-col>
            <v-col cols="6">
              <v-autocomplete
                label="Regione"
                :placeholder="user.permissions.admin ? 'Regione' : 'Regione *'"
                name="companyRegion"
                id="companyRegion"
                v-model.trim="$v.region.$model"
                :error-messages="regionErrors"
                @input="$v.region.$touch()"
                @blur="$v.region.$touch()"
                :items="listaRegioni"
                outlined
              ></v-autocomplete>
            </v-col>
            <v-col cols="6">
              <v-text-field
                label="Stato"
                :placeholder="user.permissions.admin ? 'Stato' : 'Stato *'"
                type="text"
                name="companyCountry"
                id="companyCountry"
                v-model.trim="$v.country.$model"
                :error-messages="countryErrors"
                @input="$v.country.$touch()"
                @blur="$v.country.$touch()"
                outlined
              ></v-text-field>
            </v-col>
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
        </div>
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
import { required, email } from "vuelidate/lib/validators";
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
          required,
        },
        region: {
          required,
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
