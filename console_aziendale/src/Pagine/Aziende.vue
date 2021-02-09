<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2">
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
      <div class="ml-auto pt-4 pr-4">
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
      <template v-slot:header> {{popup.title}} </template>
      <template v-slot:body>
        <!-- <div class="flex items-center h-screen w-full bg-teal-lighter"> -->
      <!-- <div class="w-full bg-white rounded shadow-lg p-8 m-4 md:max-w-sm md:mx-auto"> -->
        <div class="mb-4 md:flex md:flex-wrap md:justify-between" >
          <div class="field-group mb-4 md:w-1/2">
            <label class="field-label" for="first_name">Nome </label>
            <input type="text"
                            name="companyName"
                            id=""
                            required
                            placeholder="Nome *"
                            v-model="company.name"
                            class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                         >
          </div>
          <div class="field-group mb-4 md:w-1/2">
            <label class="field-label md:ml-2" for="last_name">Indirizzo </label>
            <input type="text"
                            name="companyAddress"
                            id=""
                            required
                            placeholder="Address *"
                                                        v-model="company.address"

                            class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                          >
          </div>
          <div class="field-group mb-4 md:w-full">
            <label class="field-label" for="email">Telefono</label>
            <input type="text"
                            name="companyPhone"
                            id=""
                            required
                            placeholder="Telefono *"
                            v-model="company.contactPhone"
                            class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                        >
          </div>
          <div class="field-group mb-6 md:w-full">
            <label class="field-label" for="password">Email </label>
            <input type="text"
                            name="companyEmail"
                            id=""
                            required
                            placeholder="Email *"
                            v-model="company.contactEmail"
                            class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                        >
          </div>
                    <div class="field-group mb-6 md:w-full">
            <label class="field-label" for="password">Web </label>
            <input type="text"
                            name="companyWeb"
                            id=""
                            required
                            placeholder="Web *"
                            v-model="company.web"
                            class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
                        >
          </div>
        </div>
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
      </template>
    </modal>
  </div>
</template>

<script>
import Infobox from "../components/Infobox.vue";
// import { companies } from "../tmp-data/companies";
import Modal from "../components/Modal.vue";
import { mapState, mapActions } from "vuex";

export default {
  components: { Infobox, Modal },
  name: "Aziende",

  data: function () {
    return {
      // companies: [],
      isModalVisible: false,
      currentCompanySelected: undefined,
      popup:{
        title:""
      },
      company:{}
    };
  },
  computed: {
    ...mapState("company", ["allCompanies"]),
  },
  mounted: function () {
    this.getAllCompanies();
  },
  methods: {
    ...mapActions("company", { getAllCompanies: "getAll",addCompanyCall:"addCompany",updateCompanyCall:"updateCompany" }),
    showModal(title) {
      this.isModalVisible = true;
       this.popup= {
        title:title
      }
    },
    closeModal() {
      this.isModalVisible = false;
    },
    addCompany() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      if (true){
      this.addCompanyCall(this.company)
      this.isModalVisible = false;
    }
    },
    updateCompany() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      if (true){
      this.updateCompanyCall(this.company)
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
.selected {
  @apply bg-background;
}
</style>
