<template>
  <div style="margin-right: 56px">
  <v-navigation-drawer
      absolute
      permanent
      right
      v-model="drawer"
      :mini-variant.sync="mini"
      width="384"
      >
      <v-list-item class="ml-2 px-2">
        <v-list-item-icon>
            <v-icon>mdi-filter-cog</v-icon>
          </v-list-item-icon>

        <v-list-item-title>Impostazioni</v-list-item-title>

        <v-btn
          icon
          @click.stop="mini = !mini"
        >
          <v-icon>mdi-chevron-right</v-icon>
        </v-btn>
      </v-list-item>

      <v-divider></v-divider>
      <div class="pa-2">
      <v-expansion-panels v-show="!mini" v-if="localSelection && view" 

        v-model="panel"
        multiple
      >
        <v-expansion-panel>
          <v-expansion-panel-header>Livello aggregazione</v-expansion-panel-header>
          <v-expansion-panel-content>
                <v-autocomplete
                  label="Livello aggregazione"
                  name="livelloaggregazione"
                  id="livelloaggregazione"
                  v-model="localSelection.dataLevel"
                  :items="view.dataLevel"
                  item-text="label"
                  return-object
                  outlined
                  @change="resetPunctualAggregation"
                ></v-autocomplete>

                <div
                class="pl-5"
                v-if="localSelection && localSelection.puntualAggregationSelected"
              >
                <p class="text-subtitle-1">Filtri</p>
                <v-radio-group
                  v-model="puntualAggregationSelected"
                  @change="updatePuntualAggregationChange"
                >
                  <v-radio
                    v-for="(agg, index) in localSelection.dataLevel.puntualAggregation"
                    :key="index"
                    :label="agg.label"
                    :value="agg.value"
                  ></v-radio>
                </v-radio-group>
                <div
                  v-if="
                    localSelection.puntualAggregationSelected &&
                    localSelection.puntualAggregationSelected.value != 'NONE' &&
                    localSelection.itemsAggreation != null
                  "
                >
                  <v-autocomplete
                    label="Selezione"
                    name="typeData"
                    id="typeData"
                    v-model="localSelection.puntualAggregationItems"
                    :items="localSelection.itemsAggreation"
                    :item-text="getItemText"
                    return-object
                    outlined
                    multiple
                  ></v-autocomplete>
                </div>
              </div>
          </v-expansion-panel-content>
        </v-expansion-panel>
        <v-expansion-panel>
          <v-expansion-panel-header>Aggregazione temporale</v-expansion-panel-header>
          <v-expansion-panel-content>
                <v-autocomplete
                  label="Aggregazione temporale"
                  placeholder="Aggregazione temporale"
                  name="unitaTemporale"
                  id="unitaTemporale"
                  v-model="localSelection.timeUnit"
                  :items="view.timeUnit"
                  item-text="label"
                  return-object
                  outlined
                ></v-autocomplete>

                <p class="text-subtitle-1">Periodo di tempo</p>
                <v-radio-group v-model="timePeriod" @change="updateTimePeriod">
                  <v-radio
                    v-for="(period, index) in view.timePeriod"
                    :key="index"
                    :label="period.label"
                    :value="period.value"
                  ></v-radio>
                </v-radio-group>
                <div
                  v-if="
                    localSelection.timePeriod &&
                    localSelection.timePeriod.value == 'SPECIFIC'
                  "
                >
                  <v-menu
                    v-model="showPickerFrom"
                    :close-on-content-click="false"
                    :nudge-right="40"
                    transition="scale-transition"
                    offset-y
                    min-width="auto"
                  >
                    <template v-slot:activator="{ on }">
                      <v-text-field
                        v-model="localSelection.selectedDateFrom"
                        label="Seleziona la data di inizio"
                        hint="YYYY/MM/DD/"
                        persistent-hint
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker
                      v-model="localSelection.selectedDateFrom"
                      @input="showPickerFrom = false"
                    ></v-date-picker>
                  </v-menu>

                  <v-menu
                    v-model="showPickerTo"
                    :close-on-content-click="false"
                    :nudge-right="40"
                    transition="scale-transition"
                    offset-y
                    min-width="auto"
                  >
                    <template v-slot:activator="{ on }">
                      <v-text-field
                        v-model="localSelection.selectedDateTo"
                        label="Seleziona la data di fine"
                        hint="YYYY/MM/DD"
                        persistent-hint
                        v-on="on"
                      ></v-text-field>
                    </template>
                    <v-date-picker
                      v-model="localSelection.selectedDateTo"
                      @input="showPickerTo = false"
                    ></v-date-picker>
                  </v-menu>
                </div>
          </v-expansion-panel-content>
        </v-expansion-panel>
        <v-expansion-panel>
          <v-expansion-panel-header>Mezzi</v-expansion-panel-header>
          <v-expansion-panel-content>
              <v-autocomplete
                    label="Selezione"
                    name="means"
                    id="means"
                    v-model="localSelection.means"
                    :items="meansList"
                    :item-text="getMeanText"
                    outlined
                    multiple
                  ></v-autocomplete>

                  <p class="text-subtitle-1">Direzione</p>
                <v-radio-group
                  v-model="localSelection.direction"
                >
                  <v-radio label="Tutti" value="all"></v-radio>
                  <v-radio label="Verso sede" value="wayThere"></v-radio>
                  <v-radio label="Da sede" value="wayBack"></v-radio>
                </v-radio-group>
          </v-expansion-panel-content>
        </v-expansion-panel>
        <v-expansion-panel>
          <v-expansion-panel-header>Colonne dati</v-expansion-panel-header>
          <v-expansion-panel-content>
                <v-checkbox v-for="dc in view.dataColumns" :key="dc.value"
                v-model="localSelection.dataColumns" :value="dc"
                :label="dc.label" hide-details
              ></v-checkbox>
              <br>
              <v-switch
                v-model="localSelection.groupByMean"
                label="Dividere per mezzo"
                hide-details
                inset
              ></v-switch>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>

      <v-divider v-show="!mini"></v-divider>
      <v-row v-show="!mini" class="pa-2 mb-4" >
        <v-col cols="6" class="text-center">
          <v-btn
            color="blue darken-1"
            text
            @click="resetFilterAndRefreshStat()"
          >
            Reimposta
          </v-btn>
        </v-col>
        <v-col cols="6" class="text-center">
          <v-btn
            color="blue darken-1"
            text
            @click="saveFiltersAndRefreshStat()"
          >
            Applica
          </v-btn>
        </v-col>
      </v-row> 
      </div>   
  </v-navigation-drawer>
  <v-row>
    <v-col cols="12">
      <v-card>
        <v-row>
          <v-col cols="6">
            <v-tabs
              v-model="tab"
              align-with-title
              v-if="configurations && configurations.items"
            >
              <v-tabs-slider color="primary"></v-tabs-slider>
                <v-tab @click.prevent="setNewActiveView('Tabella')">Tabella</v-tab>
                <v-tab @click.prevent="setNewActiveView('Grafico')">Grafico</v-tab>
                <v-tab @click.prevent="setNewActiveView('Mappa')">Mappa</v-tab>
            </v-tabs>
          </v-col>
          <v-col cols="6">
            <div class="text-right m-2" v-if="activeSelection && activeSelection.dataLevel">
              <v-btn
                color="primary"
                @click="exportCsv"
              >
                Download CSV
              </v-btn>
            </div>

          </v-col>
        </v-row>

        <v-tabs-items v-model="tab" class="mt-5">
          <v-tab-item key="Tabella">
            <data-table :dataTableData="viewData"></data-table>
          </v-tab-item>
          <v-tab-item key="Grafico">
            <data-chart :dataChartData="viewData" :configuration="localSelection"></data-chart>
          </v-tab-item>
          <v-tab-item key="Mappa">Mappa</v-tab-item>
        </v-tabs-items>
      </v-card>
    </v-col>
  </v-row>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import { validationMixin } from "vuelidate";
