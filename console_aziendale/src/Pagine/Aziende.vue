<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2">
      <h1>Aziende</h1>
      <table class=" shadow-lg rounded relative w-full">
        <tbody class="bg-white">
          <template v-for="company in companies">
            <tr
              class="flex border-b border-background hover:bg-background transition ease-in duration-100 "
              :key="company.id"
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
                <svg
                  class="mr-3 md:mr-1 h-12 w-6 fill-current text-grey-dark m-auto -m-r-4"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                >
                  <path
                    d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                  />
                </svg>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
    <infobox />
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
    };
  },
  mounted: function() {
    this.companies = companies;
  },
};
</script>

<style></style>
