<template>
  <modal>
      <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
            <v-row v-if="campaigns">
            <v-col cols="4" v-for="campaign in campaigns" :key="campaign.id">
                <v-card
                v-if="campaigns && campaigns.length > 0"              
                >

                <v-card-title
                  class="textSize"
                >
                    {{ campaign.title }}
                </v-card-title>

                <v-spacer></v-spacer>

                <v-card-actions>
                    <v-btn
                    color="error"
                    plain
                    small
                    v-if="isAssociated(campaign)"
                    @click="openModal({type:'dissociateForm', object:campaign})/*,disassociaPopup(campaign)*/"
                    >
                    Disassocia
                    </v-btn>
                    <v-btn
                    color="primary"
                    plain
                    small
                    v-else
                    @click="associa(campaign)"
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
import { campaignService } from "@/services/campaign.services";
import Modal from "@/components/modal/ModalStructure.vue";

export default {
  components: {Modal},

  data() {
    return {
      campaigns: [],
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
      createCompanyCampaign: "createCompanyCampaign",
    }),
    
    associa(campaign) {
      this.createCompanyCampaign({
        companyId: this.actualCompany.item.id,
        campaign: campaign,
      });
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
    ...mapState("account", ["user"]),
    ...mapState("company", ["actualCompany", "adminCompany"]),
    ...mapState("campaign", ["allCampaigns"]),
  },

  mounted() {
      //get all public
      if (this.adminCompany && this.adminCompany.item) {
        this.getAllCampaigns(this.adminCompany.item.id);
        campaignService.getAllCampaigns().then((campaigns) => {
          this.campaigns = campaigns.filter(c => c.territoryId === this.actualCompany.item.territoryId);
        });
      } else {
        if (this.actualCompany && this.actualCompany.item) {
          this.getAllCampaigns(this.actualCompany.item.id);
          campaignService.getPublicCampaigns().then((campaigns) => {
            this.campaigns = campaigns.filter(c => c.territoryId === this.actualCompany.item.territoryId);
          });
        }
      }
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
.textSize{
  font-size: 98%;
}
</style>
