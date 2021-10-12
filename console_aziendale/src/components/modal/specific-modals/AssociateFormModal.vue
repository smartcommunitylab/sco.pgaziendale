<template>
  <modal>
      <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
            <v-row v-if="campaigns">
            <v-col cols="4" v-for="campaign in campaigns" :key="campaign.id">
                <v-card
                v-if="campaigns && campaigns.length > 0"
                class="mx-auto"
                max-width="344"
                >
                <v-card-title>
                    {{ campaign.title }}
                </v-card-title>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn
                    color="error"
                    plain
                    small
                    v-if="isAssociated(campaign)"
                    @click="openModal({type:'dissociateForm', object:campaign})/*,disassociaPopup(campaign)*/"
                    class="py-8 ml-8"
                    >
                    Disassocia
                    </v-btn>
                    <v-btn
                    color="primary"
                    plain
                    small
                    v-else
                    @click="associa(campaign)"
                    class="py-8 ml-8"
                    >
                    Associa
                    </v-btn>
                </v-card-actions>
                </v-card>
            </v-col>
            </v-row>
        </template>
        <template v-slot:footer>
            <v-btn
            text
            @click="closeModal()"
            class="py-8 ml-8"
            >
                Chiudi
            </v-btn>
        </template>
  </modal>
</template>

<script>
import { mapState, mapActions } from "vuex";
import EventBus from "@/components/eventBus";
import { campaignService } from "@/services/campaign.services";
import Modal from "@/components/modal/ModalStructure.vue";

export default {
  components: {Modal},

  data() {
    return {
      disassociaTmpCampaign:null,
      campaigns: [],
      disassociaModalVisible:false,
      popup: {
          title: 'Associa o Disassocia Campagne',
      }
    };
  },

  methods: {
    ...mapActions("modal", {openModal: 'openModal', closeModal: 'closeModal'}),
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
      getPublicCampaigns: "getPublicCampaigns",
      getAllCompaniesOfCampaignCall: "getAllCompaniesOfCampaign",
      addCampaignCall: "addCampaign",
      updateCampaignCall: "updateCampaign",
      deleteCampaignCall: "deleteCampaign",
      deleteCompanyCampaign: "deleteCompanyCampaign",
      createCompanyCampaign: "createCompanyCampaign",
    }),
    
    associa(campaign) {
      if (this.role == "ROLE_ADMIN" && this.adminCompany && this.adminCompany.item) {
        this.createCompanyCampaign({
          companyId: this.adminCompany.item.id,
          campaign: campaign,
        });
      } else {
        this.createCompanyCampaign({
          companyId: this.actualCompany.item.id,
          campaign: campaign,
        });
      }
      this.$emit("update:allCampaigns.items", this.allCampaigns.items);
    },
    isAssociated(campaign) {
      if (this.allCampaigns && this.allCampaigns.items)
        return (
          this.allCampaigns.items.filter((elem) => elem.id == campaign.id).length > 0
        );
      else return false;
    },
  },

  computed: {
    ...mapState("account", ["role"]),
    ...mapState("company", ["actualCompany", "adminCompany"]),
    ...mapState("campaign", ["allCampaigns", "actualCampaign", "publicCampaigns"]),
  },

  
  mounted() {
      //get all public
      if (this.role == "ROLE_ADMIN" && this.adminCompany && this.adminCompany.item) {
        this.getAllCampaigns(this.adminCompany.item.id);
        campaignService.getAllCampaigns().then((campaigns) => {
          this.campaigns = campaigns;
        });
      } else {
        if (this.actualCompany && this.actualCompany.item) {
          this.getAllCampaigns(this.actualCompany.item.id);
          campaignService.getPublicCampaigns().then((campaigns) => {
            this.campaigns = campaigns;
          });
        }
      }
  },

  beforeDestroy() {
    EventBus.$off("ASSOCIATE_CAMPAIGN_FORM");
  },

};
</script>

<style scoped>
.v-card__title {
    font-size: 1.00rem;
    font-weight: 500;
    letter-spacing: .0125em;
    line-height: 2rem;
    word-break: break-all;
    padding-bottom: 0px;
}
</style>
