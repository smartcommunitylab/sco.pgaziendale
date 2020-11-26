<template>
  <div class="bg-blue-600">
    <div>
      <h1
        class="justify-self-center text-center text-white text-4xl pt-2 md:text-6xl font-semibold pb-6"
      >
        Campagne Attive
      </h1>
      <hr class="mx-12" />

      <div
        class=" flex  flex-col sm:flex-row sm:flex-wrap sm:justify-center md:justify-start md:pl-12"
      >
        <template v-for="campaign in fakeCampaigns">
          <campaign-card
            class=""
            :key="campaign.id"
            :id="campaign.id"
            :title="campaign.title"
            :description="campaign.description"
            :startDate="campaign.startDate"
            :endDate="campaign.endDate"
            :active="campaign.active"
            :means="campaign.means"
          />
        </template>
      </div>
    </div>
    <div>
      <h1
        class="justify-self-center text-center text-white text-4xl pt-2 md:text-6xl font-semibold pb-6"
      >
        Campagne Concluse
      </h1>
      <hr class="mx-12" />
      <div
        class=" flex  flex-col sm:flex-row sm:flex-wrap sm:justify-center md:justify-start md:pl-12"
      >
        <template v-for="campaign in fakeCampaigns">
          <campaign-card
            class=""
            :key="campaign.id"
            :title="campaign.title"
            :description="campaign.description"
            :startDate="campaign.startDate"
            :endDate="campaign.endDate"
            :active="campaign.active"
            :means="campaign.means"
          />
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import CampaignCard from "../Components/CampaignCard.vue";
export default {
  name: "Campagne",
    components: { CampaignCard },

  data: function() {
    return {
      fakeCampaigns: [],
    };
  },
  mounted: function() {
    let x = require("../tmp-data/campaigns").campaigns;
    this.fakeCampaigns = x;

    console.log(x);  }
};
</script>

<style>
</style>
