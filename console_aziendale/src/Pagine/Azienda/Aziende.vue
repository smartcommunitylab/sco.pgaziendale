<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2 pb-16 relative">
      <div v-if="allCompanies && allCompanies.items && allCompanies.items.length > 0">
        <generic-table
          :data="allCompanies.items"
          :columns="gridColumns"
          :header="headerColumns"
          :method="showCompanyInfo"
        >
        </generic-table>
        <!-- <table class="shadow-lg rounded relative w-full">
        <thead class="text-center justify-between">
          <tr class="truncate px-2 flex border-b border-background text-center">
            <th class="w-1/6">Logo</th>

            <th class="w-4/6">Nome</th>
          </tr>
        </thead>
        <tbody class="bg-white" v-if="allCompanies">
          <template v-for="company in allCompanies.items">
            <tr
              class="select-none cursor-pointer flex border-b border-background hover:bg-background transition ease-in duration-100"
              :key="company.id"
              tag="tr"
              @dblclick="goToCompany(company.name)"
              @click="showCompanyInfo(company)"
              :id="company.id"
            >
              <td class="flex items-center align-middle w-1/6">
                <span class="m-auto" v-if="company.logo">
                  <img 
                    class="hidden mr-1 md:mr-2 md:inline-block h-8 w-8 rounded-sm object-cover"
                    :src="company.logo"
                    alt=""
                  />
                </span>
              </td>
              <td class="flex items-center text-center align-middle w-4/6">
                <span class="m-auto">
                  <p class="text-gray-800 text-sm font-semibold">
                    {{ company.name }}
                  </p>
                </span>
              </td>
              <td class="w-1/6">
                <eye-icon />
              </td>
            </tr>
          </template>
        </tbody>
      </table> -->
      </div>
      <div v-else>Non ci sono Aziende</div>

      <div class="ml-auto pt-4 pr-4 absolute right-0">
        <button
          @click="showModal('Aggiungi azienda')"
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <add-icon class="add-icon" />
        </button>
      </div>
    </div>
    <profilo-azienda v-if="actualCompany"></profilo-azienda>
    <modal v-show="editModalVisible">
      <template v-slot:header> {{ popup.title }} </template>
      <template v-slot:body>
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
      <template v-slot:footer>
        <button
          type="button"
          class="btn-close"
          @click="saveCompany"
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
    <modal v-show="deleteModalVisible">
      <template v-slot:header> Cancella Azienda </template>
      <template v-slot:body>
        Sei sicuro di voler cancellare l'azienda selezionata?
      </template>
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
  </div>
</template>

<script>
import ProfiloAzienda from "./ProfiloAzienda.vue";
import Modal from "@/components/Modal.vue";
import { mapState, mapActions } from "vuex";
import { required } from "vuelidate/lib/validators";
import EventBus from "@/components/eventBus";
import GenericTable from "@/components/GenericTable.vue";
export default {
  components: { ProfiloAzienda, Modal, GenericTable },
  name: "Aziende",
  data: function () {
    return {
      gridColumns: ["name", "code"],
      headerColumns: ["Nome", "Codice Azienda"],
      editModalVisible: false,
      deleteModalVisible: false,
      currentCompanySelected: undefined,
      popup: {
        title: "",
      },
      company: {},
      submitStatus: null,
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
  computed: {
    ...mapState("company", ["allCompanies", "actualCompany", "adminCompany"]),
  },
  mounted: function () {
    this.changePage({ title: "Lista aziende", route: "/aziende" });
    this.getAllCompanies();
    EventBus.$on("EDIT_COMPANY", (company) => {
      this.editModalVisible = true;
      this.company = company.item;
      this.copyFormValues();
      this.popup = {
        title: "Modifica",
      };
    });
    EventBus.$on("DELETE_COMPANY", (company) => {
      this.deleteModalVisible = true;
      this.company = company.item;
      this.popup = {
        title: "Cancella",
      };
    });
  },
  methods: {
    ...mapActions("company", {
      getAllCompanies: "getAll",
      addCompanyCall: "addCompany",
      updateCompanyCall: "updateCompany",
      getCompanyById: "getCompanyById",
      deleteCompany: "deleteCompany",
    }),
    ...mapActions("navigation", { changePage: "changePage" }),
    showModal(title) {
      this.editModalVisible = true;
      this.newCompany = true;
      this.initCompany();
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.editModalVisible = false;
      this.newCompany = false;
    },
    initCompany() {
      this.company={};
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
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
    copyFormValues() {
      for (const [key] of Object.entries(this.company)) {
        this[key] = this.company[key];
      }
    },

    createCompany() {
      this.company = {
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
    saveCompany() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      // console.log("submit!");
      this.$v.$touch();
      if (this.$v.$invalid) {
        this.submitStatus = "ERROR";
        return;
      } else {
        this.createCompany();
        if (this.newCompany) {
          this.addCompanyCall(this.company);
        } else {
          this.updateCompanyCall(this.company);
        }
        this.$v.$reset();
      }

      this.editModalVisible = false;
      this.newCompany = false;
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteCompany(this.company);
    },
    updateCompany() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      if (true) {
        this.updateCompanyCall(this.company);
        this.editModalVisible = false;
      }
    },
    goToCompany: function (companyName) {
      this.$router.push("/azienda/" + companyName);
    },
    showCompanyInfo: function (company) {
      if (this.currentCompanySelected == company) {
        this.getCompanyById(null);

        this.currentCompanySelected = undefined;
      } else {
        this.getCompanyById(company.id);
        this.currentCompanySelected = company;
      }
    },
  },
};
</script>

<style>
.field-label {
  display: inline-block;
  width: 40%;
}
.selected {
  @apply bg-background;
}
.form-group--error {
  /* color: red; */
  animation: shake 0.82s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}
.error {
  color: red;
  text-align: center;
}
@keyframes shake {
  10%,
  90% {
    transform: translate3d(-1px, 0, 0);
  }

  20%,
  80% {
    transform: translate3d(2px, 0, 0);
  }

  30%,
  50%,
  70% {
    transform: translate3d(-4px, 0, 0);
  }

  40%,
  60% {
    transform: translate3d(4px, 0, 0);
  }
}
</style>
