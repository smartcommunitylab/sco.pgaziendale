<template>
  <div>
    Campagne pubbliche
    <div v-if="publicCampaigns && publicCampaigns.items">
      <div v-for="campaign in publicCampaigns.items" :key="campaign.id">
        <div v-if="publicCampaigns && publicCampaigns.items.length > 0">
          {{ campaign.title }}
          <button v-if="isAssociated(campaign)" @click="disassocia(campaign)">Disassocia</button>
          <button v-else @click="associa(campaign)">Associa</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState, mapActions } from "vuex";
import EventBus from "@/components/eventBus";

export default {
  components: {},
  data() {
    return {};
  },
  computed: {
    ...mapState("company", ["actualCompany", "adminCompany"]),
    ...mapState("campaign", ["allCampaigns", "actualCampaign", "publicCampaigns"]),
  },

  methods: {
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
      getPublicCampaigns: "getPublicCampaigns",
      getCampaignCall: "getCampaign",
      addCampaignCall: "addCampaign",
      updateCampaignCall: "updateCampaign",
      deleteCampaignCall: "deleteCampaign",
      deleteCompanyCampaign:"deleteCompanyCampaign",
      createCompanyCampaign:"createCompanyCampaign"
    }),
    disassocia(campaign) {
        this.deleteCompanyCampaign({companyId:this.adminCompany.item.id,campaign:campaign});
        this.$emit('update:allCampaigns.items', this.allCampaigns.items)
    },
    associa(campaign) {
        this.createCompanyCampaign({companyId:this.adminCompany.item.id,campaign:campaign});
        this.$emit('update:allCampaigns.items', this.allCampaigns.items)

    },
    isAssociated(campaign) {
      if (this.allCampaigns && this.allCampaigns.items)
        return (
          this.allCampaigns.items.filter((elem) => elem.id == campaign.id).length > 0
        );
      else return false;
    },
  },
  mounted() {
    EventBus.$on("ASSOCIATE_CAMPAIGN_FORM", () => {
      //get all public
      this.getAllCampaigns(this.adminCompany.item.id);
      this.getPublicCampaigns();
      //show the set withouth the already subscribed
    });
  },

  beforeDestroy() {
    EventBus.$off("ASSOCIATE_CAMPAIGN_FORM");
  },
};
</script>
<style scoped></style>
