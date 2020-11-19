<template>
  <div>Card</div>
</template>

<script>
export default {
  name: "CampaignCard",
  props: {
    title: String,
    description: String,
    active: Boolean,
    startDate: String,
    endDate: String,
    means: [],
  },

  data: function() {
    return {
      dTitolo: this.title,
      dDescription: this.description,
      dActive: this.active,
      dStartDate: this.startDate,
      dEndDate: this.endDate,
      dMeans: this.means,
    };
  },
};
</script>

<style></style>
