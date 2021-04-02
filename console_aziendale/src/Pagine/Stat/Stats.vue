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
          class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col text-white bg-primary "
          v-if="role=='ROLE_ADMIN' && adminCompany==null"
        >
          <label for="sub_select">Seleziona una azienda</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
            name="sub_select"
            id="company"
            v-model="selectedCompany"
            @change="changeCompany($event)"
            required
          >
            <option disabled value="">Seleziona una azienda</option>
            <template v-if="actualCampaign && actualCampaign.item && actualCampaign.item.companies">
            <option
              v-for="company in actualCampaign.item.companies"
              :value="company"
              :key="company.id"
            >
              {{ company.name }}
            </option>
            </template>
          </select>
        </div>
          <div
          class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col text-white bg-primary "
          v-if="role=='ROLE_COMPANY_ADMIN'||(role=='ROLE_ADMIN'&&adminCompany!=null)||(role=='ROLE_MOBILITY_MANAGER'&& actualCompany!=null)"
        >
          <label for="sub_select">Seleziona una azienda</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
            name="sub_select"
            id="company"
            v-model="selectedCompany"
            @change="changeCompany($event)"
            required
          >
            <option selected value="">{{adminCompany?adminCompany.item.name:actualCompany.item.name}}</option>
            
          </select>
        </div>
                 <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col" v-if="role!='ROLE_ADMIN'" >
          <label for="sub_select">Cosa?</label>
              <div class="flex flex-col items-center justify-center ">
        <div class="flex flex-col">
            <label class="inline-flex items-center mt-3">
                <input type="radio" class="form-radio h-5 w-5 text-primary" value="sede" v-model="what" @change="changeWhat('sede')"><span class="ml-2 text-gray-700">Sede</span>
            </label>

            <label class="inline-flex items-center mt-3">
                <input type="radio" class="form-radio h-5 w-5 text-primary" value="dipendente" v-model="what" @change="changeWhat('dipendente')"><span class="ml-2 text-gray-700">Dipendente</span>
            </label>
        </div>
        
              </div>
         </div>
        <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col" v-if="role=='ROLE_COMPANY_ADMIN' && what=='sede'">
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
             <template v-if="allLocations">

            <option
              v-for="location in allLocations.items"
              :value="location"
              :key="location.id"
            >
             {{ location.id }} {{ location.address }} {{ location.streetNumber }}
            </option>
             </template>
          </select>
        </div>
        <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"  v-if="allEmployees &&allEmployees.items && role!='ROLE_ADMIN' && what=='dipendente'">
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
                <input type="radio" class="form-radio h-5 w-5 text-primary" value="month" v-model="span" @change="changeSpan('month')"><span class="ml-2 text-gray-700">Mese</span>
            </label>

            <label class="inline-flex items-center mt-3">
                <input type="radio" class="form-radio h-5 w-5 text-primary" value="total" v-model="span" @change="changeSpan('total')"><span class="ml-2 text-gray-700">Totale</span>
            </label>
        </div>
        
              </div>
         </div>
                 <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"  v-if="span=='month'">
          <label for="sub_select">Seleziona un mese</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
            name="sub_select"
            id="employee"
            v-model="selectedMonth"
            @change="changeMonth($event)"
            required
          >
            <option disabled value="">Seleziona un mese</option>
            <option
              v-for="month in months"
              :value="month"
              :key="month.id"
            >
              {{ month.name }}
            </option>
          </select>
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
        <div class="flex-row">
              <button
          type="button"
          class="btn-close flex"
          @click="showStat"
          aria-label="Close modal"
        >
          Mostra statistica
        </button>
                      <button
          type="button"
          class="btn-close flex"
          @click="exportCsv"
          aria-label="Close modal"
        >
          Esporta CSV
        </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import {campaignService} from "../../services/campaign.services";

export default {
  // components: {
  //   VueTailwindPicker,
  // },
  data() {
    return {
      checkin: "",
      span:"total",
      what:"sede",
      selectedCompany:"",
      selectedCampaign:"",
      selectedEmployee:"",
      selectedMonth:{},

      // azienda:"",
      // sede:"",
      // dal:"",
      // al:"",
      months:[]
    };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
    ...mapState("campaign", ["allCampaigns","actualCampaign"]),
    ...mapState("company", ["allCompanies","adminCompany","actualCompany"]),
    ...mapState("location", ["allLocations"]),
    ...mapState("employee", ["allEmployees"]),
  },
  created() {
    //default company if AA
    this.selectedCompany = null;
    if (this.role=='ROLE_ADMIN'&&this.adminCompany==null) {
      //get all campaigns
      this.getAllCampaigns();
    } else
    if (this.role=='ROLE_COMPANY_ADMIN'||(this.role=='ROLE_ADMIN'&&this.adminCompany!=null)||(this.role=='ROLE_MOBILITY_MANAGER'&&this.actualCompany!=null)) {
      // var company = this.getFirstCompany(this.user);
      // if (company) this.selectedCompany = company;
      //       this.getAllCampaigns(this.selectedCompany);
      this.selectedCompany=(this.adminCompany? this.adminCompany.item.id:this.actualCompany.item.id)
      this.getAllCampaigns(this.selectedCompany);
    }
    // if (this.role == "ROLE_COMPANY_ADMIN") {
    //   var company = this.getFirstCompany(this.user);
    //   if (company) this.azienda = company.companyId;
    // }
    // //get all the campaigns, if azienda is set, filter by it
    // this.getAllCampaigns(this.azienda);

    // if (this.role == "ROLE_ADMIN") {
    //   this.getAllCompanies();
    //   }
    if (this.selectedCompany) {
      this.getAllLocations(this.selectedCompany);
      this.getAllEmployees(this.selectedCompany);
    }
  },
  mounted() {
    this.changePage({title: 'Statistiche',
                route: '/stats'})
    this.buildChart;
  },
  methods: {
    ...mapActions("campaign", { getAllCampaigns: "getAll",getAllCompaniesOfCampaignCall: "getAllCompaniesOfCampaign"}),
    ...mapActions("company", { getAllCompanies: "getAll" }),
    ...mapActions("location", { getAllLocations: "getAllLocations" }),
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
      if (this.selectedCampaign)
      this.getAllCompaniesOfCampaignCall(this.selectedCampaign); 
    },
    changeCompany(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value);
      if (this.selectedCompany)
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
    changeSpan(span) {
      if (span=='month'){
        if (this.selectedCampaign)
      this.months=campaignService.getMonthsForCampaign(this.selectedCampaign);
      }
    },
    showStat(){
      console.log("getStat and show values");
    },
    exportCsv(){
      console.log("export csv");
    }
  },
};
</script>
<style scoped></style>
