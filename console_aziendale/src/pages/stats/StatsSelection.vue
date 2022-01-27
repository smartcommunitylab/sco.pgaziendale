<template>
  <v-row justify="center" align="center" >
    <v-col cols="4" class="pl-5 pr-20" v-if="allCampaigns && allCampaigns.items">

      <v-autocomplete
        label="Campagna"
        name="campagna"
        id="idCampagna"
        v-model="localCampaign"
        item-text="title"
        item-value="id"
        :items="allCampaigns.items"
        @change="updateCampaign"
        outlined
      ></v-autocomplete>
    </v-col>

    <v-col cols="4" class="pl-5 pr-20" v-if="!isAdmin && localCompany">
      <p class="text-subtitle-1"> {{ localCompany.name }}</p>
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
    };
  },
  computed: {
    ...mapState("company", ["allCompanies", "actualCompany", "adminCompany"]),
    ...mapState("campaign", [
      "allCampaigns",
      "actualCampaign",
      "getAllCompaniesOfCampaignCall",
    ]),
    ...mapState("stat", ["activeSelection"]),
    ...mapState("account", ["role", "temporaryAdmin"]),
    isAdmin() {
      return this.role == "ROLE_ADMIN" && !this.temporaryAdmin;
    },
  },
  mounted: function () {
    if (this.isAdmin) {
      if (this.allCampaigns && this.allCampaigns.items) {
        this.localCampaign = this.allCampaigns.items[0];
        this.updateCampaign();
      } else {
        this.getAllCampaigns();
      }
    } else {
      this.localCompany = this.actualCompany.item;
      this.getAllCampaigns(this.actualCompany.item.id);
    }
  },
  watch: {
    allCampaigns: {
      handler: function (newValue, oldValue) {
        if (oldValue && oldValue.loading && newValue.items) {
          this.localCampaign = newValue.items[0];
          this.updateCampaign();
        }
      },
      deep: true,
    },
  },
  methods: {
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
    }),
    ...mapActions("stat", {
      setCurrentCampaign: "setCurrentCampaign",
    }),
    updateCampaign(value) {
      console.log(value);
      if (this.activeSelection && this.localCampaign) {
        // this.activeSelection.campaign = this.localCampaign;
        this.setCurrentCampaign({ campaign: this.localCampaign });
      }
    },
  },
};
</script>
