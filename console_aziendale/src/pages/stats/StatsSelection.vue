<template>
  <v-row align="center" >
    <v-col cols="3" class="pl-5 pr-20" v-if="localCompany">
      <p class="text-subtitle-1"> {{ localCompany.name }}</p>
    </v-col>
    <v-col cols="3" class="pl-5 pr-20" v-if="statsCampaigns">

      <v-autocomplete
        label="Campagna"
        name="campagna"
        id="idCampagna"
        v-model="localCampaign"
        item-text="title"
        item-value="id"
        :items="statsCampaigns"
        @change="updateCampaign"
        :return-object="true"
        outlined
      ></v-autocomplete>
    </v-col>
    <v-col cols="3" class="pl-5 pr-20" v-if="configurations && configurations.items">
      <v-select
        label="Profilo statistiche"
        name="profili"
        id="idprofili"
        v-model="selectedConfiguration"
        :items="configurations.items"
        item-text="name"
        item-value="id"
         @change="selectConfiguration"
        outlined
      ></v-select>
    </v-col>

  </v-row>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  data() {
    return {
      localCompany: null,
      localCampaign: null,
      selectedConfiguration: null,
      statsCampaigns: null
    };
  },
  computed: {
    ...mapState("company", ["allCompanies", "adminCompany"]),
    ...mapState("campaign", [
      "allCampaigns",
      "actualCampaign",
      "getAllCompaniesOfCampaignCall",
    ]),
    ...mapState("stat", ["activeSelection", "configurations", "activeConfiguration"]),
    ...mapState("account", ["user", "temporaryAdmin"]),
  },
  mounted: function () {
    if (!this.adminCompany) {
      if (this.allCampaigns && this.allCampaigns.items) {
        this.statsCampaigns = this.allCampaigns.items.sort((a, b) => new Date(a?.from).getTime() - new Date(b?.from).getTime()).reverse();
        this.localCampaign = this.allCampaigns.items[0];
        console.log('mounted',this.localCampaign);
        this.updateCampaign();
      } else {
        this.getAllCampaigns();
      }
    } else {
      this.localCompany = this.adminCompany.item;
      this.getAllCampaigns(this.adminCompany.item.id);
    }
    this.loadConfiguration();
  },
  watch: {
    allCampaigns: {
      handler: function (newValue, oldValue) {
        if (oldValue && oldValue.loading && newValue.items) {
          this.statsCampaigns = newValue.items.sort((a, b) => new Date(a?.from).getTime() - new Date(b?.from).getTime()).reverse();
          this.localCampaign = newValue.items[0];
          console.log('watched',this.localCampaign);
          this.updateCampaign();
        }
      },
      deep: true,
    },
    activeConfiguration() {
      if (this.activeConfiguration) {
        this.selectedConfiguration = this.activeConfiguration.items;
      }
    }
  },
  methods: {
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
    }),
    ...mapActions("stat", {
      setCurrentCampaign: "setCurrentCampaign",
      getConfigurationByUser:"getConfigurationByUser",
      setActiveConfiguration:"setActiveConfiguration"
    }),

    loadConfiguration(){
      this.getConfigurationByUser({user: this.user, temporaryAdmin: this.temporaryAdmin});
    },
    selectConfiguration(){
      this.setActiveConfiguration({configurationId: this.selectedConfiguration})
    },
    updateCampaign() {
      console.log('campaign', this.localCampaign);
      if (this.activeSelection && this.localCampaign) {
        // this.activeSelection.campaign = this.localCampaign;
        this.setCurrentCampaign({ campaign: this.localCampaign });
      }
    },
  },
};
</script>
