<template>
  <v-col cols="4">
    <v-card elevation="2">
      <v-card-title>{{ actualCampaign.item.title }}</v-card-title>
      
      <v-img
          v-if="actualCampaign.item.logo"
          class="block mx-auto h-48 w-48 bg-contain bg-center bg-no-repeat"
          :style="{ backgroundImage: 'url(' + actualCampaign.item.logo + ')' }"
          height="200px"
      />
      
      <v-card-text>

        <v-row class="mb-0">
          <v-col
            cols="12"
            class="mb-0"
          >
            <p class="text-subtitle-2 ml-2 mb-2"> Mezzi associati </p>
            <v-spacer></v-spacer>
            <p class="text-body-2 ml-4 mb-0"> {{ getListOfMeans() }} </p>
          </v-col>
        </v-row>
        <v-row class="my-0">
          <v-col
            cols="12"
            class="mt-0 mb-5"
          >
            <p class="text-subtitle-2 ml-2 mb-2"> Aziende che hanno aderito </p>
            <v-spacer></v-spacer>
            <p class="text-body-2 ml-4 mb-0" v-for="company in actualCampaign.item.companies" :key="company.id">
              {{ company.name }}
            </p>
          </v-col>
        </v-row>

        <v-expansion-panels>
          <v-expansion-panel>
            <v-expansion-panel-header>
              Descrizione
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              {{ actualCampaign.item.description }}
            </v-expansion-panel-content>
          </v-expansion-panel>        
          <v-expansion-panel>
            <v-expansion-panel-header>
              Regolamento
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <div
                v-html="actualCampaign.item.rules"
              ></div>
            </v-expansion-panel-content>
          </v-expansion-panel>
          <v-expansion-panel>
            <v-expansion-panel-header>
              Privacy
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <div
                v-html="actualCampaign.item.privacy"
              ></div>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-card-text>
      <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn icon @click="openModal({type:'campaignFormEdit', object:null})">
              <v-icon>mdi-pencil</v-icon>
          </v-btn>

          <v-btn icon @click="openModal({type:'deleteCampaign', object:null})">
              <v-icon>mdi-delete</v-icon>
          </v-btn>

      </v-card-actions>
    </v-card>
  </v-col>
</template>

<script>
import { mapActions, mapState } from "vuex";
import { campaignService } from "@/services";

export default {
  name: "ProfiloCampagna",

  methods: {
    ...mapActions("modal", {openModal: "openModal"}),

    getListOfMeans() {
      return campaignService.getTextOfMeans(this.actualCampaign.item.means);
    },

    editCampaign() {
    },
  },

  computed: {
    ...mapState("campaign", ["actualCampaign"]),
    ...mapState("account", [ "role"]),
    ...mapState("company", [ "adminCompany"]),

  },
};
</script>

<style scoped>
.entry-description {
  text-align: center;
  width: 100%;
  font-size: large;
  font-weight: bold;
  border-bottom:1px solid black;
  margin-bottom: 1em;
  margin-top:1em;
}
</style>
