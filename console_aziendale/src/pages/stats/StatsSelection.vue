<template>
  <v-row justify="center">
    <v-col cols="4" class="pl-5 pr-20" v-if="allCampaigns && allCampaigns.items">
      <p class="text-subtitle-1">Seleziona Campagna</p>

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

    <v-col cols="4" class="pl-5 pr-20" v-if="allCampaigns && allCompanies.items">
      <p class="text-subtitle-1">Seleziona Azienda</p>
      <v-autocomplete
        label="Azienda"
        placeholder="azienda"
        name="azienda"
        id="idAzienda"
        item-text="name"
        item-value="id"
        v-model="localCompany"
        :items="allCompanies.items"
        @change="updateCompany"
        outlined
      ></v-autocomplete>
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
    ...mapState("campaign", ["allCampaigns", "actualCampaign"]),
  },
  mounted: function () {
        if (this.allCompanies && this.allCompanies.items) {
        this.localCompany = this.allCompanies.items[0];
      } else {
    this.getAllCompanies();

      }

      if (this.allCampaigns && this.allCampaigns.items) {
        this.localCampaign = this.allCampaigns.items[0];
      } else {
            this.getAllCampaigns();

      }
  },
    watch: {
      allCompanies: {
        handler: function (newValue, oldValue ) {
          if (oldValue.loading && newValue.items) {
            this.localCompany = newValue.items[0];
          }
        },
        deep: true,
      },
      allCampaigns: {
        handler: function (newValue, oldValue ) {
          if (oldValue.loading && newValue.items) {
            this.localCampaign = newValue.items[0];
          }
        },
        deep: true,
      },
    },
  methods: {
    ...mapActions("company", {
      getAllCompanies: "getAll",
    }),
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
    }),
    updateCompany() {
      console.log(this.localCompany)
    },
    updateCampaign() {
            console.log(this.localCampaign)
    },
    
  },
};
</script>
