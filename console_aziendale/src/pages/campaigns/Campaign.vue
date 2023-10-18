<template>
    <v-card elevation="2">
      <v-card-title>{{ actualCampaign.item.title }}</v-card-title>
      
      <v-img
          v-if="actualCampaign.item.logo"
          class="block mx-auto h-48 w-48 bg-contain bg-center bg-no-repeat"
          :style="{ backgroundImage: 'url(' + actualCampaign.item.logo + ')' }"
          height="200px"
      />
      
      <v-card-text>

        <!-- <v-row class="my-0">
          <v-col
            cols="12"
            class="mt-0 mb-5"
          >
            <p class="text-h6 ml-2 mb-2"> Aziende che hanno aderito </p>
            <v-spacer></v-spacer>
            <p class="text-body-2 ml-4 mb-0" v-for="company in actualCampaign.item.companies" :key="company.id">
              {{ company.name }}
            </p>
          </v-col>
        </v-row> -->
        
        <!-- <v-row class="mb-0">
          <v-col
            cols="12"
            class="mb-0"
          >
            <p class="text-h6 ml-2 mb-2"> Mezzi associati </p>
            <v-spacer></v-spacer>
            <p class="text-body-2 ml-4 mb-0"> {{ getListOfMeans() }} </p>
          </v-col>
        </v-row> -->


        <!-- <v-row class="my-0">
          <v-col
            cols="12"
            class="mt-0 mb-0 py-0"
          >
            <p class="text-h6 ml-2 mb-0"> Limiti </p>
            <v-spacer></v-spacer>
            <div class="ml-2">
              <v-simple-table>
                <tbody>
                <tr>
                  <td>Punti ({{actualCampaign.item.virtualScore.label}}):</td>
                  <td cols="auto" v-for="lim in actualCampaign.item.scoreLimits" :key="lim.span">{{lim.value}} per {{lim.span == 'day' ? 'giorno': lim.span == 'week' ? 'settimana' : 'mese'}}</td>
                </tr>
                <tr>
                  <td>Viaggi:</td>
                  <td cols="auto" v-for="lim in actualCampaign.item.trackLimits" :key="lim.span">{{lim.value}} per {{lim.span == 'day' ? 'giorno': lim.span == 'week' ? 'settimana' : 'mese'}}</td>
                </tr>
                </tbody>
              </v-simple-table>
            </div>
          </v-col>
        </v-row>
        <v-row class="my-0">
          <v-col
            cols="12"
            class="mt-0 mb-0"
          >
            <p class="text-h6 ml-2"> Calcolo </p>
            <v-spacer></v-spacer>
            <div class="ml-2">
              <v-simple-table>
                <tbody>
                <tr>
                  <td cols="auto" v-for="m in means" :key="m.value">
                    {{m.text}} 
                  </td>
                </tr>
                <tr>
                  <td cols="auto" v-for="m in means" :key="m.value">
                    <span v-if="actualCampaign.item.virtualScore[m.value]">1 {{getMetricLabel(actualCampaign.item.virtualScore[m.value].metric)}} = {{actualCampaign.item.virtualScore[m.value].coefficient}} {{actualCampaign.item.virtualScore.label}}</span>
                    <span v-else> - </span></td>
                </tr>
                </tbody>
              </v-simple-table>
            </div>
          </v-col>
        </v-row> -->

        <v-expansion-panels>
          <v-expansion-panel>
            <v-expansion-panel-header>
              <span class="text-h6">Aziende aderenti</span>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <p class="text-body-2 ml-4 mb-0" v-for="company in actualCampaign.item.companies" :key="company.id">
                {{ company.name }}
              </p>
            </v-expansion-panel-content>
          </v-expansion-panel>        
          <v-expansion-panel>
            <v-expansion-panel-header>
              <span class="text-h6">Mezzi</span>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
            <p class="text-body-2 ml-4 mb-0"> {{ getListOfMeans() }} </p>
            </v-expansion-panel-content>
          </v-expansion-panel>        
          <v-expansion-panel>
            <v-expansion-panel-header>
              <span class="text-h6">Limiti</span>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <v-simple-table>
                <tbody>
                <tr>
                  <td>Punti ({{actualCampaign.item.virtualScore.label}}):</td>
                  <td cols="auto" v-for="lim in actualCampaign.item.scoreLimits" :key="lim.span">{{lim.value}} per {{lim.span == 'day' ? 'giorno': lim.span == 'week' ? 'settimana' : 'mese'}}</td>
                </tr>
                <tr>
                  <td>Viaggi:</td>
                  <td cols="auto" v-for="lim in actualCampaign.item.trackLimits" :key="lim.span">{{lim.value}} per {{lim.span == 'day' ? 'giorno': lim.span == 'week' ? 'settimana' : 'mese'}}</td>
                </tr>
                </tbody>
              </v-simple-table>
            </v-expansion-panel-content>
          </v-expansion-panel>        
          <v-expansion-panel>
            <v-expansion-panel-header>
              <span class="text-h6">Calcolo</span>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
               <v-simple-table>
                <tbody>
                <tr>
                  <td cols="auto" v-for="m in means" :key="m.value">
                    {{m.text}} 
                  </td>
                </tr>
                <tr>
                  <td cols="auto" v-for="m in means" :key="m.value">
                    <span v-if="actualCampaign.item.virtualScore[m.value]">1 {{getMetricLabel(actualCampaign.item.virtualScore[m.value].metric)}} = {{actualCampaign.item.virtualScore[m.value].coefficient}} {{actualCampaign.item.virtualScore.label}}</span>
                    <span v-else> - </span></td>
                </tr>
                </tbody>
              </v-simple-table>
            </v-expansion-panel-content>
          </v-expansion-panel>        


          <v-expansion-panel>
            <v-expansion-panel-header>
              <span class="text-h6">Descrizione</span>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <div v-html="actualCampaign.item.description"></div>
            </v-expansion-panel-content>
          </v-expansion-panel>        
          <v-expansion-panel>
            <v-expansion-panel-header>
              <span class="text-h6">Regolamento</span>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <div
                v-html="actualCampaign.item.rules"
              ></div>
            </v-expansion-panel-content>
          </v-expansion-panel>
          <v-expansion-panel>
            <v-expansion-panel-header>
              <span class="text-h6">Privacy</span>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <div
                v-html="actualCampaign.item.privacy"
              ></div>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-card-text>
      <!-- <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn icon @click="openModal({type:'campaignFormEdit', object:null})">
              <v-icon>mdi-pencil</v-icon>
          </v-btn>

          <v-btn icon @click="openModal({type:'deleteCampaign', object:null})">
              <v-icon>mdi-delete</v-icon>
          </v-btn>

      </v-card-actions> -->
    </v-card>
</template>

<script>
import { mapActions, mapState } from "vuex";
import { campaignService } from "@/services";

export default {
  name: "ProfiloCampagna",
  data(){
    return {
      means: campaignService.getArrayMeans()
    }
  },

  methods: {
    ...mapActions("modal", {openModal: "openModal"}),

    getListOfMeans() {
      return campaignService.getTextOfMeans(this.actualCampaign.item.means);
    },
    getMetricLabel(m) {
      return m == 'distance' ? 'm' : m == 'co2' ? 'CO2' : m == 'time' ? 'sec' : 'viaggio'
    }
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
