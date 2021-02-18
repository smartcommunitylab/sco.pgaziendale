<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2 pb-16 relative">
      <table class="shadow-lg rounded relative w-full">
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
      </table>
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
          <div class="mb-4 flex flex-wrap justify-between">
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.company.name.$error }"
              >
                <label class="field-label" for="first_name">Nome </label>
                <input
                  type="text"
                  name="companyName"
                  placeholder="Nome *"
                  v-model.trim="$v.company.name.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="companyName"
                />
              </div>
              <div v-if="$v.company.name.$error">
                <div class="error" v-if="!$v.company.name.required">
                  Il campo nome e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.company.code.$error }"
              >
                <label class="field-label" for="first_name">Codice Azienda</label>
                <input
                  type="text"
                  name="companyCode"
                  id="companyCode"
                  placeholder="Codice *"
                  v-model.trim="$v.company.code.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.company.code.$error">
                <div class="error" v-if="!$v.company.code.required">
                  Il campo codice e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.company.address.$error }"
              >
                <label class="field-label" for="last_name">Indirizzo </label>
                <input
                  type="text"
                  name="companyAddress"
                  id=""
                  required
                  placeholder="Address *"
                  v-model.trim="$v.company.address.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.company.contactPhone.$error }"
              >
                <label class="field-label" for="email">Telefono</label>
                <input
                  type="text"
                  name="companyPhone"
                  id=""
                  required
                  placeholder="Telefono *"
                  v-model.trim="$v.company.contactPhone.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.company.contactPhone.$error">
                <div class="error" v-if="!$v.company.contactPhone.required">
                  Il campo telefono e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.company.contactEmail.$error }"
              >
                <label class="field-label" for="password">Email </label>
                <input
                  type="text"
                  name="companyEmail"
                  id=""
                  required
                  placeholder="Email *"
                  v-model.trim="$v.company.contactEmail.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.company.contactEmail.$error">
                <div class="error" v-if="!$v.company.contactEmail.required">
                  Il campo email e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.company.web.$error }"
              >
                <label class="field-label" for="password">Web </label>
                <input
                  type="text"
                  name="companyWeb"
                  id=""
                  required
                  placeholder="Web *"
                  v-model.trim="$v.company.web.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.company.web.$error">
                <div class="error" v-if="!$v.company.web.required">
                  Il campo web e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.company.logo.$error }"
              >
                <label class="field-label" for="first_name">Logo Azienda</label>
                <input
                  type="text"
                  name="companyLogo"
                  id="companyLogo"
                  placeholder="Logo *"
                  v-model.trim="$v.company.logo.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.company.logo.$error">
                <div class="error" v-if="!$v.company.logo.required">
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
import ProfiloAzienda from "../components/ProfiloAzienda.vue";
// import { companies } from "../tmp-data/companies";
import Modal from "../components/Modal.vue";
import { mapState, mapActions } from "vuex";
import { required } from "vuelidate/lib/validators";
import EventBus from "../components/eventBus"
export default {
  components: { ProfiloAzienda, Modal },
  name: "Aziende",

  data: function () {
    return {
      // companies: [],
      editModalVisible: false,
      deleteModalVisible:false,
      currentCompanySelected: undefined,
      popup: {
        title: "",
      },
      company: {},
      submitStatus: null,
    };
  },
  validations: {
    company: {
      name: {
        required,
      },
      code: {
        required,
      },
      address: {
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
  },
  computed: {
    ...mapState("company", ["allCompanies","actualCompany"]),
  },
  mounted: function () {
    this.getAllCompanies();
    EventBus.$on("EDIT_COMPANY", company => {
      this.editModalVisible = true;
      this.company = company.item;
      this.popup = {
        title: "Modifica",
      };
    });
    EventBus.$on("DELETE_COMPANY", company => {
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
      getCompanyById:"getCompanyById",
      deleteCompany:"deleteCompany"
    }),
    showModal(title) {
      this.editModalVisible = true;
      this.newCompany =true;
      this.popup = {
        title: title
      };
    },
    closeModal() {
      this.editModalVisible = false;
      this.newCompany =false;
    },
        closeDeleteModal() {
      this.deleteModalVisible = false;
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
        if (this.newCompany) {
        this.addCompanyCall(this.company);
        }
        else {
            this.updateCompanyCall(this.company);
            }
      }

        this.editModalVisible = false;
        this.newCompany =false;
        }
      ,
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
        this.getCompanyById(null)

        this.currentCompanySelected = undefined;
      } else {
             this.getCompanyById(company.id)
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
