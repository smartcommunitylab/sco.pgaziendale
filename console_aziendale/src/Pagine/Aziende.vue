<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2 pb-16">
      <table class="shadow-lg rounded relative w-full">
        <thead class="text-center justify-between">
          <tr class="truncate px-2 flex border-b border-background text-center">
            <th class="w-1/6">Logo</th>

            <th class="w-4/6">Nome</th>
          </tr>
        </thead>
        <tbody class="bg-white">
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
                <span class="m-auto">
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
                <dots-h-icon />
              </td>
            </tr>
          </template>
        </tbody>
      </table>
      <div class="ml-auto pt-4 pr-4 absolute right-0 lg:mr-96">
        <button
          @click="showModal('Aggiungi azienda')"
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <add-icon class="add-icon" />
        </button>
      </div>
    </div>
    <infobox ref="infobox" />
    <modal v-show="isModalVisible">
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
                  class="focus:border-blue-600 border-2 p-2 mb-2  flex-1 mr-2"
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
                  class="focus:border-blue-600 border-2 p-2 mb-2  flex-1 mr-2"
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
                  class="focus:border-blue-600 border-2 p-2 mb-2  flex-1 mr-2"
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
                  class="focus:border-blue-600 border-2 p-2 mb-2  flex-1 mr-2"
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

                  class="focus:border-blue-600 border-2 p-2 mb-2  flex-1 mr-2"
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
                  class="focus:border-blue-600 border-2 p-2 mb-2  flex-1 mr-2"
                />
              </div>
                                                        <div v-if="$v.company.web.$error">
                <div class="error" v-if="!$v.company.web.required">
                  Il campo web e' richiesto.
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
          @click="addCompany"
          aria-label="Close modal"
        >
          Aggiungi
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
import Infobox from "../components/Infobox.vue";
// import { companies } from "../tmp-data/companies";
import Modal from "../components/Modal.vue";
import { mapState, mapActions } from "vuex";
import { required } from "vuelidate/lib/validators";

export default {
  components: { Infobox, Modal },
  name: "Aziende",

  data: function () {
    return {
      // companies: [],
      isModalVisible: false,
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
    },
  },
  computed: {
    ...mapState("company", ["allCompanies"]),
  },
  mounted: function () {
    this.getAllCompanies();
  },
  methods: {
    ...mapActions("company", {
      getAllCompanies: "getAll",
      addCompanyCall: "addCompany",
      updateCompanyCall: "updateCompany",
    }),
    showModal(title) {
      this.isModalVisible = true;
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.isModalVisible = false;
    },
    addCompany() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      console.log("submit!");
      this.$v.$touch();
      if (this.$v.$invalid) {
        this.submitStatus = "ERROR";
      } else {
        this.addCompanyCall(this.company);
        this.isModalVisible = false;
      }
    },
    updateCompany() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      if (true) {
        this.updateCompanyCall(this.company);
        this.isModalVisible = false;
      }
    },
    goToCompany: function (companyName) {
      this.$router.push("/azienda/" + companyName);
    },
    showCompanyInfo: function (company) {
      if (this.currentCompanySelected == company) {
        window.document
          .getElementById(this.currentCompanySelected.id)
          .classList.toggle("selected");
        this.currentCompanySelected = undefined;
      } else {
        if (this.currentCompanySelected != undefined) {
          window.document
            .getElementById(this.currentCompanySelected.id)
            .classList.toggle("selected");
        }
        this.currentCompanySelected = company;
        window.document
          .getElementById(this.currentCompanySelected.id)
          .classList.toggle("selected");
      }

      this.$refs["infobox"].showCompanyDetails(company);
    },
  },
};
</script>

<style>
.field-label{
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
