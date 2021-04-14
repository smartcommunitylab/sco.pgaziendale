<template>
  <div
    id="camapgna"
    class="w-full lg:w-1/2 rounded-lg lg:rounded-l-lg lg:rounded-r-none bg-white opacity-75 mx-6 lg:mx-0"
    v-if="actualCampaign"
  >
    <div class="w-full">
      <button
        @click="deleteCampaign"
        class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
      >
        <delete-icon />
      </button>
      <button
        @click="editCampaign"
        class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
      >
        <pencil-outline-icon />
      </button>
    </div>
    <div v-if="actualCampaign.item" class="p-4 md:p-12 text-center lg:text-left">
      <div class="pt-8 lg:pt-0" v-if="actualCampaign.item.logo">
        <div class="entry-description">Logo</div>
        <img :src="actualCampaign.item.logo" />
      </div>
      <div class="text-xl font-bold pt-8 lg:pt-0" v-if="actualCampaign.item.title">
        <div class="entry-description">Titolo</div>
        <div>{{ actualCampaign.item.title }}</div>
      </div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.description">
        <div class="entry-description">Descrizione</div>
        <div>{{ actualCampaign.item.description }}</div>
      </div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.from">
        <div class="entry-description">Periodo</div>

        Da: {{ moment(actualCampaign.item.from).format("DD-MM-YYYY") }}
      </div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.to">
        A: {{ moment(actualCampaign.item.to).format("DD-MM-YYYY") }}
      </div>
      <div class="entry-description">Regolamento</div>

      <div
        class="text-xl pt-8 lg:pt-0"
        v-if="actualCampaign.item.rules"
        v-html="actualCampaign.item.rules"
      ></div>
      <div class="entry-description">Privacy</div>

      <div
        class="text-xl pt-8 lg:pt-0"
        v-if="actualCampaign.item.privacy"
        v-html="actualCampaign.item.privacy"
      ></div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.means">
        <div class="entry-description">Mezzi associati alla campagna</div>
        <div>{{ getListOfMeans() }}</div>
      </div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.companies">
        <div class="entry-description">Aziende che hanno aderito alla campagna</div>
        <div v-for="company in actualCampaign.item.companies" :key="company.id">
          {{ company.name }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from "vuex";
import EventBus from "@/components/eventBus";
import { campaignService } from "../../services";

export default {
  name: "ProfiloCampagna",
  data() {
    return {};
  },
  computed: {
    ...mapState("campaign", ["actualCampaign"]),
  },
  methods: {
    getListOfMeans() {
      return campaignService.getTextOfMeans(this.actualCampaign.item.means);
    },
    deleteCampaign() {
      EventBus.$emit("DELETE_CAMPAIGN", this.actualCampaign);
    },
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
