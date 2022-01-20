<template>
  <v-row>
    <v-col cols="9">
      <v-card>
        <v-tabs
          v-model="tab"
          align-with-title
          v-if="configurations && configurations.items"
        >
          <v-tabs-slider color="primary"></v-tabs-slider>

          <v-tab
            v-for="item in configurations.items[0].views"
            :key="item.type"
            @click.prevent="setNewActiveView(item.type)"
          >
            {{ item.type }}
          </v-tab>
        </v-tabs>

        <v-tabs-items v-model="tab" class="mt-5">
          <v-tab-item key="Tabella">
            <data-table :dataTableData="viewData"></data-table>
          </v-tab-item>
          <v-tab-item key="Grafico a Linee">
            <line-chart></line-chart>
          </v-tab-item>
          <v-tab-item key="Grafico a Barre">
            <bar-chart></bar-chart>
          </v-tab-item>
          <v-tab-item key="Mappa???"> </v-tab-item>
        </v-tabs-items>
      </v-card>
    </v-col>
    <v-col cols="3">
      <div>
        <v-card v-if="activeViewType && activeSelection">
          <div>
            <v-card-title> Filtri - {{ activeViewType.item }} </v-card-title>
          </div>
          <v-card-text class="px-5 py-4" v-if="activeSelection">
            <p v-if="activeSelection.dataLevel" class="p-0">
              <b>Livello</b>: {{ activeSelection.dataLevel.label }}
            </p>
            <p v-if="activeSelection.timeUnit" class="p-0">
              <b>Unità temporale</b>: {{ activeSelection.timeUnit.label }}
            </p>
            <p v-if="activeSelection.dataColumns" class="p-0">
              <b>Colonne dati</b>:
              <span v-for="(column, index) in activeSelection.dataColumns" :key="index">{{
                column.label
              }}</span>
            </p>
            <p v-if="activeSelection.timePeriod" class="p-0">
              <b>Periodo di tempo</b>:
              <template v-if="timeSelected"
                ><span>{{ activeSelection.selectedDateFrom }}<br /></span>
                <span>{{ activeSelection.selectedDateTo }}<br /></span> </template
              ><template v-else
                ><span>{{ activeSelection.timePeriod.label }}</span></template
              >
            </p>
            <p v-if="activeSelection.dataLevel.puntualAggregation" class="p-0">
              <b>Aggregazione puntuale</b>:
              <template v-if="activeSelection.dataLevel.puntualAggregation.value != 'NONE'"
                ><span
                  v-for="(agg, index) in activeSelection.itemAggregation"
                  :key="index"
                  >{{ agg.name }}<br /></span></template
              ><template v-else><span>Nessuna</span></template>
            </p>
          </v-card-text>

          <v-card-actions>
            <v-btn color="primary" text @click="sheet = !sheet"> Modifica filtri </v-btn>
          </v-card-actions>
        </v-card>
        <div class="text-center m-2">
          <v-btn color="primary" @click="exportCsv"> Download CSV </v-btn>
        </div>
        <!-- Gestore di inserimento dati (MODALE) -->
        <v-bottom-sheet v-model="sheet">
          <v-sheet height="100vh">
            <div class="text-center">
              <v-btn
                class="my-6"
                text
                color="primary"
                @click="saveFiltersAndRefreshStat()"
              >
                Salva Filtri
              </v-btn>
            </div>

            <div v-if="activeViewType">
              <div v-if="activeViewType.item === 'Tabella'">Tabella</div>
              <div v-if="activeViewType.item === 'Grafico a Barre Orizzontali'">
                Grafico a Barre Orizzontali
              </div>
              <div v-if="activeViewType.item === 'Grafico a Linee'">Grafico a Linee</div>
              <div v-if="activeViewType.item === 'Grafico a Barre'">Grafico a Barre</div>
            </div>

            <!-- INPUT - Componenti per il filtraggio -->

            <v-row justify="center">
              <v-col cols="4" class="pl-5 pr-20" v-if="localSelection && view">
                <p class="text-subtitle-1">Livello Aggregazione</p>

                <v-autocomplete
                  label="Livello Aggregazione"
                  name="livelloaggregazione"
                  id="livelloaggregazione"
                  v-model="localSelection.dataLevel"
                  :items="view.dataLevel"
                  item-text="label"
                  return-object
                  @change="updateDataLevel"
                  outlined
                ></v-autocomplete>
              </v-col>

              <v-col cols="4" class="pl-5 pr-20" v-if="localSelection && view">
                <p class="text-subtitle-1 mb-10">Unità temporale</p>
                <v-autocomplete
                  label="Unità Temporale"
                  placeholder="Unità Temporale"
                  name="unitaTemporale"
                  id="unitaTemporale"
                  v-model="localSelection.timeUnit"
                  :items="view.timeUnit"
                  item-text="label"
                  return-object
                  @change="updateTimeUnit"
                  outlined
                ></v-autocomplete>
              </v-col>

              <v-col cols="4" class="pl-5 pr-20" v-if="localSelection && view">
                <p class="text-subtitle-1">Colonne Dati</p>
                <v-select
                  v-model="localSelection.dataColumns"
                  :items="view.dataColumns"
                  item-text="label"
                  :menu-props="{ maxHeight: '400' }"
                  label="Colonne Dati"
                  multiple
                  hint="Seleziona le colonne da visualizzare"
                  persistent-hint
                ></v-select>
              </v-col>
            </v-row>
            <v-row v-if="view">
              <v-col
                cols="4"
                class="pl-5 pr-20"
                v-if="localSelection && localSelection.dataLevel.puntualAggregation"
              >
                <p class="text-subtitle-1">Aggregazione puntuale</p>
                <v-radio-group
                  v-model="localSelection.dataLevel.puntualAggregation"
                  @change="updatePuntualAggregationChange"
                >
                  <v-radio
                    v-for="(agg, index) in view.dataLevel.puntualAggregation"
                    :key="index"
                    :label="agg.label"
                    :value="agg"
                  ></v-radio>
                </v-radio-group>
                <div
                  v-if="
                    localSelection.dataLevel.puntualAggregation &&
                    localSelection.dataLevel.puntualAggregation.value != 'NONE' &&
                    localSelection.itemsAggreation != null
                  "
                >
                  <v-autocomplete
                    label="Selezione"
                    name="typeData"
                    id="typeData"
                    v-model="localSelection.puntualAggregationItems"
                    :items="localSelection.itemsAggreation"
                    item-text="name"
                    return-object
                    outlined
                    multiple
                  ></v-autocomplete>
                </div>
              </v-col>
              <v-col cols="4" class="pl-5 pr-20">
                <p class="text-subtitle-1">Periodo di tempo</p>
                <v-radio-group v-model="localSelection.timePeriod">
                  <v-radio
                    v-for="(period, index) in view.timePeriod"
                    :key="index"
                    :label="period.label"
                    :value="period"
                    @change="updateTimePeriodChange"
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
                        hint="DD/MM/YYYY"
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
                        hint="DD/MM/YYYY"
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
              </v-col>
            </v-row>
          </v-sheet>
        </v-bottom-sheet>
      </div>
    </v-col>
  </v-row>
