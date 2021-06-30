<template>
  <div
    id="camapgna"
    class="w-full lg:w-1/2 rounded-lg lg:rounded-l-lg lg:rounded-r-none bg-white opacity-75 mx-6 lg:mx-0"
    v-if="actualCampaign"
  >
    <div class="w-full" v-if="role == 'ROLE_ADMIN' && adminCompany == null">
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
        <h1 class="text-2xl text-center font-bold">Logo</h1>
        <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
        <img :src="actualCampaign.item.logo" />
      </div>
      
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.title">
        <h1 class="text-2xl text-center font-bold">Titolo</h1>
        <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
        <div class="text-center py-2">{{ actualCampaign.item.title }}</div>
      </div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.description">
        <h1 class="text-2xl text-center font-bold">Descrizione</h1>
        <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
        <div class="text-justify py-2">{{ actualCampaign.item.description }}</div>
      </div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.from">
        <h1 class="text-2xl text-center font-bold">Periodo</h1>
        <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
        <div class="text-center pt-2">
          Da: {{ moment(actualCampaign.item.from).format("DD-MM-YYYY") }}
        </div>
      </div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.to">
        <div class="text-center py-2">
          A: {{ moment(actualCampaign.item.to).format("DD-MM-YYYY") }}
        </div>
      </div>
      <h1 class="text-2xl text-center font-bold">Regolamento</h1>
        <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
      <div
        class="text-xl pt-8 lg:pt-2 text-justify"
        v-if="actualCampaign.item.rules"
        v-html="actualCampaign.item.rules"
      ></div>
      <h1 class="text-2xl text-center font-bold">Privacy</h1>
        <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>

      <div
        class="text-xl pt-8 lg:pt-0 lg:text-justify"
        v-if="actualCampaign.item.privacy"
        v-html="actualCampaign.item.privacy"
      ></div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.means">
        <h1 class="text-2xl text-center font-bold">Mezzi associati alla campagna</h1>
        <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
        <div class="text-center">{{ getListOfMeans() }}</div>
      </div>
      <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.companies">
        <h1 class="text-2xl text-center font-bold">Aziende che hanno aderito alla campagna</h1>
        <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
        <div class="text-center" v-for="company in actualCampaign.item.companies" :key="company.id">
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
    ...mapState("account", [ "role"]),
        ...mapState("company", [ "adminCompany"]),

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