import { required, maxLength, email } from "vuelidate/lib/validators";
import DataTable from "@/components/stats-views/DataTable.vue";
import DataChart from "@/components/stats-views/DataChart.vue";
import { statService } from "../../services";
import { viewStatService } from "../../services";
export default {
  mixins: [validationMixin],

  validations: {
    name: { required, maxLength: maxLength(10) },
    email: { required, email },
    select: { required },
    checkbox: {
      checked(val) {
        return val;
      },
    },
  },

  components: {
    "data-table": DataTable,
    "data-chart": DataChart,
  },

  data() {
    return {
      drawer: true,
      mini: true,
      panel: [0],
      tab: null,
      sheet: false,
      showPickerFrom: false,
      showPickerTo: false,
      loader: null,
      // puntualAggregationValue: "NONE",
      //timePeriodValue: null,
      localSelection: {
        dataLevel: null,
        timeUnit: null,
        dataColumns: [],
        means: [],
        timePeriod: null,
        selectedDateFrom: new Date(),
        selectedDateTo: new Date(),
        puntualAggregation: [],
        puntualAggregationSelected: null,
        puntualAggregationItems: [],
        itemsAggreation: [],
        direction: 'all',
        groupByMean: false,
      },
      baseSelection: null,
      puntualAggregationSelected: null,
      timePeriod: null,
      name: "",
      email: "",
      select: null,
      // items: ["Item 1", "Item 2", "Item 3", "Item 4"],
      checkbox: false,
      viewData: null,
      meansList: [],
      allMeans: [
        { value: "bike", text: "Bici", order: 2 },
        { value: "car", text: "Auto", order: 5 },
        { value: "train", text: "Treno", order: 4 },
        { value: "walk", text: "Piedi", order: 1 },
        { value: "bus", text: "Autobus", order: 3 },
        { value: "boat", text: "Barca", order: 6 },
      ],
    };
  },
  computed: {
    ...mapState("stat", [
      "configurations",
      "activeConfiguration",
      "activeViewType",
      "activeSelection",
      "currentCampaign",
      "statValues",
    ]),
    ...mapState("company", ["actualCompany"]),
    ...mapState("navigation", ["page"]),
    ...mapState("navigation", ["page"]),
    ...mapState("account", ["user", "temporaryAdmin"]),
    timeSelected() {
      return (
        this.activeSelection &&
        this.activeSelection.timePeriod &&
        this.activeSelection.timePeriod.value != "ALL"
      );
    },
    view() {
      let view = null;
      if (
        this.getConfigurationById &&
        this.getConfigurationById.views &&
        this.activeViewType!=null
      ) {
        view = this.getConfigurationById.views.find(
          (element) => element.type === this.activeViewType.item
        );
      }

      return view;
    },

    getConfigurationById() {
      let conf = {};
      if (this.configurations && this.configurations.items && this.activeConfiguration)
        this.configurations.items.forEach((configuration) => {
          if (configuration.id == this.activeConfiguration.items) {
            conf = configuration;
          }
        });

      return conf;
    },

    checkboxErrors() {
      const errors = [];
      if (!this.$v.checkbox.$dirty) return errors;
      !this.$v.checkbox.checked && errors.push("You must agree to continue!");
      return errors;
    },
    selectErrors() {
      const errors = [];
      if (!this.$v.select.$dirty) return errors;
      !this.$v.select.required && errors.push("Item is required");
      return errors;
    },
    nameErrors() {
      const errors = [];
      if (!this.$v.name.$dirty) return errors;
      !this.$v.name.maxLength && errors.push("Name must be at most 10 characters long");
      !this.$v.name.required && errors.push("Name is required.");
      return errors;
    },
    emailErrors() {
      const errors = [];
      if (!this.$v.email.$dirty) return errors;
      !this.$v.email.email && errors.push("Must be valid e-mail");
      !this.$v.email.required && errors.push("E-mail is required");
      return errors;
    },
  },

  methods: {
    ...mapActions("stat", {
      initConfigurationByUser: "initConfigurationByUser",
      setActiveViewType: "setActiveViewType",
      setActiveSelection: "setActiveSelection",
      getStatFromServer: "getStat",
      downloadCsv: "getCsv",
    }),
    ...mapActions("stat", { getConfigurationByUser: "getConfigurationByUser" }),

    comparator(a, b) {
      return a.value === b.value;
    },
    copy(obj) {
      return obj ? JSON.parse(JSON.stringify(obj)) : null;
    },

    saveFiltersAndRefreshStat() {
      // this.sheet = !this.sheet;
      this.setActiveSelection({ selection: this.copy(this.localSelection) });
      this.getLocalStat(this.localSelection);
      this.mini = true;
    },

    resetFilterAndRefreshStat() {
      this.initiSelection();
      this.setActiveSelection({ selection: this.copy(this.localSelection) });
      this.getLocalStat(this.localSelection);
      this.mini = true;
    },

    resetPunctualAggregation() {
      this.puntualAggregationSelected = 'NONE';
      this.updatePuntualAggregationChange();
    },

    fillTheViewWithValues(values, view, activeSelection, currentCampaign, loader) {
      viewStatService
        .fillTheViewWithValues(values, view, activeSelection, currentCampaign)
        .then((viewData) => {
          this.viewData = viewData;
          loader.hide();
        });
    },
    getLocalStat(selection) {
      this.loader = this.$loading.show({
        canCancel: false,
        backgroundColor: "#000",
        color: "#fff",
      });
      try {
        this.getStatFromServer(selection);
      } finally {
        setTimeout(() => {
          this.loader.hide();
        }, 5000);
      }
    },
    getItemText(item) {
      return item.label;
    },
    getMeanText(item) {
      return item.text;
    },
    exportCsv() {
      this.downloadCsv(this.localSelection);
    },
    updateTimePeriod() {
      if (!this.localSelection || !this.view) return;
      this.localSelection.timePeriod = this.view.timePeriod.find((v) => v.value === this.timePeriod);
    },
    updatePuntualAggregationChange() {
      if (!this.localSelection) return;
      
      this.localSelection.puntualAggregationSelected = this.localSelection.dataLevel.puntualAggregation.find((v) => v.value === this.puntualAggregationSelected);
      if (
        this.localSelection &&
        this.localSelection.puntualAggregationSelected &&
        this.localSelection.puntualAggregationSelected.value == "NONE"
      )
        this.localSelection.puntualAggregationItems = [];
      else this.getItemsAggregation();
    },
    setNewActiveView(view) {
      console.log(view);
      // this.setActiveViewType({ activeViewType: view });
    },

    initConfigurationStat() {
      this.initConfigurationByUser({
        user: this.user,
        temporaryAdmin: this.temporaryAdmin,
      });
      //console.log(this.configurations.items);
    },

    submit() {
      this.$v.$touch();
    },
    clear() {
      this.$v.$reset();
      this.name = "";
      this.email = "";
      this.select = null;
      this.checkbox = false;
    },
    getItemsAggregation() {
      if (this.localSelection){
        if (!this.localSelection.puntualAggregationSelected){
          this.localSelection.puntualAggregationSelected= {
          function: "",
          label: "Nessuno",
          value: "NONE",
          }
        }
        statService
          .getItemsAggregation(
            this.localSelection.puntualAggregationSelected.value,
            this.localSelection.campaign ? this.localSelection.campaign.id : null,
            this.localSelection.company ? this.localSelection.company.id : null
          )
          .then(
            (values) => {
              values?.sort((a,b) => a?.label?.localeCompare(b.label));
              this.localSelection.itemsAggreation = values;
            },
            (err) => {
              console.log(err);
            }
          );
      }
    },
    getDataRange() {
      this.localSelection.selectedDateFrom = this.currentCampaign.item.from;
      this.localSelection.selectedDateTo = this.currentCampaign.item.to;
    },
    initiSelection() {
      this.tab = this.activeViewType;
      if (this.baseSelection && this.currentCampaign) {
        //init with view configuration

        this.localSelection.company = this.baseSelection.company
          ? this.baseSelection.company
          : this.actualCompany
          ? this.actualCompany.item
          : undefined;
        this.localSelection.campaign = this.currentCampaign.item;
        this.localSelection.dataLevel = this.baseSelection.dataLevel;
        this.localSelection.timeUnit = this.baseSelection.timeUnit;
        this.localSelection.dataColumns = this.baseSelection.dataColumns;
        console.log('timePeriod', this.baseSelection.timePeriod);
        this.localSelection.selectedDateFrom = this.baseSelection.selectedDateFrom;
        this.localSelection.selectedDateTo = this.baseSelection.selectedDateTo;
        this.puntualAggregationSelected = this.baseSelection.puntualAggregationSelected.value;
        this.updatePuntualAggregationChange();
        this.timePeriod = this.baseSelection.timePeriod.value;
        this.localSelection.timePeriod = this.baseSelection.timePeriod;
        //load specific values
        this.getItemsAggregation();
        this.getDataRange();
        console.log("Configurations loaded: "+ this.localSelection.campaign);
        this.meansList = this.allMeans.filter(m => this.localSelection.campaign.means.includes(m.value));
        this.localSelection.means = this.localSelection.campaign.means.slice();
      }
    },

    setDefaultCampaign() {},
  },
  created() {
    this.baseSelection = this.copy(this.activeSelection);
    this.initConfigurationStat();
  },
  mounted() {
  },

  watch: {
    statValues: {
      handler: function (newVal) {
        if (newVal && newVal.items) {
          this.fillTheViewWithValues(
            newVal.items,
            this.activeViewType,
            this.activeSelection,
            this.currentCampaign.item,
            this.loader
          );
        }
      },
      deep: true,
    },
    activeConfiguration() {
      this.tab = this.activeViewType;
      this.baseSelection = this.copy(this.activeSelection);

      this.initiSelection();
      if (this.activeSelection && this.currentCampaign) {
        this.getLocalStat(this.localSelection);
      }
    },

    currentCampaign: {
      handler(newValue, oldValue) {
        if (!oldValue && newValue || oldValue.item.id !== newValue.item.id) {
          this.initiSelection();
          if (this.activeSelection && this.currentCampaign) {
            this.getLocalStat(this.localSelection);
          }
        }
      },
      deep: true,
    },

    $route() {
      console.log("E' cambiata la root, ora sei in: ");
      console.log(this.page);
      this.tab = this.activeViewType;
    },
  },
};
</script>
