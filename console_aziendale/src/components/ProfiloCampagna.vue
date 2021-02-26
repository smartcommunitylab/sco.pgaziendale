<template>

	<div id="camapgna" class="w-full lg:w-3/5 rounded-lg lg:rounded-l-lg lg:rounded-r-none shadow-2xl bg-white opacity-75 mx-6 lg:mx-0" v-if="actualCampaign">	
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
		<div class="p-4 md:p-12 text-center lg:text-left">
		<div class=" pt-8 lg:pt-0"> <img :src="actualCampaign.item.logo"/></div>
		<div class="text-3xl font-bold pt-8 lg:pt-0">{{actualCampaign.item.title}}</div>
			<div class="text-3xl font-bold pt-8 lg:pt-0">{{actualCampaign.item.description}}</div>
			<div class="text-3xl font-bold pt-8 lg:pt-0">{{actualCampaign.item.from}}</div>
			<div class="text-3xl font-bold pt-8 lg:pt-0">{{actualCampaign.item.to}}</div>
			<div class="text-3xl font-bold pt-8 lg:pt-0" v-html="actualCampaign.item.rules"></div>
            <div class="text-3xl font-bold pt-8 lg:pt-0"  v-html="actualCampaign.item.privacy"></div>
            <div class="text-3xl font-bold pt-8 lg:pt-0">{{actualCampaign.item.means}}</div>


			
	
		</div>

	</div>
</template>
<script>
import { mapState } from 'vuex';
import EventBus from '../components/eventBus'

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