<template>
  <div
    class=" bg-blue-600 flex  flex-col sm:flex-row sm:flex-wrap justify-center"
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

    console.log(x);
  },
};
</script>

<style></style>
