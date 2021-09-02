<template>
  <div>
    <div v-if="campaigns">
      <div v-for="campaign in campaigns" :key="campaign.id">
        <div
          class="flex items-center justify-left"
          v-if="campaigns && campaigns.length > 0"
        >
          <div class="Subtitle 1">{{ campaign.title }}</div>
          <v-btn
            color="error"
            plain
            small
            v-if="isAssociated(campaign)"
            @click="disassociaPopup(campaign)"
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
        </div>
      </div>
    </div>
    <modal v-show="disassociaModalVisible">
      <template v-slot:header> <div class="text-danger"> Disassocia dalla campagna </div>  </template>
      <template v-slot:body>
        <p class="text-subtitle-1">Sei sicuro di voler procedere con la cancellazione dalla campagna <b>{{disassociaTmpCampaign.title}}</b>?</p>
      </template>
      <template v-slot:footer>
        <v-btn
          text
          @click="closeDeleteModal"
          class="py-8 ml-8"
        >
          Annulla
        </v-btn>
        <v-btn
          color="error"
          text
          @click="deleteConfirm"
          class="py-8 ml-8"
        >
          Conferma
        </v-btn>
      </template>
    </modal>
  </div>
</template>
<script>
import { mapState, mapActions } from "vuex";
import EventBus from "@/components/eventBus";
import { campaignService } from "../../services/campaign.services";
import Modal from "@/components/Modal.vue";

export default {
  components: {Modal},
  data() {
    return {
      disassociaTmpCampaign:null,
      campaigns: [],
      disassociaModalVisible:false
    };
  },
  computed: {
    ...mapState("account", ["role"]),
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
      deleteCompanyCampaign: "deleteCompanyCampaign",
      createCompanyCampaign: "createCompanyCampaign",
    }),
    disassocia(campaign) {
      if (this.role == "ROLE_ADMIN" && this.adminCompany && this.adminCompany.item) {
        this.deleteCompanyCampaign({
          companyId: this.adminCompany.item.id,
          campaign: campaign,
        });
      } else {
        this.deleteCompanyCampaign({
          companyId: this.actualCompany.item.id,
          campaign: campaign,
        });
      }
      this.$emit("update:allCampaigns.items", this.allCampaigns.items);
    },
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
      disassociaPopup(campaign) {
    this.disassociaModalVisible = true;
    this.disassociaTmpCampaign=campaign;
  },
  closeDeleteModal() {
      this.disassociaModalVisible = false;
      this.disassociaTmpCampaign=null;
  },
  deleteConfirm() {
      this.disassociaModalVisible = false;
      this.disassocia(this.disassociaTmpCampaign);
      this.disassociaTmpCampaign=null;
    },
  },
  mounted() {
    EventBus.$on("ASSOCIATE_CAMPAIGN_FORM", () => {
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

      //show the set withouth the already subscribed
    });
  },

  beforeDestroy() {
    EventBus.$off("ASSOCIATE_CAMPAIGN_FORM");
  },

};
</script>
<style scoped></style>