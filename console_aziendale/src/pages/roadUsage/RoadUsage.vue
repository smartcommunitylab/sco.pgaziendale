<template>
    <div class="h-100 d-flex flex-column">
      <v-card class="mb-4">
        <v-card-text>
          <v-row align="center">
            <v-col cols="3">
              <v-select
                v-model="selectedCampaign"
                :items="campaignsList"
                item-text="title"  
                item-value="id"
                label="Campagna"
                outlined hide-details
              ></v-select>
            </v-col>
            <v-col cols="2">
              <v-slider
                v-model="resolution"
                min="5" max="10"
                label="Risoluzione H3"
                thumb-label
                hide-details
              ></v-slider>
            </v-col>
            <v-col cols="2">
              <v-text-field
                v-model="minTracks"
                label="Tratte minime"
                type="number"
                outlined hide-details
              ></v-text-field>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
  
      <v-tabs v-model="activeTab" background-color="primary" dark>
        <v-tab>Durata Media</v-tab>
        <v-tab>Partenze Utenti</v-tab>
        <v-tab>Tratte / Trips</v-tab>
      </v-tabs>
  
      <div style="height: 600px; width: 100%; position: relative;">
        <div v-if="!selectedCampaign" class="pa-4 text-center">
          Caricamento campagna in corso... (o selezionala dal menu a tendina)
        </div>
  
        <interactive-h3-map 
          v-else
          :geoData="geoJsonData" 
          @map-click="handleMapClick"
        />
      </div>
    </div>
  </template>
  
  <script>
  import InteractiveH3Map from './InteractiveH3Map.vue';
  import { h3Service } from '@/services/h3.services';
  import { mapState, mapActions } from "vuex";
  
  export default {
    components: { InteractiveH3Map },
    data() {
      return {
        activeTab: 0,
        selectedCampaign: null, 
        resolution: 8,
        minTracks: 5,
        geoJsonData: null,
      }
    },
    computed: {
      ...mapState('account', ['user']),
      ...mapState('company', ['actualCompany']),
      ...mapState('campaign', ['allCampaigns']), // FIX: Si chiama 'allCampaigns' nello store
      
      campaignsList() {
        // FIX: Struttura corretta in base al tuo JSON -> allCampaigns.items
        if (!this.allCampaigns || !this.allCampaigns.items) return [];
        return this.allCampaigns.items;
      }
    },
    watch: {
      activeTab() { this.fetchData(); },
      resolution() { this.fetchData(); },
      selectedCampaign(newVal, oldVal) {
        if (newVal !== oldVal) this.fetchData();
      },
      campaignsList(newVal) {
        if (newVal && newVal.length > 0 && !this.selectedCampaign) {
          this.selectedCampaign = newVal[0].id;
        }
      }
    },
    methods: {
      ...mapActions('campaign', ['getAll']), // FIX: L'azione nello store si chiama 'getAll'
  
      async fetchData(h3_cell = '881ea48725fffff') {
        if (!this.selectedCampaign) return;
  
        try {
          if (this.activeTab === 0) {
            this.geoJsonData = await h3Service.getDurationMap(
              'TN', this.selectedCampaign, this.resolution, this.minTracks, h3_cell, true
            );
          }
        } catch (error) {
          console.error("Errore fetch dati h3:", error);
        }
      },
      handleMapClick(latlng) {
        console.log("Cliccato su coordinate:", latlng);
      }
    },
    mounted() {
      // FIX: L'azione 'getAll' vuole la stringa 'id' direttamente, non l'oggetto
      if (this.actualCompany && this.actualCompany.item) {
        this.getAll(this.actualCompany.item.id);
      }
  
      if (this.campaignsList && this.campaignsList.length > 0 && !this.selectedCampaign) {
        this.selectedCampaign = this.campaignsList[0].id;
        this.fetchData();
      }
    }
  }
  </script>