<template>
  <div
    class=" m-auto justify-center flex flex-col  lg:w-1/3 bg-white rounded-lg my-4 lg:mx-2 xl:w-1/5 justify-cente shadow-xl"
  >
    <div class="flex flex-col align-middle p-2 pt-0 h-40" >
      <img 
        class="mt-auto  object-scale-down sm:px-8 py-2 lg:px-0  h-48" v-if="dLogo"
        :src="dLogo"
      />
    </div>
    
    <div class="flex flex-col py-6 lg:mx-2 justify-center">
      <div class="px-2">
        <h2 class="text-2xl font-semibold break-normal ">
          {{ dTitolo }}
        </h2>

        <div class="flex flex-row justify-self-center text-sm">
          <span class="font-light">{{ printDate }} </span>
        </div>
        <div class="pt-4 break-words text-sm">
          <p>{{ description }}</p>
        </div>
      </div>

      <div class="flex flex-row lg:mt-auto align-middle pt-4 text-sm">
        <template v-if="dUserInCampaign">
          <button
            type="button"
            class="p-0 text-primary hover:bg-primary   my-1 inline-flex items-center bg-transparent  font-semibold hover:text-white py-1 px-2  "
            @click="performance"
          >
            <performance-icon class="pr-1" />
            Performance
          </button>
          <button
            type="button"
            @click="dettaglio"
            class="p-0 text-primary hover:bg-primary  my-1 inline-flex items-center bg-transparent  font-semibold hover:text-white py-1 px-2  "
          >
            <info-outline-icon class="pr-1" />Info
          </button>
        </template>
        <template v-else-if="!dFinished">
          <button
            type="button"
            @click="dettaglio"
            class="p-0 text-primary hover:bg-primary rounded-md  my-1 inline-flex items-center bg-transparent  font-semibold hover:text-white py-1 px-2  "
          >
            <join-icon class="pr-1" />Partecipa
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
    rules:String,
    userInCampaign: Boolean,
    subscribedCompany: Object,
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
       dRules: this.rules,
      dUserInCampaign: this.userInCampaign,
      dSubscribedCompany: this.subscribedCompany,
    };
  },
  computed: {
    printDate: function() {
      let from = new Intl.DateTimeFormat("it", {
        year: "numeric",
        month: "long",
        day: "numeric",
      }).format(new Date(this.startDate));

      let to = new Intl.DateTimeFormat("it", {
        year: "numeric",
        month: "long",
        day: "numeric",
      }).format(new Date(this.endDate));

      return from + " - " + to;
    },
  },
  methods: {
    dettaglio() {
      this.$store
        .dispatch("storeCampagna", {
          id: this.id,
          title: this.title,
          description: this.description,
          logo: this.logo,
          active: this.active,
          startDate: this.startDate,
          endDate: this.endDate,
          means: this.means,
          rules: this.rules,
          userInCampaign: this.userInCampaign,
          subscribedCompany: this.subscribedCompany,
        })
        .then(() => {
          this.$router
            .push({ name: "campagna", params: { id: this.id } })
            .catch(() => {});
        });
    },
    performance() {
      this.$store
        .dispatch("storeCampagna", {
          id: this.id,
          title: this.title,
          description: this.description,
          logo: this.logo,
          active: this.active,
          startDate: this.startDate,
          endDate: this.endDate,
          means: this.means,
                    rules: this.rules,
          userInCampaign: this.userInCampaign,
          subscribedCompany: this.subscribedCompany,
        })
        .then(() => {
          this.$router
            .push({ name: "myperformance", params: { id: this.id } })
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
