<template>
  <div class="">
    <div class="flex flex-col">
      <!-- tmp-->
      <!-- <h1
        class="justify-self-center text-center text-5xl pt-2 lg:text-6xl font-semibold pb-4"
      >
        Campagne
      </h1> -->
       <div class="text-md">
          <nav class="flex flex-row text-white bg-primary">
            <button
              class="flex-1 py-2 px-6 block focus:outline-none font-medium sm:bg-green-400 hover:bg-blue-700"
              :class="mode == 'MY' ? 'border-blue-300 border-b-4 text-blue-300' : ''"
              @click="changeMode('MY')"
            >
              Le mie campagne</button
            ><button
              class="flex-1 py-2 px-6 block focus:outline-none hover:bg-blue-700"
              :class="mode == 'ALL' ? ' border-blue-300 border-b-4 text-blue-300' : ''"
              @click="changeMode('ALL')"
            >
              Tutte le campagne
            </button>
          </nav>
        </div>
         <div class="bg-opacity-0 py-2">
        <div
          v-show="mode == 'MY'"
          class="justify-center text-center w-full text-xl"
        >
        <div class="flex flex-col sm:flex-row sm:flex-wrap sm:justify-center md:px-12">
        <template v-if="!campaignToShow.length">
          <div
            class="m-auto justify-center flex flex-col-reverse bg-white rounded-lg w-full my-4 text-center justify-cente shadow-xl p-12"
          >
            Nessuna campagna presente
          </div>
        </template>
        <template v-else v-for="campaign in campaignToShow">
          <template
            ><campaign-card
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
              :rules="campaign.rules"
              :userInCampaign="campaign.userInCampaign"
              :subscribedCompany="campaign.subscribedCompany"
            /> </template
        ></template>
      </div>
        </div>
        <div id="chart_container" v-show="mode == 'ALL'" class="">
          <div class="flex flex-col sm:flex-row sm:flex-wrap sm:justify-center md:px-12">
        <template v-if="!campaignToShow.length">
          <div
            class="m-auto justify-center flex flex-col-reverse bg-white rounded-lg w-full my-4 text-center justify-cente shadow-xl p-12"
          >
            Nessuna campagna presente
          </div>
        </template>
        <template v-else v-for="campaign in campaignToShow">
          <template
            ><campaign-card
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
              :rules="campaign.rules"
              :userInCampaign="campaign.userInCampaign"
              :subscribedCompany="campaign.subscribedCompany"
            /> </template
        ></template>
      </div>
        </div>
      </div>
      <!-- <context-menu
        ref="menu"
        class="mx-auto -mb-8"
        @click.native="sortCampaign()"
        v-bind:_options="[
          { name: 'my', view_name: 'Le mie Campagne', default: true },
          { name: 'active', view_name: 'Campagne Attive', default: false },
          { name: 'finished', view_name: 'Campagne Concluse', default: false },
        ]"
      /> -->

      
    </div>
  </div>
</template>

<script>
import DataApi from "../communication/dataApi";
// import ContextMenu from "../Components/ContextMenu.vue";
import CampaignCard from "../Components/CampaignCard.vue";
import EventBus from "../communication/eventBus";
export default {
  name: "Campagne",
  components: {  CampaignCard },

  data: function () {
    return {
      fakeCampaigns: [],
      myCampaigns: [],
      allCampaigns: [],
      // currentView: "my",
      mode: "MY",
      // user:{}
    };
  },
  methods: {
        changeMode(mode) {
      if (this.mode == mode) return;
      this.mode = mode;
      // this.buildChart(this.originalStats);
    },
    // sortCampaign: function () {
    //   if (this.$refs["menu"].getCurrentOption().name != this.currentView)
    //     this.currentView = this.$refs["menu"].getCurrentOption().name;
    // },
  },
  computed: {
    // getCurrentViewTitle: function () {
    //   let toRtn = "Campagne Concluse";
    //   if (this.currentView == "my") {
    //     toRtn = "Le mie Campagne";
    //   } else if (this.currentView == "active") {
    //     toRtn = "Campagne Attive";
    //   }
    //   return toRtn;
    // },
    campaignToShow: function () {
      let toRtn = this.myCampaigns;

      if (this.mode == "ALL") {
        toRtn = [];
        this.allCampaigns.forEach((campaign) => {
          if (
            !this.myCampaigns.some(function isUserInCampaign(element) {
              return element.id == campaign.id;
            })
          ) {
            toRtn.push(campaign);
          }
        });
      } else if (this.mode == "MY") {
        //order by date
        // toRtn = [];
        // this.allCampaigns.forEach((campaign) => {
        //   if (new Date(campaign.to) < new Date()) {
        //     toRtn.push(campaign);
        //   }
        // });
      }
      return toRtn;
    },
  },
  created: function() {
    this.$store.dispatch("storePage", {title:"Campagne",back:false});

  },
  mounted: function () {
    let loader = this.$loading.show({
      canCancel: false,
      backgroundColor: "#000",
      color: "#fff",
    });

    DataApi.getPublicCampaigns().then(
      (res) => {
        this.allCampaigns = res.data.content;
        console.log(this.campaigns);
      },
      (err) => {
        console.log(err);
        loader.hide();
        EventBus.$emit("snack-open");
      }
    );

    DataApi.getUser().then((res) => {
      let user = res.data;
      this.$store.dispatch("storeUser", res.data).then(
        () => {
          DataApi.getMyCampaigns().then(
            (res) => {
              this.myCampaigns = res.data;
              this.myCampaigns.forEach((campaign) => {
                campaign.userInCampaign = true;
                campaign.subscribedCompany = {
                  companyCode: user.roles[0].subscriptions.find(
                    (x) => x.campaign == campaign.id
                  ).companyCode,
                };
              });
              console.log(this.campaigns);
              loader.hide();
              if (this.myCampaigns.length == 0) {
                // this.$refs["menu"].currentOption = {
                //   name: "active",
                //   view_name: "Campagne Attive",
                //   default: false,
                // };
                this.mode = "ALL";
              }
            },
            (err) => {
              console.log(err);
              loader.hide();
            }
          );
        },
        (err) => {
          console.log(err);
          loader.hide();
        }
      );
    });
  },
};
</script>

<style></style>
