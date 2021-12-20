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
            <data-table></data-table>
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
        <v-card v-if="activeViewType">
          <div>
            <v-card-title> Filtri - {{ activeViewType.item }} </v-card-title>
          </div>
          <v-card-text class="px-5 py-4" v-if="activeSelection">
            <p v-if="activeSelection.dataLevel" class="p-0">
              <b>Livello</b>: {{ activeSelection.dataLevel }}
            </p>
            <p v-if="activeSelection.timeUnit" class="p-0">
              <b>Unità temporale</b>: {{ activeSelection.timeUnit }}
            </p>
          </v-card-text>

          <v-card-actions>
            <v-btn color="primary" text @click="sheet = !sheet"> Modifica filtri </v-btn>
          </v-card-actions>
        </v-card>

        <!-- Gestore di inserimento dati (MODALE) -->
        <v-bottom-sheet v-model="sheet">
          <v-sheet height="100vh">
            <div class="text-center">
              <v-btn class="my-6" text color="primary" @click="sheet = !sheet">
                Salva Filtri
              </v-btn>
            </div>
            <div v-if="getConfigurationById">
              {{ getConfigurationById.views }}
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
                  @change="updateTimeUnit"
                  outlined
                ></v-autocomplete>
              </v-col>

              <v-col cols="4" class="pl-5 pr-20">
                <p class="text-subtitle-1">Colonne Dati</p>
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
      pippo: "ciao",
      localSelection: {
        dataLevel: null,
        timeUnit: null,
      },
      name: "",
      email: "",
      select: null,
      items: ["Item 1", "Item 2", "Item 3", "Item 4"],
      checkbox: false,
    };
  },
  computed: {
    ...mapState("stat", [
      "configurations",
      "activeConfiguration",
      "activeViewType",
      "activeSelection",
    ]),
    ...mapState("navigation", ["page"]),
    // activeSelection () {
    //   return this.$store.state.stat.activeSelection;
    // },
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
    }),
    updateTimeUnit() {
      this.setActiveSelection({ selection: this.localSelection });
    },
    updateDataLevel() {
      this.setActiveSelection({ selection: this.localSelection });
    },
    setNewActiveView(view) {
      this.setActiveViewType({ activeViewType: view });
    },

    initConfigurationStat() {
      this.initConfigurationByRole({ role: "ROLE_COMPANY_ADMIN" });
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
  },
  created() {
    this.initConfigurationStat();
  },
  mounted() {
    this.tab = this.activeViewType;
    if (this.activeSelection) {
      this.localSelection.dataLevel = this.activeSelection.dataLevel;
      this.localSelection.timeUnit = this.activeSelection.timeUnit;
    }
  },

  watch: {
    activeConfiguration() {
      this.tab = this.activeViewType;
    },
    activeSelection: {
      handler: function (oldValue, newValue) {
        if (!oldValue && newValue) {
          this.localSelection.dataLevel = this.activeSelection.dataLevel;
          this.localSelection.timeUnit = this.activeSelection.timeUnit;
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
