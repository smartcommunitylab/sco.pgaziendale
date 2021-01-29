<template>
  <div class="flex flex-col lg:flex-row">
    <div class=" lg:w-4/6 mx-2 my-2 flex flex-col bg-white">
      <table class="table-auto rounded relative w-full">
        <thead class="text-center justify-between">
          <tr
            class="truncate px-2  flex border-b border-background text-center"
          >
            <th class="w-1/6">
              Nome
            </th>

            <th class=" w-1/6">
              Inizio
            </th>
            <th class="w-1/6">
              Fine
            </th>
            <th class="w-1/6">
              Regola
            </th>
            <th class="w-1/6">Status</th>
            <th class="w-1/6"></th>
          </tr>
        </thead>
        <tbody class="bg-white text-center justify-between">
          <template v-for="(campaign, index) in campaigns">
            <tr
              class=" text-center m-auto truncate px-2 select-none cursor-pointer flex items-center border-b border-background hover:bg-background transition ease-in duration-100 "
              :key="campaign.id"
              tag="tr"
              :class="campaign.active ? '' : 'bg-background'"
            >
              <td class=" w-1/6">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ campaign.title }}
                </p>
              </td>
              <td class=" w-1/6">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ formatDate(campaign.startDate) }}
                </p>
              </td>
              <td class=" w-1/6">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ formatDate(campaign.endDate) }}
                </p>
              </td>
              <td class=" w-1/6">
                <p
                  class="text-gray-800 text-sm font-semibold text-center truncate"
                >
                  {{ getMeans(index) }}
                </p>
              </td>
              <td class=" w-1/6">
                <p
                  class="text-gray-800 text-sm font-semibold text-center truncate"
                >
                  {{ getStatus(campaign.active) }}
                </p>
              </td>
              <td class="flex  items-end w-1/6 pr-12">
                <svg
                  class="mr-3 md:mr-1 h-12 w-6 fill-current text-grey-dark m-auto "
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
      <div class="ml-auto pt-4 pr-4">
        <button
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <svg
            viewBox="0 0 20 20"
            enable-background="new 0 0 20 20"
            class="w-6 h-6 inline-block"
          >
            <path
              fill="#FFFFFF"
              d="M16,10c0,0.553-0.048,1-0.601,1H11v4.399C11,15.951,10.553,16,10,16c-0.553,0-1-0.049-1-0.601V11H4.601
                                    C4.049,11,4,10.553,4,10c0-0.553,0.049-1,0.601-1H9V4.601C9,4.048,9.447,4,10,4c0.553,0,1,0.048,1,0.601V9h4.399
                                    C15.952,9,16,9.447,16,10z"
            />
          </svg>
        </button>
      </div>
    </div>
    <infobox />
  </div>
</template>

<script>
import Infobox from "../components/Infobox.vue";
import { campaigns } from "../tmp-data/campaigns.js";
export default {
  components: { Infobox },
  name: "GestioneCampagne",
  data: function() {
    return {
      campaigns: [],
    };
  },

  mounted: function() {
    this.campaigns = campaigns;
  },

  methods: {
    getMeans: function(index) {
      let toRtn = "";

      this.campaigns[index]["means"].map((el) => {
        toRtn += el + " - ";
      });
      return toRtn.slice(0, -3);
    },
    getStatus: function(status) {
      let toRtn = "";
      if (status) {
        toRtn = "Attiva";
      } else {
        toRtn = "Non attiva";
      }

      return toRtn;
    },

    formatDate: function(date) {
      const moment = require("moment");

      moment.locale("it");

      return moment(date).format("DD MMMM YYYY");
    },
  },
};
</script>

<style></style>
