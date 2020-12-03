<template>
  <div class="bg-blue-600">
    <div>
      <h1
        class="justify-self-center text-center text-white text-4xl pt-2 md:text-6xl font-semibold pb-6"
      >
        Le mie campagne
      </h1>
      <hr class="mx-12" />

      <div
        class="flex flex-col sm:flex-row sm:flex-wrap sm:justify-center md:justify-start md:px-12"
      >
        <template v-if="!myCampaigns || !myCampaigns.length">
          <div
            class="m-auto justify-center flex flex-col-reverse bg-white rounded-lg w-full my-4 text-center justify-cente shadow-xl p-12"
          >
            Nessuna campagna presente
          </div>
        </template>
        <template v-else v-for="campaign in myCampaigns">
          <campaign-card
            class=""
            :key="campaign.id"
            :id="campaign.id"
            :logo="campaign.logo"
            :title="campaign.title"
            :description="campaign.description"
            :startDate="campaign.from"
            :endDate="campaign.to"
            :active="campaign.active"
            :means="campaign.means"
            :userInCampaign="campaign.userInCampaign"
          />
        </template>
      </div>
    </div>
    <div>
      <h1
        class="justify-self-center text-center text-white text-4xl pt-2 md:text-6xl font-semibold pb-6"
      >
        Campagne Attive
      </h1>
      <hr class="mx-12" />

      <div
        class="flex flex-col sm:flex-row sm:flex-wrap sm:justify-center md:justify-start md:pl-12"
      >
        <template v-for="campaign in allCampaigns">
          <campaign-card
            class=""
            :key="campaign.id"
            :id="campaign.id"
            :logo="campaign.logo"
            :title="campaign.title"
            :description="campaign.description"
            :startDate="campaign.from"
            :endDate="campaign.to"
            :active="campaign.active"
            :means="campaign.means"
            :userInCampaign="campaign.userInCampaign"
          />
        </template>
      </div>
    </div>
    <div>
      <h1
        class="justify-self-center text-center text-white text-4xl pt-2 md:text-6xl font-semibold pb-6"
      >
        Campagne Concluse
      </h1>
      <hr class="mx-12" />
      <div
        class="flex flex-col sm:flex-row sm:flex-wrap sm:justify-center md:justify-start md:pl-12"
      >
        <template v-for="campaign in fakeCampaigns">
          <campaign-card
            class=""
            :key="campaign.id"
            :title="campaign.title"
            :logo="campaign.logo"
            :description="campaign.description"
            :startDate="campaign.startDate"
            :endDate="campaign.endDate"
            :active="campaign.active"
            :means="campaign.means"
            :userInCampaign="campaign.userInCampaign"
          />
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import CampaignCard from "../Components/CampaignCard.vue";
import DataApi from "../communication/dataApi";
export default {
  name: "Campagne",
  components: { CampaignCard },

  data: function () {
    return {
      fakeCampaigns: [],
      myCampaigns: [],
      allCampaigns: [],
      // user:{}
    };
  },
  created: function () {
    //tmp
    let x = require("../tmp-data/campaigns").campaigns;
    this.fakeCampaigns = x;
    // console.log(x);
    DataApi.getPublicCampaigns().then(
      (res) => {
        this.allCampaigns = res.data.content;
        console.log(this.campaigns);
        //todo userInCampaign calculation
      },
      (err) => {
        console.log(err);
      }
    );
    DataApi.getMyCampaigns().then(
      (res) => {
        this.myCampaigns = res.data;
        this.myCampaigns.forEach(campaign=>{
          campaign.userInCampaign=true;
        })
        console.log(this.campaigns);
      },
      (err) => {
        console.log(err);
      }
    );
    // // just example on how to use it
    // DataApi.getCompaniesOfCampaign('prova').then (res => {
    //   this.campaigns = res.data;
    //    console.log(this.campaigns)
    // }, err => {
    //   console.log(err)
    // })
    
    DataApi.getUser().then((res) => {
      this.$store.dispatch("storeUser", res.data).then(
        () => {},
        (err) => {
          console.log(err);
        }
      );
    });
  },
};
</script>

<style></style>
