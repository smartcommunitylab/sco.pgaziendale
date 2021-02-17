<template>

	<div id="location" class="w-full lg:w-3/5 rounded-lg lg:rounded-l-lg lg:rounded-r-none shadow-2xl bg-white opacity-75 mx-6 lg:mx-0" v-if="actualLocation">
	

		<div class="p-4 md:p-12 text-center lg:text-left">
			<!-- Image for mobile view-->
			
			<h1 class="text-3xl font-bold pt-8 lg:pt-0">{{actualLocation.city}}</h1>
			<div class="mx-auto lg:mx-0 w-4/5 pt-3 border-b-2 border-green-500 opacity-25"></div>
			<p class="pt-4 text-base font-bold flex items-center justify-center lg:justify-start"><address-icon />{{ actualLocation.address }}{{ actualLocation.streetNumber }}</p>
	
		</div>

	</div>
</template>
<script>
import { mapState,mapActions } from 'vuex';


export default {
    name:"ProfiloLocation",
    data() {
        return {
            
        }
    },
    computed: {
    ...mapState("location", ["actualLocation"])

    },
    methods: {
    ...mapActions("location", { changeActualLocation: "changeActualLocation" }),
    },
}
</script>

<style scoped>

</style>