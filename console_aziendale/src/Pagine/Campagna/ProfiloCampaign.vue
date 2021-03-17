<template>

	<div id="camapgna" class="w-full lg:w-1/2 rounded-lg lg:rounded-l-lg lg:rounded-r-none  bg-white opacity-75 mx-6 lg:mx-0" v-if="actualCampaign">	
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
		<div class=" pt-8 lg:pt-0" v-if="actualCampaign.item.logo"> <img :src="actualCampaign.item.logo"/></div>
		<div class="text-xl font-bold pt-8 lg:pt-0" v-if="actualCampaign.item.logo">{{actualCampaign.item.title}}</div>
			<div class="text-xl   pt-8 lg:pt-0" v-if="actualCampaign.item.description">{{actualCampaign.item.description}}</div>
			<div class="text-xl   pt-8 lg:pt-0" v-if="actualCampaign.item.from">{{actualCampaign.item.from}}</div>
			<div class="text-xl  pt-8 lg:pt-0" v-if="actualCampaign.item.to">{{actualCampaign.item.to}}</div>
			<div class="text-xl  pt-8 lg:pt-0" v-if="actualCampaign.item.rules" v-html="actualCampaign.item.rules"></div>
      <div class="text-xl  pt-8 lg:pt-0"  v-if="actualCampaign.item.privacy" v-html="actualCampaign.item.privacy"></div>
        <div class="text-xl pt-8 lg:pt-0" v-if="actualCampaign.item.means">{{getListOfMeans()}}</div>


			
	
		</div>

	</div>
</template>
<script>
import { mapState } from 'vuex';
import EventBus from '@/components/eventBus'
import  {campaignService}  from '../../services';

export default {
    name:"ProfiloCampagna",
    data() {
        return {
            
        }
    },
    computed: {
    ...mapState("campaign", ["actualCampaign"])

    },
    methods: {
      getListOfMeans() {
        return campaignService.getTextOfMeans(this.actualCampaign.item.means);
      },
	deleteCampaign() {
		EventBus.$emit("DELETE_CAMPAIGN",this.actualCampaign);
	},
	editCampaign() {
		EventBus.$emit("EDIT_CAMPAIGN",this.actualCampaign);
	}    },
}
</script>

<style scoped>

</style>