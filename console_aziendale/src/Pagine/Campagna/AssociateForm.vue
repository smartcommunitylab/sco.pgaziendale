<template>
  <div>
    <div v-if="campaigns ">
      <div v-for="campaign in campaigns" :key="campaign.id">
        <div class="flex items-center justify-center" v-if="campaigns && campaigns.length > 0">
          <div class="w-1/2">{{ campaign.title }}</div>
          <button  type="button"
                class="w-1/2 btn-close"
                aria-label="Close modal" v-if="isAssociated(campaign)" @click="disassocia(campaign)">Disassocia</button>
          <button  type="button"
                class="w-1/2 btn-close"
                aria-label="Close modal" v-else @click="associa(campaign)">Associa</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState, mapActions } from "vuex";
import EventBus from "@/components/eventBus";
import {campaignService} from "../../services/campaign.services"

export default {
  components: {},
  data() {
    return {
      campaigns:[]
    };
  },
  computed: {
    ...mapState("company", ["actualCompany", "adminCompany"]),
    ...mapState("campaign", ["allCampaigns", "actualCampaign", "publicCampaigns"]),
  },

  methods: {
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
      getPublicCampaigns: "getPublicCampaigns",
      getAllCompaniesOfCampaignCall: "getAllCompaniesOfCampaign",
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
       campaignService.getAllCampaigns().then(campaigns=>{
         this.campaigns=campaigns;
       })
      // this.getAllCampaigns(this.adminCompany.item.id);
      // this.getPublicCampaigns();
      //show the set withouth the already subscribed
    });
  },

  beforeDestroy() {
    EventBus.$off("ASSOCIATE_CAMPAIGN_FORM");
  },
};
</script>
<style scoped></style>
