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
              Periodo
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <v-row>
                <v-col cols="2" class="pr-0">
                  <p class="font-weight-bold">
                    Da:
                  </p>
                </v-col>
                <v-col cols="3" class="pl-0">
                  {{ moment(actualCampaign.item.from).format("DD-MM-YYYY") }} 
                </v-col>
                <v-spacer></v-spacer>
                <v-col cols="2" class="pr-0">
                  <p class="font-weight-bold">
                    A:
                  </p>
                </v-col>
                <v-col cols="3" class="pl-0">
                  {{ moment(actualCampaign.item.to).format("DD-MM-YYYY") }}
                </v-col>
              </v-row>
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
          <v-expansion-panel>
            <v-expansion-panel-header>
              Mezzi associati alla campagna
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              {{ getListOfMeans() }}
            </v-expansion-panel-content>
          </v-expansion-panel>
          <v-expansion-panel>
            <v-expansion-panel-header>
              Aziende che hanno aderito alla campagna
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <div v-for="company in actualCampaign.item.companies" :key="company.id">
                {{ company.name }}
              </div>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-card-text>
      <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn icon @click="editCampaign">
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
import EventBus from "@/components/eventBus";
import { campaignService } from "../../services";

export default {
  name: "ProfiloCampagna",
  data() {
    return {};
  },
  computed: {
    ...mapState("campaign", ["actualCampaign"]),
    ...mapState("account", [ "role"]),
        ...mapState("company", [ "adminCompany"]),

  },
  methods: {
    getListOfMeans() {
      return campaignService.getTextOfMeans(this.actualCampaign.item.means);
    },
    ...mapActions("modal", {openModal: "openModal"}),
    editCampaign() {
      EventBus.$emit("EDIT_CAMPAIGN", this.actualCampaign);
    },
  },
  mounted() {},
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
