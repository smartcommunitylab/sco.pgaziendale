<template>
   <form action="" id="addAzienda">
          <div class="mb-20 flex flex-wrap justify-between">
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.name.$error }">
                <label class="field-label" for="first_name">Nome </label>
                <input
                  type="text"
                  name="companyName"
                  placeholder="Nome *"
                  v-model.trim="$v.name.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="companyName"
                />
              </div>
              <div v-if="$v.name.$error">
                <div class="error" v-if="!$v.name.required">
                  Il campo nome e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.code.$error }">
                <label class="field-label" for="first_name">Codice Azienda</label>
                <input
                  type="text"
                  name="companyCode"
                  id="companyCode"
                  placeholder="Codice *"
                  v-model.trim="$v.code.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.code.$error">
                <div class="error" v-if="!$v.code.required">
                  Il campo codice e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.address.$error }">
                <label class="field-label" for="last_name">Indirizzo </label>
                <input
                  type="text"
                  name="companyAddress"
                  id=""
                  required
                  placeholder="Indirizzo *"
                  v-model.trim="$v.address.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.streetNumber.$error }"
              >
                <label class="field-label" for="last_name">Numero </label>
                <input
                  type="text"
                  name="companyNumber"
                  id=""
                  required
                  placeholder="Numero *"
                  v-model.trim="$v.streetNumber.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.city.$error }">
                <label class="field-label" for="last_name">Cittá </label>
                <input
                  type="text"
                  name="companyCity"
                  id=""
                  required
                  placeholder="Cittá *"
                  v-model.trim="$v.city.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.province.$error }"
              >
                <label class="field-label" for="last_name">Provincia </label>
                <input
                  type="text"
                  name="companyProvince"
                  id=""
                  required
                  placeholder="Provincia *"
                  v-model.trim="$v.province.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.region.$error }">
                <label class="field-label" for="last_name">Regione </label>
                <input
                  type="text"
                  name="companyRegion"
                  id=""
                  required
                  placeholder="Regione *"
                  v-model.trim="$v.region.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.country.$error }">
                <label class="field-label" for="last_name">Stato </label>
                <input
                  type="text"
                  name="companyCountry"
                  id=""
                  required
                  placeholder="Stato *"
                  v-model.trim="$v.country.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.zip.$error }">
                <label class="field-label" for="last_name">CAP </label>
                <input
                  type="text"
                  name="companyCap"
                  id=""
                  required
                  placeholder="CAP *"
                  v-model.trim="$v.zip.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.contactPhone.$error }"
              >
                <label class="field-label" for="email">Telefono</label>
                <input
                  type="text"
                  name="companyPhone"
                  id=""
                  required
                  placeholder="Telefono *"
                  v-model.trim="$v.contactPhone.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.contactPhone.$error">
                <div class="error" v-if="!$v.contactPhone.required">
                  Il campo telefono e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.contactEmail.$error }"
              >
                <label class="field-label" for="password">Email </label>
                <input
                  type="text"
                  name="companyEmail"
                  id=""
                  required
                  placeholder="Email *"
                  v-model.trim="$v.contactEmail.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.contactEmail.$error">
                <div class="error" v-if="!$v.contactEmail.required">
                  Il campo email e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.web.$error }">
                <label class="field-label" for="password">Web </label>
                <input
                  type="text"
                  name="companyWeb"
                  id=""
                  required
                  placeholder="Web *"
                  v-model.trim="$v.web.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.web.$error">
                <div class="error" v-if="!$v.web.required">
                  Il campo web e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.logo.$error }">
                <label class="field-label" for="first_name">Logo Azienda</label>
                <input
                  type="text"
                  name="companyLogo"
                  id="companyLogo"
                  placeholder="Logo *"
                  v-model.trim="$v.logo.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.logo.$error">
                <div class="error" v-if="!$v.logo.required">
                  Il campo logo e' richiesto.
                </div>
              </div>
            </div>
          </div>
        </form>
</template>
<script>
import { required } from "vuelidate/lib/validators";
import EventBus from "@/components/eventBus";

export default {
  data() {
    return {
 company: {},
      id:"",
      name: "",
      code: "",
      address: "",
      streetNumber: "",
      city: "",
      province: "",
      region: "",
      country: "",
      zip: "",
      contactEmail: "",
      contactPhone: "",
      web: "",
      logo: "",
    };
  },
    validations: {
    name: {
      required,
    },
    code: {
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
    },
    contactPhone: {
      required,
    },
    web: {
      required,
    },
    logo: {
      required,
    },
  },
  methods: {

  copyFormValues(company) {
      for (const [key] of Object.entries(company)) {
        this[key] = company[key];
      }
    },
    initCompany() {
      this.company={};
      this.id="";
        this.name= "";
      this.code= "";
      this.address= "";
      this.streetNumber= "";
      this.city= "";
      this.province= "";
      this.region= "";
      this.country= "";
      this.zip= "";
      this.contactEmail= "";
      this.contactPhone= "";
      this.web= "";
      this.logo= "";
      
    },
    createCompany() {
      this.company = {
        id:this.id,
        name: this.name,
        code: this.code,
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
  },
  mounted() {
    EventBus.$on("EDIT_COMPANY_FORM", company => {
        this.copyFormValues(company);
    });
     EventBus.$on("NEW_COMPANY_FORM", () => {
         this.initCompany()
    });
    EventBus.$on("CHECK_COMPANY_FORM", () => {
      this.$v.$touch();
      if (this.$v.$invalid) {
        //generate event no
        EventBus.$emit("NO_COMPANY_FORM");
      } else {
        //   generate event ok
        this.createCompany();
        EventBus.$emit("OK_COMPANY_FORM",this.company);
        this.$v.$reset();
      }
    });
  },

 beforeDestroy () {
    EventBus.$off('CHECK_COMPANY_FORM');
    EventBus.$off('NEW_COMPANY_FORM');
    EventBus.$off('EDIT_COMPANY_FORM');
 },
};
</script>
<style scoped></style>
