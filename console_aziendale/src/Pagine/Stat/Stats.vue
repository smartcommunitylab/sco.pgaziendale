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
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
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
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
            name="sub_select"
            id="campaign"
            v-model="selectedCompany"
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
        <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col" v-if="role=='ROLE_COMPANY_ADMIN'">
          <label for="sub_select">Seleziona una sede</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
            name="sub_select"
            id="campaign"
            v-model="selectedSede"
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
        <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"  v-if="allEmployees &&allEmployees.items">
          <label for="sub_select">Seleziona un dipendente</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
            name="sub_select"
            id="employee"
            v-model="selectedEmployee"
            @change="changeEmployee($event)"
            required
          >
            <option disabled value="">Seleziona un dipendente</option>
            <option
              v-for="employee in allEmployees.items"
              :value="employee"
              :key="employee.id"
            >
              {{ employee.name }} {{ employee.surname }}
            </option>
          </select>
        </div>
         <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col" >
          <label for="sub_select">Visualizzazione</label>
              <div class="flex flex-col items-center justify-center ">
        <div class="flex flex-col">
            <label class="inline-flex items-center mt-3">
                <input type="radio" class="form-radio h-5 w-5 text-primary" value="month" v-model="span"><span class="ml-2 text-gray-700">Mese</span>
            </label>

            <label class="inline-flex items-center mt-3">
                <input type="radio" class="form-radio h-5 w-5 text-primary" value="total" v-model="span"><span class="ml-2 text-gray-700">Totale</span>
            </label>
        </div>
        
              </div>
         </div>
        <!-- <div class="flex flex-col mt-3 justify-stretch">
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
        </div> -->
              <button
          type="button"
          class="btn-close"
          @click="showStat"
          aria-label="Close modal"
        >
          Mostra statistica
        </button>
      </div>

    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
// import VueTailwindPicker from "vue-tailwind-picker";
export default {
  // components: {
  //   VueTailwindPicker,
  // },
  data() {
    return {
      checkin: "",
      span:"month",
      selectedCompany:"",
      selectedCampaign:"",
      selectedEmployee:"",
      azienda:"",
      sede:"",
      dal:"",
      al:""
    };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
    ...mapState("campaign", ["allCampaigns"]),
    ...mapState("company", ["allCompanies"]),
    ...mapState("location", ["allLocations"]),
    ...mapState("employee", ["allEmployees"]),
  },
  created() {
    //default company if AA
    this.azienda = null;
    if (this.role == "ROLE_COMPANY_ADMIN") {
      var company = this.getFirstCompany(this.user);
      if (company) this.azienda = company.companyId;
    }
    //get all the campaigns, if azienda is set, filter by it
    this.getAllCampaigns(this.azienda);

    if (this.role == "ROLE_ADMIN") this.getAllCompanies();
    if (this.azienda) {
      this.getAllLocations(this.azienda);
      this.getAllEmployees(this.azienda);
    }
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
    ...mapActions("employee", { getAllEmployees: "getAll" }),
    ...mapActions("stat",{getStat:"getStat"}),
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
      console.log(event.target.value);
      this.getAllEmployees(this.selectedCompany.id);
    },
    changeSede(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value)
    },
        changeEmployee(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value)
    },
    showStat(){
      console.log("getStat and show values");
    }
  },
};
</script>
<style scoped></style>
