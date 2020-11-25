<template>
  <div
    class="  m-auto justify-center  flex flex-col-reverse md:flex-row lg:w-1/3 bg-white rounded-lg sm:mx-12  my-4 lg:mx-2 xl:w-1/5 justify-cente shadow-xl"
  >
    <div class="flex flex-col py-2 lg:mx-2 justify-center">
      <h2
        class=" text-xl sm:text-3xl font-semibold break-normal lg:text-left text-center"
      >
        {{ dTitolo }}
      </h2>

      <div
        class="flex flex-col-reverse justify-self-center text-center lg:flex-row lg:text-left"
      >
        <div
          class="flex flex-row lg:flex-col justify-center align-middle text-lg font-light  "
        >
          <span class="font-light">dal {{ dStartDate }} </span
          ><span class="font-light"> al {{ dEndDate }}</span>
        </div>
        <div class="">
          <img
            class="h-48 w-full object-content sm:px-8 py-2 lg:px-0 "
            :src="require('../assets/images/bike.svg')"
          />
        </div>
      </div>

      <div
        class="flex flex-col-reverse xl:flex-row lg:flex-row  lg:mt-auto align-middle  lg:ml-0 t-auto mx-2 lg:mx-0 "
      >
        <template v-if="dUserInCampaign">
          <button
            type="button"
            class=" p-0 xl:mr-2 my-1 inline-flex items-center bg-transparent hover:bg-red-600  font-semibold hover:text-white py-1 px-4 border-2 border-red-600 hover:border-transparent rounded"
          >
            <img
              class="w-4 h-4 mr-2"
              :src="require('../assets/images/increase-up-profit.svg')"
            />Performance
          </button>
          <button
            type="button"
            class="my-1  inline-flex items-center lg:ml-auto bg-transparent hover:bg-blue-600  font-semibold hover:text-white py-1 px-4 border-2 border-blue-600 hover:border-transparent rounded"
          >
            <img
              class="w-4 h-4 mr-2"
              :src="require('../assets/images/information.svg')"
            />Info
          </button>
        </template>
        <template v-else-if="!dFinished">
          <button
            type="button" @click="dettaglio"
            class="my-1  inline-flex items-center lg:ml-auto bg-transparent hover:bg-green-600  font-semibold hover:text-white py-1 px-4 border-2 border-green-600 hover:border-transparent rounded"
          >
            <img
              class="w-4 h-4 mr-2"
              :src="require('../assets/images/information.svg')"
            />Partecipa
          </button>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CampaignCard",
  props: {
    title: String,
    description: String,
    active: Boolean,
    startDate: String,
    endDate: String,
    means: Array,
  },

  data: function() {
    return {
      dTitolo: this.title,
      dDescription: this.description,
      dActive: this.active,
      dStartDate: this.startDate,
      dEndDate: this.endDate,
      dMeans: this.means,
      dFinished: false,
      dUserInCampaign: false,
    };
  },
  methods: {
    dettaglio() {
      this.$store.dispatch('enterCampagna',{id:'1'}).then(() => {
        this.$router.push({name:'campagna',params: { id: '1' }}).catch(()=>{});
       })
    }
    },
  mounted: function() {
    let data = this.endDate.split("-");
    let date = new Date(data[2], data[1], data[0]);
    if (date < new Date()) this.dFinished = true;
  },
};
</script>

<style>
.in_corso {
  right: 0;
  top: 0;
}
</style>
