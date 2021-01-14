<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2">
      <h1>Aziende</h1>
      <table class=" shadow-lg rounded relative w-full">
        <tbody class="bg-white">
          <template v-for="company in companies">
            <tr
              class="select-none cursor-pointer flex border-b border-background hover:bg-background transition ease-in duration-100 "
              :key="company.id"
              tag="tr"
              @dblclick="goToCompany(company.name)"
              @click="showCompanyInfo(company)"
              :id="company.id"
            >
              <td class="flex  items-center align-middle w-5/6">
                <span>
                  <img
                    class="hidden mr-1 md:mr-2 md:inline-block h-8 w-8 rounded-sm object-cover"
                    src=""
                    alt=""
                  />
                </span>
                <span class=" ">
                  <p class="text-gray-800 text-sm font-semibold">
                    {{ company.name }}
                  </p>
                </span>
              </td>

              <td class="w-1/6">
                <pencil-outline-icon />
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
    <infobox ref="infobox" />
  </div>
</template>

<script>
import Infobox from "../components/Infobox.vue";
import { companies } from "../tmp-data/companies";

export default {
  components: { Infobox },
  name: "Aziende",

  data: function() {
    return {
      companies: [],
      currentCompanySelected: undefined,
    };
  },
  mounted: function() {
    this.companies = companies;
  },
  methods: {
    goToCompany: function(companyName) {
      this.$router.push("/azienda/" + companyName);
    },
    showCompanyInfo: function(company) {
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
