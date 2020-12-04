<template>
  <div
    class="m-auto justify-center flex flex-col  lg:w-1/3 bg-white rounded-lg sm:mx-12 my-4 lg:mx-2 xl:w-1/5 justify-cente shadow-xl"
  >
    <div class="flex flex-col align-middle p-2 h-40">
      <img
        class="mt-auto w-full object-fill sm:px-8 py-2 lg:px-0"
        :src="dLogo"
      />
    </div>
    <div class="flex flex-col py-2 lg:mx-2 justify-center">
      <div class="px-2">
        <h2 class="text-2xl font-semibold break-normal ">
          {{ dTitolo }}
        </h2>

        <div class="flex flex-row justify-self-center text-sm">
          <span class="font-light">dal {{ dStartDate }} al {{ dEndDate }}</span>
        </div>
      </div>
      <div class="flex flex-row lg:mt-auto align-middle pt-4 text-sm">
        <template v-if="dUserInCampaign">
          <button
            type="button"
            class="p-0 text-blue-600 hover:bg-blue-600 rounded-md  my-1 inline-flex items-center bg-transparent  font-semibold hover:text-white py-1 px-2  "
            @click="dettaglio"
          >
            <performance class="pr-1" />
            Performance
          </button>
          <button
            type="button"
            @click="dettaglio"
            class="p-0 text-blue-600 hover:bg-blue-600 rounded-md  my-1 inline-flex items-center bg-transparent  font-semibold hover:text-white py-1 px-2  "
          >
            <info-outline-icon class="pr-1" />Info
          </button>
        </template>
        <template v-else-if="!dFinished">
          <button
            type="button"
            @click="dettaglio"
            class="my-1 inline-flex items-center lg:ml-auto bg-transparent hover:bg-green-600 font-semibold hover:text-white py-1 px-4 border-2 border-green-600 hover:border-transparent rounded"
          >
            <info-outline-icon />Partecipa
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
    id: String,
    title: String,
    logo: String,
    description: String,
    active: Boolean,
    startDate: String,
    endDate: String,
    means: Array,
    userInCampaign: Boolean,
  },

  data: function() {
    return {
      dTitolo: this.title,
      dLogo: this.logo,
      dDescription: this.description,
      dActive: this.active,
      dStartDate: this.startDate,
      dEndDate: this.endDate,
      dMeans: this.means,
      dFinished: false,
      dUserInCampaign: this.userInCampaign,
    };
  },
  methods: {
    dettaglio() {
      this.$store
        .dispatch("enterCampagna", {
          id: this.id,
          title: this.title,
          description: this.description,
          logo: this.logo,
          active: this.active,
          startDate: this.startDate,
          endDate: this.endDate,
          means: this.means,
          userInCampaign: this.userInCampaign,
        })
        .then(() => {
          this.$router
            .push({ name: "campagna", params: { id: this.id } })
            .catch(() => {});
        });
    },
  },
  mounted: function() {
    let data = this.endDate.split("-");
    let date = new Date(data[0], data[1], data[2]);
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