</template>

<script>
import { mapState, mapActions } from "vuex";
import { validationMixin } from "vuelidate";
import { required, maxLength, email } from "vuelidate/lib/validators";
import DataTable from "@/components/stats-views/DataTable.vue";
import LineChart from "@/components/stats-views/LineChart.vue";
import BarChart from "@/components/stats-views/BarChart.vue";
import { statService } from "../../services";
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
    "line-chart": LineChart,
    "bar-chart": BarChart,
  },

  data() {
    return {
      tab: null,
      sheet: false,
      showPickerFrom: false,
      showPickerTo: false,
      // puntualAggregationValue: "NONE",
      //timePeriodValue: null,
      localSelection: {
        dataLevel: null,
        timeUnit: null,
        dataColumns: null,
        timePeriod: null,
        selectedDateFrom: new Date(),
        selectedDateTo: new Date(),
        puntualAggregation: null,
        puntualAggregationItems: [],
        itemsAggreation: [],
      },
      name: "",
      email: "",
      select: null,
      items: ["Item 1", "Item 2", "Item 3", "Item 4"],
      checkbox: false,
      viewData: null,
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
    ...mapState("navigation", ["page"]),
    ...mapState("account", ["role", "temporaryAdmin"]),
    // activeSelection () {
    //   return this.$store.state.stat.activeSelection;
    // },
    activeAggregation() {
      return (
        this.activeSelection.dataLevel.puntualAggregation &&
        this.activeSelection.dataLevel.puntualAggregation.length
      );
    },
    timeSelected() {
      return this.localSelection.timePeriod.value != "ALL";
    },
    view() {
      console.log("La vista è: ");
      let view = null;
      if (this.getConfigurationById && this.getConfigurationById.views)
        view = this.getConfigurationById.views.find(
          (element) => element.type === this.activeViewType.item
        );
      console.log(view);

      return view;
    },

    getConfigurationById() {
      let conf = {};
      if (this.configurations && this.configurations.items)
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
      initConfigurationByRole: "initConfigurationByRole",
      setActiveViewType: "setActiveViewType",
      setActiveSelection: "setActiveSelection",
      getStatFromServer: "getStat",
      downloadCsv: "getCsv",
    }),
    ...mapActions("stat", { getConfigurationByRole: "getConfigurationByRole" }),

    saveFiltersAndRefreshStat() {
      this.sheet = !this.sheet;
      this.getLocalStat(this.localSelection);
    },
    fillTheViewWithValues(values, view, activeSelection, currentCampaign) {
      statService
        .fillTheViewWithValues(values, view, activeSelection, currentCampaign)
        .then((viewData) => {
          this.viewData = viewData;
        });
    },
    getLocalStat(selection) {
      this.getStatFromServer(selection);
    },
    exportCsv() {
      this.downloadCsv(this.localSelection);
    },
    updateTimeUnit() {
      this.setActiveSelection({ selection: this.localSelection });
    },
    updateDataLevel() {
      this.setActiveSelection({ selection: this.localSelection });
    },
    updateDataColumns() {
      this.setActiveSelection({ selection: this.localSelection });
    },
    updateTimePeriodChange() {
      this.setActiveSelection({ selection: this.localSelection });
    },
    updatePuntualAggregationChange() {
      if (this.localSelection.dataLevel.puntualAggregation.value == "NONE")
        this.localSelection.puntualAggregationItems = [];
      else this.getItemsAggregation();
      this.setActiveSelection({ selection: this.localSelection });
    },
    setNewActiveView(view) {
      this.setActiveViewType({ activeViewType: view });
    },

    initConfigurationStat() {
      this.initConfigurationByRole({
        role: this.role,
        temporaryAdmin: this.temporaryAdmin,
      });
      console.log(this.configurations.items);
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
      if (this.localSelection)
        statService
          .getItemsAggregation(
            this.localSelection.dataLevel.puntualAggregation.value,
            this.localSelection.campaign.id
          )
          .then(
            (values) => {
              this.localSelection.itemsAggreation = values;
              console.log(values);
            },
            (err) => {
              console.log(err);
            }
          );
    },
    getDataRange() {
      this.localSelection.selectedDateFrom = this.currentCampaign.item.from;
      this.localSelection.selectedDateTo = this.currentCampaign.item.to;
    },
    initiSelection() {
      this.tab = this.activeViewType;
      if (this.activeSelection && this.currentCampaign) {
        //init with view configuration
        this.localSelection.company = this.activeSelection.company;
        this.localSelection.campaign = this.currentCampaign.item;
        this.localSelection.dataLevel = this.activeSelection.dataLevel;
        this.localSelection.timeUnit = this.activeSelection.timeUnit;
        this.localSelection.dataColumns = this.activeSelection.dataColumns;
        this.localSelection.timePeriod = this.activeSelection.timePeriod;
        //this.timePeriodValue = this.activeSelection.timePeriod.value;
        this.localSelection.selectedDateFrom = this.activeSelection.selectedDateFrom;
        this.localSelection.selectedDateTo = this.activeSelection.selectedDateTo;
        // this.localSelection.puntualAggregation = this.activeSelection.puntualAggregation;
        //load specific values
        this.getItemsAggregation();
        this.getDataRange();
        //load default values
        this.getLocalStat(this.localSelection);
      }
    },

    setDefaultCampaign() {},
  },
  created() {
    this.initConfigurationStat();
    this.initiSelection();
  },
  mounted() {
    if (
      this.statValues &&
      this.statValues.items &&
      this.activeViewType &&
      this.activeSelection &&
      this.currentCampaign.item
    )
      this.fillTheViewWithValues(
        this.statValues.items,
        this.activeViewType,
        this.activeSelection,
        this.currentCampaign.item
      );
  },

  watch: {
    statValues: {
      handler: function (newVal) {
        if (newVal && newVal.items) {
          this.fillTheViewWithValues(
            newVal.items,
            this.activeViewType,
            this.activeSelection,
            this.currentCampaign.item
          );
        }
      },
      deep: true,
    },

    // puntualAggregationValue() {
    //   this.getItemsAggregation();
    // },
    activeConfiguration() {
      this.tab = this.activeViewType;
    },
    // puntualAggregation() {
    //    this.getItemsAggregation();
    // },
    currentCampaign: {
      handler: function (newValue, oldValue) {
        if (!oldValue && newValue) {
          // if (!this.activeSelection.campaign)
          //   this.setDefaultCampaign();
          // else
          this.initiSelection();
        }
      },
      deep: true,
    },
    // activeSelection: {
    //   handler: function (newValue,oldValue) {
    //     if (!oldValue && newValue && this.localSelection && this.activeSelection ) {
    //       if (!this.activeSelection.campaign)
    //         this.setDefaultCampaign();
    //       else
    //         this.initiSelection();
    //     }
    //   },
    //   deep: true,
    // },

    $route() {
      console.log("E' cambiata la root, ora sei in: ");
      console.log(this.page);
      this.tab = this.activeViewType;
    },
  },
};
</script>
