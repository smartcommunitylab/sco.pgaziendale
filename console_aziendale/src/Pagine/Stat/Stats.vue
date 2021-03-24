<template>
  <div>
    Statistiche
    <div class="flex flex-col lg:flex-row">
      <div class="mx-2 my-2 flex flex-col lg:w-4/6 bg-white p-2">
        Grafici
        <div id="chart_container" class="">
          <canvas ref="canvas" class="p-4"></canvas>
        </div>
      </div>
      <div
        class="mx-2 my-2 flex flex-col lg:w-2/6 text-white p-2 bg-primary rounded-xl border-2 h-full"
      >
        <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col">
          <label for="sub_select">Seleziona una campagna</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none"
            name="sub_select"
            id="campaign"
            v-model="selectedCampaign"
            @change="changeCampaign($event)"
            required
          >
            <option disabled value="">Seleziona una campagna</option>
            <option
              v-for="campaign in allCampaigns.items"
              :key="campaign.id"
              :value="campaign"
            >
              {{ campaign.title }}
            </option>
          </select>
        </div>
        <div
          class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
          v-if="role == 'ROLE_ADMIN'"
        >
          <label for="sub_select">Seleziona una azienda</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none"
            name="sub_select"
            id="campaign"
            v-model="showFilter.selectedCompany"
            @change="changeCompany($event)"
            required
          >
            <option disabled value="">Seleziona una azienda</option>
            <option
              v-for="company in allCompanies.items"
              :value="company"
              :key="company.id"
            >
              {{ company.name }}
            </option>
          </select>
        </div>
        <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col">
          <label for="sub_select">Seleziona una sede</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none"
            name="sub_select"
            id="campaign"
            v-model="showFilter.selectedSede"
            @change="changeSede($event)"
            required
          >
            <option disabled value="">Seleziona una sede</option>
            <option
              v-for="location in allLocations.items"
              :value="location"
              :key="location.id"
            >
              {{ location.address }}
            </option>
          </select>
        </div>
        <div class="flex flex-col mt-3 justify-stretch">
          <div class="flex flex-col mt-3 justify-stretch w-full">
            <label for="sub_select">Dal</label>

            <VueTailwindPicker
              :theme="{
                background: '#1A202C',
                text: 'text-white',
                border: 'border-gray-700',
                currentColor: 'text-gray-200',
                navigation: {
                  background: 'bg-gray-800',
                  hover: 'hover:bg-gray-700',
                  focus: 'bg-gray-700',
                },
                picker: {
                  rounded: 'rounded-md',
                  selected: {
                    background: 'bg-teal-400',
                    border: 'border-teal-400',
                    hover: 'hover:border-teal-400',
                  },
                  holiday: 'text-red-400',
                  weekend: 'text-green-400',
                  event: 'bg-blue-500',
                },
                event: {
                  border: 'border-gray-700',
                },
              }"
              @change="(v) => (checkin = v)"
            >
              <input type="text" class="text-primary w-full" v-model="checkin" />
            </VueTailwindPicker>
          </div>
          <div class="flex flex-col mt-3 justify-stretch w-full">
            <label for="sub_select">Al</label>

            <VueTailwindPicker
              :theme="{
                background: '#1A202C',
                text: 'text-white',
                border: 'border-gray-700',
                currentColor: 'text-gray-200',
                navigation: {
                  background: 'bg-gray-800',
                  hover: 'hover:bg-gray-700',
                  focus: 'bg-gray-700',
                },
                picker: {
                  rounded: 'rounded-md',
                  selected: {
                    background: 'bg-teal-400',
                    border: 'border-teal-400',
                    hover: 'hover:border-teal-400',
                  },
                  holiday: 'text-red-400',
                  weekend: 'text-green-400',
                  event: 'bg-blue-500',
                },
                event: {
                  border: 'border-gray-700',
                },
              }"
              @change="(v) => (checkin = v)"
            >
              <input type="text" v-model="checkin" class="text-primary w-full" />
            </VueTailwindPicker>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import VueTailwindPicker from "vue-tailwind-picker";
export default {
  components: {
    VueTailwindPicker,
  },
  data() {
    return {
      checkin: "",
      showFilter: {
        selectedCampaign: null,
        azienda: null,
        sede: null,
        period: {
          dal: null,
          al: null,
        },
      },
      sendFilter: {
        selectedCampaign: null,
        azienda: null,
        sede: null,
        period: {
          dal: null,
          al: null,
        },
      },
    };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
    ...mapState("campaign", ["allCampaigns"]),
    ...mapState("company", ["allCompanies"]),
    ...mapState("location", ["allLocations"]),
  },
  created() {
    //default company if AA
    this.sendFilter.azienda = null;
    if (this.role == "ROLE_COMPANY_ADMIN") {
      var company = this.getFirstCompany(this.user);
      if (company) this.sendFilter.azienda = company.companyId;
    }
    //get all the campaigns, if azienda is set, filter by it
    this.getAllCampaigns(this.sendFilter.azienda);

    if (this.role == "ROLE_ADMIN") this.getAllCompanies();
    if (this.sendFilter.azienda) this.getAllLocations(this.sendFilter.azienda);
  },
  mounted() {
    this.changePage({title: 'Statistiche',
                route: '/stats'})
    this.buildChart;
  },
  methods: {
    ...mapActions("campaign", { getAllCampaigns: "getAll" }),
    ...mapActions("company", { getAllCompanies: "getAll" }),
    ...mapActions("location", { getAllLocations: "getAll" }),
    ...mapActions("navigation", { changePage: "changePage" }),

    getFirstCompany(user) {
      return user.roles.find(function (role) {
        return role.role == "ROLE_COMPANY_ADMIN";
      });
    },
    buildChart(stats) {
      console.log(stats);
      //   this.labels = this.buildLabels(stats);
      //   let ctx = this.$refs.canvas;
      //   let config = this.buildConfig(this.labels);
      //   if (ctx && config) this.chart = new Chart(ctx, config);
    },
    changeCampaign(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value)
    },
    changeCompany(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value)
    },
    changeSede(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value)
    },
  },
};
</script>
<style scoped></style>
