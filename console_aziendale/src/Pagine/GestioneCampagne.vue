<template>
  <div class="flex flex-col lg:flex-row">
    <div class=" lg:w-4/6 mx-2 my-2 flex flex-col bg-white" v-if="allCampaigns && allCampaigns.items &&  allCampaigns.items.length>0">
      <table class="table-auto rounded relative w-full" >
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
          <template v-for="(campaign, index) in allCampaigns.items">
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
                <dots-h-icon  class="dots-icon"/>
              </td>
            </tr>
          </template>
        </tbody>
      </table>

    </div>
    <div v-else class="text-center">
      Non ci sono campagne
    </div>
       <div class="ml-auto pt-4 pr-4">
        <button
        
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
        <add-icon class="add-icon"/>
        </button>
       </div>
    <!-- <infobox /> -->
    <profilo-campagna />
  </div>
</template>

<script>
// import Infobox from "../components/Infobox.vue";
// import { campaigns } from "../tmp-data/campaigns.js";
import { mapState,mapActions } from 'vuex';
import ProfiloCampagna from '../components/ProfiloCampagna.vue'

export default {
  components: { ProfiloCampagna },
  name: "GestioneCampagne",
  data: function() {
    return {
      // campaigns: [],
    };
  },
  computed: {
            ...mapState("company", ["actualCompany","adminCompany"]),
            ...mapState("campaign", ["allCampaigns"]),
  },
  mounted: function() {
    // this.campaigns = campaigns;
    if (this.adminCompany)
     this.getAllCampaigns(this.adminCompany.item.id);
    if (this.actualCompany)
       this.getAllCampaigns(this.actualCompany.item.id);
    if (!this.adminCompany && !this.actualCompany)
           this.getAllCampaigns(null);


  },

  methods: {
    ...mapActions("campaign",{getAllCampaigns:"getAll"}),
    getMeans: function(index) {
      let toRtn = "";

      this.allCampaigns.items[index]["means"].map((el) => {
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

<style>
.dots-icon {
    width: 40px;

}
.dots-icon svg{
  width: 100%;
  height: 100%;
}</style>
