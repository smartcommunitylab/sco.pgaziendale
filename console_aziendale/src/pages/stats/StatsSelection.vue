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
      statsCampaigns: null,
      isLoadingPreferences: false,
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
      this.statsCampaigns = this.allCampaigns.items
        .sort((a, b) => new Date(a?.from).getTime() - new Date(b?.from).getTime())
        .reverse();

      // Carica le preferenze salvate
      this.loadSavedPreferences();
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
    localCampaign(newVal) {
    if (this.isLoadingPreferences) {
      console.log("Skipping save: loading preferences"); // LOG
      return; // Evita di salvare durante il caricamento
    }
    if (newVal) {
      console.log("Campaign changed:", newVal); // LOG
      this.savePartialPrefs();
    }
  },
  selectedConfiguration(newVal) {
    if (this.isLoadingPreferences) {
      console.log("Skipping save: loading preferences"); // LOG
      return; // Evita di salvare durante il caricamento
    }
    if (newVal) {
      console.log("Configuration changed:", newVal); // LOG
      this.savePartialPrefs();
    }
  },
    allCampaigns: {
      handler: function (newValue, oldValue) {
        if (oldValue && oldValue.loading && newValue.items) {
          this.statsCampaigns = newValue.items.sort((a, b) => new Date(a?.from).getTime() - new Date(b?.from).getTime()).reverse();
          
          // INIT SALVATO
          const saved = this.getSavedPrefs();
          if (saved && saved.campaignId) {
             const found = this.statsCampaigns.find(c => c.id === saved.campaignId);
             this.localCampaign = found || this.statsCampaigns[0];
          } else {
             this.localCampaign = this.statsCampaigns[0];
          }
          this.updateCampaign();
        }
      },
      deep: true,
    },
    configurations(val) {
      if (val && val.items) {
        const saved = this.getSavedPrefs();
        if (saved && saved.configurationId) {
           const found = val.items.find(c => c.id === saved.configurationId);
           if (found) {
               this.selectedConfiguration = found.id;
               this.selectConfiguration();
           }
        }
      }
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
    getConfigurationByUser: "getConfigurationByUser",
    setActiveConfiguration: "setActiveConfiguration",
  }),
  getSavedPrefs() {
    try {
      return JSON.parse(localStorage.getItem("pg_stats_prefs"));
    } catch (e) {
      return null;
    }
  },
  savePartialPrefs() {
  try {
    let prefs = this.getSavedPrefs() || {};
    prefs.campaignId = this.localCampaign?.id;
    if (this.selectedConfiguration) {
      prefs.configurationId = this.selectedConfiguration;
    } else {
      console.warn("Skipping save: selectedConfiguration is null"); // LOG
      return; // Evita di salvare se `selectedConfiguration` è `null`
    }
    localStorage.setItem("pg_stats_prefs", JSON.stringify(prefs));
    console.log("Preferences saved:", prefs); // LOG
  } catch (e) {
    console.error("Error saving preferences", e);
  }
},
  loadSavedPreferences() {
  this.isLoadingPreferences = true; // Inizio caricamento
  const saved = this.getSavedPrefs();
  if (saved) {
    // Carica la campagna salvata
    if (saved.campaignId) {
      const foundCampaign = this.statsCampaigns?.find(
        (c) => c.id === saved.campaignId
      );
      this.localCampaign = foundCampaign || this.statsCampaigns?.[0];
    } else {
      this.localCampaign = this.statsCampaigns?.[0];
    }

    // Carica la configurazione salvata
    if (saved.configurationId) {
      const foundConfig = this.configurations?.items?.find(
        (c) => c.id === saved.configurationId
      );
      if (foundConfig) {
        this.selectedConfiguration = foundConfig.id;
      } else {
        console.warn("Saved configuration not found, defaulting to null");
        this.selectedConfiguration = null;
      }
    }
  } else {
    // Nessuna preferenza salvata, usa i valori predefiniti
    this.localCampaign = this.statsCampaigns?.[0];
    this.selectedConfiguration = null;
  }
  this.isLoadingPreferences = false; // Fine caricamento
},
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
