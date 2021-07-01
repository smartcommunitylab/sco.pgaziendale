<template>
  <div>
    <div class="flex flex-col lg:flex-row">
      <div class="mx-2 my-2 flex flex-col lg:w-4/6 bg-white p-2">
          <div
            class="mx-2 my-2 flex flex-col bg-white p-2 text-primary rounded-xl border-2 h-full"
          >
            <ul class="list-reset flex">
              <li class="-mb-px mr-1">
                <a
                  class="bg-white inline-block py-2 px-4 font-semibold"
                  :class="{
                    'border-l border-t border-r rounded-t text-blue-dark':
                      tabWhatActive == 'draw',
                    'text-blue hover:text-blue-darker': tabWhatActive != 'draw',
                  }"
                  href="#"
                  @click="tabWhatActive = 'draw'"
                  >Grafico</a
                >
              </li>
              <li class="mr-1">
                <a
                  class="bg-white inline-block py-2 px-4 font-semibold"
                  :class="{
                    'border-l border-t border-r rounded-t ': tabWhatActive == 'table',
                    'text-blue hover:text-blue-darker': tabWhatActive != 'table',
                  }"
                  href="#"
                  @click="tabWhatActive = 'table'"
                  >Tabella</a
                >
              </li>
            </ul>
            <div v-if="tabWhatActive == 'draw'">
              <chart :selection="selection" />
            </div>
            <div v-if="tabWhatActive == 'table'">
              <generic-table
                class="text-center"
                :data.sync="dataStat"
                :columns.sync="gridColumns"
                :header.sync="headerColumns"
              >
              </generic-table>
            </div>
          </div>
      </div>

      <div
        class="mx-2 my-2 flex flex-col lg:w-2/6 bg-white p-2 text-primary rounded-xl border-2 h-full"
      >
        <ul class="list-reset flex border-b">
          <li class="-mb-px mr-1">
            <a
              class="bg-white inline-block py-2 px-4 font-semibold"
              :class="{
                'border-l border-t border-r rounded-t text-blue-dark':
                  tabActive == 'charts',
                'text-blue hover:text-blue-darker': tabActive != 'charts',
              }"
              href="#"
              @click="tabActive = 'charts'"
              >Visualizza</a
            >
          </li>
          <li class="mr-1">
            <a
              class="bg-white inline-block py-2 px-4 font-semibold"
              :class="{
                'border-l border-t border-r rounded-t ': tabActive == 'csv',
                'text-blue hover:text-blue-darker': tabActive != 'csv',
              }"
              href="#"
              @click="tabActive = 'csv'"
              >Esporta</a
            >
          </li>
        </ul>
        <div
          class="mx-2 my-2 flex flex-col text-white p-2 bg-primary rounded-xl border-2 h-full"
          v-if="tabActive == 'charts'">
          <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col">
            <button
              type="button"
              class="btn-close text-xs font-medium mt-2 mb-2 mx-2 inline-block px-6 py-2 leading-6 text-center text-primary transition bg-white rounded ripple uppercase hover:bg-primary_light hover:shadow-lg focus:outline-none"
              aria-label="Close modal"
              @click="livelloAgg = !livelloAgg"
            >
              Livello aggregazione
            </button>
            

            <transition name="slide-fade">
            <div v-if="livelloAgg">
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
              <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col text-white bg-primary"
            v-if="role == 'ROLE_ADMIN' && adminCompany == null">
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
              <template
                v-if="
                  actualCampaign && actualCampaign.item && actualCampaign.item.companies
                "
              >
                <option
                  v-for="company in actualCampaign.item.companies"
                  :key="company.id"
                  :value="company"
                >
                  {{ company.name }}
                </option>
              </template>
            </select>
          </div>
          <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col text-white bg-primary"
            v-if="
              role == 'ROLE_COMPANY_ADMIN' ||
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
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
              <option
                selected
                :value="adminCompany ? adminCompany.item : actualCompany.item"
              >
                {{ adminCompany ? adminCompany.item.name : actualCompany.item.name }}
              </option>
            </select>
          </div>
          <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
            v-if="
              role == 'ROLE_COMPANY_ADMIN' ||
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
            <label for="sub_select">Cosa?</label>
            <div class="flex flex-col items-center justify-center">
              <div class="flex flex-col">
                <label class="inline-flex items-center mt-3">
                  <input
                    v-if="allLocations && allLocations.items && allLocations.items.length > 0"
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value="sede"
                    v-model="what"
                    @change="changeWhat('sede')"
                  />
                  <input
                    v-else
                    :disabled="true"
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value=""
                    v-model="what"
                    @change="changeWhat('sede')"
                  />
                  <span class="ml-2 text-gray-700">Sedi</span>
                </label>
                <label class="inline-flex items-center mt-3">
                  <input
                    v-if="allEmployees && allEmployees.items && allEmployees.items.length > 0"
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value="dipendente"
                    v-model="what"
                    @change="changeWhat('dipendente')"
                  />
                  <input
                    v-else-if="allEmployees && allEmployees.items && allEmployees.items.length == 0"
                    :disabled="true"
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value=""
                    v-model="what"
                    @change="changeWhat('dipendente')"
                  />
                  <span class="ml-2 text-gray-700">Dipendenti</span>
                </label>
              </div>
            </div>
          </div>
          <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
            v-if="
              (role == 'ROLE_COMPANY_ADMIN' ||
                (role == 'ROLE_ADMIN' && adminCompany != null) ||
                (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)) &&
              what == 'sede'
            "
          >
            <label for="sub_select">Seleziona una sede</label>
            <select
              v-if="allLocations && allLocations.items && allLocations.items.length == 0"
              :disabled="true"
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="campaign"
              v-model="selectedSede"
              @change="changeSede($event)"
              required
            >
              <option disabled value="">Seleziona una sede</option>
              <template>
              <option
                :value="{id: 'all'}"
              > Tutte le sedi </option>
                <option
                  v-for="location in allLocations.items"
                  :value="location"
                  :key="location.id"
                >
                  {{ location.id }} {{ location.address }} {{ location.streetNumber }}
                </option>
              </template>
            </select>
            <select
              v-else
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="campaign"
              v-model="selectedSede"
              @change="changeSede($event)"
              required
            >
              <option disabled value="">Seleziona una sede</option>
              <template>
              <option
                :value="{id: 'all'}"
              > Tutte le sedi </option>
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
          <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
            v-if="
              allEmployees &&
              allEmployees.items &&
              (role == 'ROLE_COMPANY_ADMIN' ||
                (role == 'ROLE_ADMIN' && adminCompany != null) ||
                (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)) &&
              what == 'dipendente'
            "
          >
            <label for="sub_select">Seleziona un dipendente</label>
            <select
              v-if="allEmployees && allEmployees.items && allEmployees.items.length == 0 || !selectedCampaign"
              :disabled="true" 
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="employee"
              v-model="selectedEmployee"
              @change="changeEmployee($event)"
              required
            >
              <option disabled value="">Seleziona un dipendente</option>
               <option
                :value="{id: 'all'}"
                
              > Tutti i dipendenti </option>
              <option
                v-for="employee in allEmployees.items"
                :value="employee"
                :key="employee.id"
                :disabled="!isSubbscribed(employee)"
              >
                {{ employee.name }} {{ employee.surname }}
                <span v-if="!isSubbscribed(employee)">Non iscritto alla campagna</span>
              </option>
            </select>
            <select
              v-else
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="employee"
              v-model="selectedEmployee"
              @change="changeEmployee($event)"
              required
              :disabled="!selectedCampaign"
            >
              <option disabled value="">Seleziona un dipendente</option>
               <option
                :value="{id: 'all'}"
                
              > Tutti i dipendenti </option>
              <option
                v-for="employee in allEmployees.items"
                :value="employee"
                :key="employee.id"
                :disabled="!isSubbscribed(employee)"
              >
                {{ employee.name }} {{ employee.surname }}
                <span v-if="!isSubbscribed(employee)">Non iscritto alla campagna</span>
              </option>
            </select>
          </div>
          <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col">
            <label for="sub_select">Cosa vuoi visualizzare</label>
            <select
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="campaign"
              v-model="selectedType"
              @change="changeType($event)"
              required
            >
              <option disabled value="">Seleziona un tipo di dato</option>
              <option value="km_valid">Chilometri validi</option>
              <option value="km_true">Chilometri effettivi</option>
              <option value="co2saved">CO2 salvata</option>
              <option value="trackCount">Numero di viaggi</option>
            </select>
          </div>          
            </div>    
            </transition>      
          </div>
          <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary"
          ></div>
          <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col">
            <button
              type="button"
              class="btn-close text-xs font-medium mt-2 mb-2 mx-2 inline-block px-6 py-2 leading-6 text-center text-primary transition bg-white rounded ripple uppercase hover:bg-primary_light hover:shadow-lg focus:outline-none"
              aria-label="Close modal"
              @click="unitaTemp = !unitaTemp"
            >
              Unità temporale
            </button>
            
            <transition name="slide-fade">
            <div v-if="unitaTemp">
            <label for="sub_select">Visualizzazione</label>
            <select
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="campaign"
            >
              <option value="" class="text-gray">Seleziona uno o tutti i mesi</option>
              <option
                type="radio"
                class="form-radio h-5 w-5 text-primary"
                :value="month"
                @change="changeSpan('month')"
              >
                <span class="ml-2 text-gray-700">Mese</span>
              </option>
              <option
                type="radio"
                class="form-radio h-5 w-5 text-primary"
                :value="total"
                @change="changeSpan('total')"
              >
                <span class="ml-2 text-gray-700">Totale</span>
              </option>
            </select>
            <!--<label class="inline-flex items-center mt-3">
                  <input
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value="month"
                    v-model="span"
                    @change="changeSpan('month')"
                  /><span class="ml-2 text-gray-700">Mese</span>
                </label>
                <label class="inline-flex items-center mt-3">
                  <input
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value="total"
                    v-model="span"
                    @change="changeSpan('total')"
                  /><span class="ml-2 text-gray-700">Totale</span>
                </label>-->
          </div>
          </transition>
          <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
            v-if="span == 'month'"
          >
            <label for="sub_select">Seleziona un mese</label>
            <select
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="employee"
              v-model="selectedMonth"
              @change="changeMonth($event)"
              required
              :disabled="!selectedCampaign"
            >
              <option disabled value="">Seleziona un mese</option>
              <option v-for="month in months" :value="month" :key="month.id">
                {{ month.name }}
              </option>
            </select>
          </div>
          </div>
            
          <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary"
          ></div>
          <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
            v-if="means">
          <button
              type="button"
              class="btn-close text-xs font-medium mt-2 mb-2 mx-2 inline-block px-6 py-2 leading-6 text-center text-primary transition bg-white rounded ripple uppercase hover:bg-primary_light hover:shadow-lg focus:outline-none"
              aria-label="Close modal"
              @click="colonneDati = !colonneDati"
              :disabled="!selectedCampaign"              
            >
              Colonne dati
            </button>
            
            <transition name="slide-fade">
            <div v-if="colonneDati">
              <label for="sub_select">Seleziona un mezzo</label>
              <select
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="mean"
              v-model="selectedMean"
              @change="changeMean($event)"
              required
              :disabled="!selectedCampaign"
              >
              <option disabled value="">Seleziona un mezzo</option>
              <option v-for="mean in means" :value="mean" :key="mean.value">
                {{ mean.text }}
              </option>
              
            </select>
          <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
            v-if="means"
          >
            <label for="sub_select">Raggruppa per</label>
            <select
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="employee"
              v-model="selectedGroupBy"
              @change="changeGroupBy($event)"
              required
              :disabled="!selectedCampaign"
            >
              <option disabled value="">Seleziona un raggruppamento</option>
              <option value="month">Mese</option>
              <option value="day">Giorno</option>
            </select>
          </div>
          </div>
          </transition>
          </div>
          <div class="">
            <div class="selection-invalid" v-if="!selectionStatIsValid()">
              Selezionare i parametri corretti
            </div>
            <button
              type="button"
              class="btn-stat"
              @click="showStat"
              aria-label="Close modal"
              :disabled="!selectionStatIsValid()"
            >
              Mostra statistica
            </button>
          </div>
        </div>
        <div
          class="mx-2 my-2 flex flex-col text-white p-2 bg-primary rounded-xl border-2 h-full"
          v-if="tabActive == 'csv'"
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
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col text-white bg-primary"
            v-if="
              role == 'ROLE_COMPANY_ADMIN' ||
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
            <label for="sub_select"
              >Azienda selezionata:
              {{ adminCompany ? adminCompany.item.name : actualCompany.item.name }}</label
            >
          </div>
          <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
            v-if="
              role == 'ROLE_COMPANY_ADMIN' ||
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
            <label for="sub_select">Cosa?</label>
            <div class="flex flex-col items-center justify-center">
              <div class="flex flex-col">
                <label class="inline-flex items-center mt-3">
                  <input
                    v-if="allLocations && allLocations.items && allLocations.items.length > 0"
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value="sede"
                    v-model="what"
                    @change="changeWhat('sede')"
                  />
                  <input
                    v-else
                    :disabled="true"
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value=""
                    v-model="what"
                    @change="changeWhat('sede')"
                  />
                  <span class="ml-2 text-gray-700">Sedi</span>
                </label>

                <label class="inline-flex items-center mt-3">
                  <input
                    v-if="allEmployees && allEmployees.items && allEmployees.items.length > 0"
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value="dipendente"
                    v-model="what"
                    @change="changeWhat('dipendente')"
                  />
                  <input
                    v-else
                    :disabled="true"
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value=""
                    v-model="what"
                    @change="changeWhat('dipendente')"
                  />
                  <span class="ml-2 text-gray-700">Dipendenti</span>
                </label>
              </div>
            </div>
          </div>

          <div class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col">
            <label for="sub_select">Visualizzazione</label>
            <div class="flex flex-col items-center justify-center">
              <div class="flex flex-col">
                <label class="inline-flex items-center mt-3">
                  <input
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value="month"
                    v-model="span"
                    @change="changeSpan('month')"
                  /><span class="ml-2 text-gray-700">Mese</span>
                </label>

                <label class="inline-flex items-center mt-3">
                  <input
                    type="radio"
                    class="form-radio h-5 w-5 text-primary"
                    value="total"
                    v-model="span"
                    @change="changeSpan('total')"
                  /><span class="ml-2 text-gray-700">Totale</span>
                </label>
              </div>
            </div>
          </div>
          <div
            class="flex flex-col md:flex-row mt-3 justify-stretch lg:flex-col"
            v-if="span == 'month'"
          >
            <label for="sub_select">Seleziona un mese</label>
            <select
              class="focus:border-blue-600 border-2 p-2 mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none text-primary bg-white"
              name="sub_select"
              id="employee"
              v-model="selectedMonth"
              @change="changeMonth($event)"
              required
              :disabled="!selectedCampaign"
            >
              <option disabled value="">Seleziona un mese</option>
              <option v-for="month in months" :value="month" :key="month.id">
                {{ month.name }}
              </option>
            </select>
          </div>
          <div class="selection-invalid" v-if="!selectionCsvIsValid()">
            Selezionare i parametri corretti
          </div>
          <button
            type="button"
            class="btn-stat"
            @click="exportCsv"
            aria-label="Close modal"
            :disabled="!selectionCsvIsValid()"
          >
            Esporta CSV
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from "moment";
import { mapState, mapActions } from "vuex";
import { campaignService } from "../../services/campaign.services";
import Chart from "./Chart.vue";
import GenericTable from "@/components/GenericTable.vue";
export default {
  components: {
    Chart,
    GenericTable,
  },
  data() {
    return {
      tabWhatActive: "table",
      tabActive: "charts",
      checkin: "",
      span: "total",
      what: "",
      selectedCompany: "",
      selectedCampaign: "",
      selectedEmployee: "",
      selectedSede: "",
      selectedMonth: "",
      selectedMean: "",
      selectedType: "",
      selectedGroupBy: "",
      endMonthValue: "",
      statData: [],
      months: [],
      means: [],
      selection: {},
      gridColumns: [],
      headerColumns: [],
      dataStat: [],
      livelloAgg: false,
      unitaTemp:false,
      colonneDati:false,

    };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
    ...mapState("campaign", ["allCampaigns", "actualCampaign"]),
    ...mapState("company", ["allCompanies", "adminCompany", "actualCompany"]),
    ...mapState("location", ["allLocations"]),
    ...mapState("employee", ["allEmployees"]),
    ...mapState("stat", ["stat","configuration"]),
  },
  created() {
    //default company if AA
    //this.selectedCompany = null;
    if (this.role == "ROLE_ADMIN" && this.adminCompany == null) {
      //get all campaigns
      this.getAllCampaigns();
    } else if (
      this.role == "ROLE_COMPANY_ADMIN" ||
      (this.role == "ROLE_ADMIN" && this.adminCompany != null) ||
      (this.role == "ROLE_MOBILITY_MANAGER" && this.actualCompany != null)
    ) {
      this.selectedCompany = this.adminCompany
        ? this.adminCompany.item
        : this.actualCompany.item;
      this.getAllCampaigns(this.selectedCompany.id);
      this.what = "sede";
    }
    if (this.selectedCompany) {
      this.getAllLocations(this.selectedCompany.id);
      this.getAllEmployees(this.selectedCompany.id);
    }
    this.showStatOnLoad();
  },
  mounted() {
    this.changePage({ title: "Statistiche", route: "/stats" });
    this.resetStat();
  },
  watch: {
    stat() {
      console.log(this.stat);
      if (this.stat && this.stat.items) this.buildTable();
    },
  },
  methods: {
    ...mapActions("campaign", {
      getAllCampaigns: "getAll",
      getAllCompaniesOfCampaignCall: "getAllCompaniesOfCampaign",
    }),
    ...mapActions("company", { getAllCompanies: "getAll" }),
    ...mapActions("location", { getAllLocations: "getAllLocations" }),
    ...mapActions("navigation", { changePage: "changePage" }),
    ...mapActions("employee", { getAllEmployees: "getAll" }),
    ...mapActions("stat", {
      getCampaignCsv: "getCampaignCsv",
      getCompanyCsv: "getCompanyCsv",
      getLocationCsv: "getLocationCsv",
      getCampaignStat: "getCampaignStat",
      getCompanyStat: "getCompanyStat",
      getEmployeeStat: "getEmployeeStat",
      getLocationStat: "getLocationStat",
      resetStat: "resetStat",
    }),
    selectionStatIsValid() {
      if (!this.selectedCampaign) return false;
      if (!this.selectedMean) return false;
      if (!this.selectedGroupBy) return false;
      if (!this.selectedType) return false;
      if (this.what === "dipendente" && !this.selectedEmployee) return false;
      if (this.what === "sede" && !this.selectedSede) return false;
      if (this.span === "month" && !this.selectedMonth) return false;
      return true;
    },
    selectionCsvIsValid() {
      if (!this.selectedCampaign) return false;
      if (this.span === "month" && !this.selectedMonth) return false;
      return true;
    },
    isSubbscribed(user) {
      if (user.campaigns && user.campaigns.includes(this.selectedCampaign.id))
        return true;
      return false;
    },
    getFirstCompany(user) {
      return user.roles.find(function (role) {
        return role.role == "ROLE_COMPANY_ADMIN";
      });
    },
    buildChart(stats) {
      console.log(stats);
      this.labels = this.buildLabels(stats);
      let ctx = this.$refs.canvas;
      let config = this.buildConfig(this.labels);
      if (ctx && config) this.chart = new Chart(ctx, config);
    },
    changeCampaign(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value);
      if (this.selectedCampaign)
        this.getAllCompaniesOfCampaignCall(this.selectedCampaign);
      this.loadMeans(this.selectedCampaign);
      this.changeSpan(this.span);
    },
    changeCompany(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value);
      if (this.selectedCompany) this.getAllEmployees(this.selectedCompany.id);
    },
    changeSede(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value);
    },
    changeType(event) {
      console.log(event);
      console.log(event.target.value);
    },
    changeEmployee(event) {
      //get new data for the new campaign
      console.log(event);
      console.log(event.target.value);
    },
    changeSpan(span) {
      if (span == "month") {
        if (this.selectedCampaign)
          this.months = campaignService.getMonthsForCampaign(this.selectedCampaign);
      } else {
        // reset selectedmonth
        this.selectedMonth = {};
        this.endMonthValue = "";
      }
    },
    changeMonth(event) {
      console.log(event);
      if (this.selectedMonth.value)
        this.endMonthValue = moment(this.selectedMonth.value, "YYYY-MM-DD")
          .add(1, "months")
          .format("YYYY-MM-DD");
    },
    changeMean(event) {
      console.log(event);
    },
    changeGroupBy(event) {
      console.log(event);
      console.log(event.target.value);
    },
    changeWhat(what) {
      this.what = what;
    },
    loadMeans(campaign) {
      this.means = campaignService.getMeansForCampaign(campaign);
    },
    buildHeaderBodyTable() {
      this.headerColumns=[];
      this.gridColumns=[];
      //check selection for header
      if (this.selectedGroupBy == "month") {
        this.headerColumns.push("Mese");
        this.gridColumns.push("month");
      } else {
        this.headerColumns.push("Giorno");
        this.gridColumns.push("date");
      }
      switch (this.selection.selectedType) {
        case "km_valid":
          this.headerColumns.push("Distanza");
          this.gridColumns.push("distance");
          break;
        case "km_true":
          this.headerColumns.push("Distanza");
          this.gridColumns.push("distance");
          break;
        case "co2saved":
          this.headerColumns.push("Grammi");
          this.gridColumns.push("co2saved");
          break;
        case "trackCount":
          this.headerColumns.push("Viaggi");
          this.gridColumns.push("trackCount");
          break;
        default:
          break;
      }
    },
    /* buildHeaderBodyTable1() {
      this.headerColumns=[];
      this.gridColumns=[];
      //check selection for header
      if (this.configuration.titolo == "Km fatti e utili") {
        this.headerColumns.push("Dipendenti");
        this.gridColumns.push("dipendente");//?????
        this.headerColumns.push("KM percorsi");
        this.gridColumns.push("distance");
        this.headerColumns.push("KM utili");
        this.gridColumns.push("distance");
      }
      else if (this.configuration.titolo == "Partecipazione dipendenti") {
        this.headerColumns.push("Sedi");//?????
        this.gridColumns.push("sede");
        this.headerColumns.push("Dipendenti registrati");
        this.gridColumns.push("dipendente");
        this.headerColumns.push("Dipendenti iscritti");
        this.gridColumns.push("dipendente");
        this.headerColumns.push("Dipendenti attivi");
        this.gridColumns.push("dipendente");
      }
      else if(this.configuration.titolo == "Impatto ambientale"){
        this.headerColumns.push("Azienda");//?????
        this.gridColumns.push("company");
        this.headerColumns.push("Totale viaggi");
        this.gridColumns.push("trackCount");
        this.headerColumns.push("KM percorsi");
        this.gridColumns.push("distance");
        this.headerColumns.push("Grammi");
        this.gridColumns.push("co2saved");
      }
      this.buildStat();
    }, */
    buildTable() {
      this.buildHeaderBodyTable();
      this.buildStat();
    },
    format(string, span) {
      if (span == "date") return moment(string).format("DD-MM-YYYY");
      if (span == "month") return moment(string).format("MM-YYYY");
    },
    buildStat() {
      this.dataStat=[];
      //simplify dataStat
      for (var i = 0; i < this.stat.items.length; i++) {
        //take the distance of the mean
        this.dataStat.push({co2saved:this.stat.items[i].co2saved,trackCount:this.stat.items[i].trackCount,date:this.format(this.stat.items[i].date,"date"),month:this.format(this.stat.items[i].month,"month"),distance:this.stat.items[i].distances[this.selection.mean]/1000})
      }
    },
    showStat() {
      console.log("getStat and show values");
      //check values and choose the right call
      this.resetStat();
      if (this.role == "ROLE_ADMIN" && this.adminCompany == null) {
        if (this.adminCompany == null && !this.selectedCompany) {
          this.selection = {
            type: "getCampaignStat",
            campaignId: this.selectedCampaign.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            mean: this.selectedMean.value,
            groupBy: this.selectedGroupBy,
            selectedType: this.selectedType,
          };
          this.getCampaignStat({
            campaignId: this.selectedCampaign.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            groupBy: this.selectedGroupBy,
            noLimits: this.selectedType == "km_true" ? true : false,
          });
          return;
        } else {
          this.selection = {
            type: "getCompanyStat",
            campaignId: this.selectedCampaign.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            mean: this.selectedMean.value,
            groupBy: this.selectedGroupBy,
            selectedType: this.selectedType,
          };
         this.getCompanyStat({
            campaignId: this.selectedCampaign.id,
            companyId: this.selectedCompany.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            groupBy: this.selectedGroupBy,
            noLimits: this.selectedType == "km_true" ? true : false,
          });
          return;
        }
      } else {
        //AA
        if (this.what === "dipendente" && this.selectedEmployee.id!="all") {
          this.selection = {
            type: "getEmployeeStat",
            campaignId: this.selectedCampaign.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            mean: this.selectedMean.value,
            groupBy: this.selectedGroupBy,
            selectedType: this.selectedType,
          };
         this.getEmployeeStat({
            campaignId: this.selectedCampaign.id,
            employeeId: this.selectedEmployee.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            groupBy: this.selectedGroupBy,
            noLimits: this.selectedType == "km_true" ? true : false,
          });
          return;
        } else {
          if  (this.selectedSede && this.selectedSede.id!="all"){
          this.selection = {
            type: "getLocationStat",
            campaignId: this.selectedCampaign.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            mean: this.selectedMean.value,
            groupBy: this.selectedGroupBy,
            selectedType: this.selectedType,
          };
         this.getLocationStat({
            campaignId: this.selectedCampaign.id,
            companyId: this.selectedCompany.id,
            locationId: this.selectedSede.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            groupBy: this.selectedGroupBy,
            noLimits: this.selectedType == "km_true" ? true : false,
          });
          return;
          }
        }
        if (this.selectedEmployee.id=="all" || this.selectedSede.id=="all"){
          this.selection = {
            type: "getCompanyStat",
            campaignId: this.selectedCampaign.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            mean: this.selectedMean.value,
            groupBy: this.selectedGroupBy,
            selectedType: this.selectedType,
          };
          this.getCompanyStat({
            campaignId: this.selectedCampaign.id,
            companyId: this.selectedCompany.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
            groupBy: this.selectedGroupBy,
            noLimits: this.selectedType == "km_true" ? true : false,
          });
        return;
        }
      }
    },
    showStatOnLoad() {
      console.log("getStat and show values");
      //check values and choose the right call
      this.resetStat();
    },
    exportCsv() {
      console.log("export csv");
      //check values and choose the right call
      if (
        (this.role == "ROLE_ADMIN" && this.adminCompany == null) ||
        !this.selectedCompany
      ) {
        //get stat for companies
        this.getCampaignCsv({
          campaignId: this.selectedCampaign.id,
          from: this.selectedMonth ? this.selectedMonth.value : null,
          to: this.selectedMonth ? this.endMonthValue : null,
        });
      } else {
        //get stat for employee
        if (this.what == "dipendente")
          this.getCompanyCsv({
            campaignId: this.selectedCampaign.id,
            companyId: this.selectedCompany.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
          });
        //get stat for locations
        else
          this.getLocationCsv({
            campaignId: this.selectedCampaign.id,
            companyId: this.selectedCompany.id,
            from: this.selectedMonth ? this.selectedMonth.value : null,
            to: this.selectedMonth ? this.endMonthValue : null,
          });
      }
    },
  },
};
</script>
<style scoped>
.btn-stat {
  border: none;
  font-size: 20px;
  padding: 20px;
  cursor: pointer;
  font-weight: bold;
  color: white;
  background: transparent;
  text-align: center;
  width: 100%;
  border-radius: 8px;
  border: 1px solid white;
  margin-top: 8px;
}
.selection-invalid {
  height: 50px;
  color: #d8000c;
  background-color: #ffd2d2;
  border-radius: 8px;
  line-height: 50px;
  text-align: center;
}
button:disabled,
button[disabled] {
  border: 1px solid #999999;
  /* background-color: #cccccc; */
  color: #cccccc;
}
.slide-fade-enter-active {
  transition: all .5s ease;
}
.slide-fade-leave-active {
  transition: all .3s ease;
}
.slide-fade-enter, .slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}
</style>