<template>
    <v-row>
        <v-col cols="9">
            <v-card>
                <v-tabs
                    v-model="tab"
                    align-with-title
                >
                    <v-tabs-slider color="primary"></v-tabs-slider>

                    <v-tab
                    v-for="item in configurations.items[0].views"
                    :key="item.type"
                    @click.prevent="setNewActualView(item.type)"
                    >
                        {{ item.type }}
                    </v-tab>
                </v-tabs>
                
                <v-tabs-items v-model="tab" class="mt-5">
                    <v-tab-item key="Tabella">
                        <data-table></data-table>
                    </v-tab-item>
                    <v-tab-item key="Grafico a Barre Orizzontali">
                    </v-tab-item>
                    <v-tab-item key="Grafico a Linee">
                        <line-chart></line-chart>
                    </v-tab-item>
                    <v-tab-item key="Grafico a Barre">
                    </v-tab-item>
                    <v-tab-item key="Mappa???">
                    </v-tab-item>
                </v-tabs-items>  
            </v-card>
        </v-col>
        <v-col cols="3">
            <div>
                <v-card>
                    <div>
                    <v-card-title> Filtri - {{activeViewType.items}} </v-card-title>
                    
                    </div>
                    <v-card-text class="px-5 py-4">
                        <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                        <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                        <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                        <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                        <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                        <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                        <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                        <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>   
                    </v-card-text>

                    <v-card-actions>
                        <v-btn
                            color="primary"
                            text
                            @click="sheet = !sheet"
                        >
                            Modifica filtri
                        </v-btn>
                    </v-card-actions>
                </v-card>

                <!-- Gestore di inserimento dati (MODALE) -->
                <v-bottom-sheet v-model="sheet">
                    <v-sheet
                        height="100vh"
                    >
                        <div class="text-center">
                            <v-btn
                            class="my-6"
                            text
                            color="primary"
                            @click="sheet = !sheet"
                            >
                            Salva Filtri
                            </v-btn>
                        </div>
                        <div>
                        {{getConfigurationById.views}}
                        </div>

                        <div v-if="getConfigurationById">
                        <div v-if="activeViewType.items === 'Tabella'">
                            Tabella
                        </div>
                        <div v-if="activeViewType.items === 'Grafico a Barre Orizzontali'">
                            Grafico a Barre Orizzontali
                        </div>
                        <div v-if="activeViewType.items === 'Grafico a Linee'">
                            Grafico a Linee
                        </div>
                        <div v-if="activeViewType.items === 'Grafico a Barre'">
                            Grafico a Barre
                        </div>                  
                        </div>

                        <!-- INPUT - Componenti per il filtraggio -->

                        
                        <v-row justify="center">
                        <v-col
                            cols="4"
                            class="pl-5 pr-20"
                        >
                            <p class="text-subtitle-1">Livello Aggregazione</p>
                        
                            <v-autocomplete
                                label="Unità Temporale"
                                name="unitaTemporale"
                                id="unitaTemporale"
                                v-model="selectDataLevel"
                                :items="view.dataLevel"
                                outlined
                            ></v-autocomplete>
                        </v-col>
                        
                        
                        <v-col
                            cols="4"
                            class="pl-5 pr-20"
                        >
                            <p class="text-subtitle-1 mb-10">Unità temporale</p>
                            <v-autocomplete
                                label="Unità Temporale"
                                placeholder="Unità Temporale"
                                name="unitaTemporale"
                                id="unitaTemporale"
                                v-model="selectTimeUnit"
                                :items="view.timeUnit"
                                outlined
                            ></v-autocomplete>
                            <!--
                            <v-container
                            class="px-0"
                            fluid
                            >
                            <v-radio-group v-model="radioGroup">
                                <v-radio
                                label="Intera durata campagna"
                                value="Intera durata campagna"
                                ></v-radio>
                                <v-radio
                                label="Periodo Specifico"
                                value="Periodo Specifico"
                                ></v-radio>
                            </v-radio-group>
                            </v-container>
                            <v-menu
                                ref="menu"
                                v-model="menu"
                                :close-on-content-click="false"
                                :return-value.sync="DA"
                                transition="scale-transition"
                                offset-y
                                min-width="auto"
                                outlined            
                            >
                            <template v-slot:activator="{ on, attrs }">
                                <v-text-field
                                v-model="DA"
                                label="DA"
                                prepend-icon="mdi-calendar"
                                readonly
                                v-bind="attrs"
                                v-on="on"
                                ></v-text-field>
                            </template>
                            <v-date-picker
                            v-model="DA"
                            no-title
                            scrollable
                            color="primary"
                            >
                                <v-btn
                                    text
                                    color="primary"
                                    @click="$refs.menu.save(DA)"
                                >
                                    Conferma
                                </v-btn>
                            </v-date-picker>
                            </v-menu>
                            <v-menu
                                ref="menu2"
                                v-model="menu2"
                                :close-on-content-click="false"
                                :return-value.sync="A"
                                transition="scale-transition"
                                offset-y
                                min-width="auto"
                                outlined            
                            >
                            <template v-slot:activator="{ on, attrs }">
                                <v-text-field
                                v-model="A"
                                label="A"
                                prepend-icon="mdi-calendar"
                                readonly
                                v-bind="attrs"
                                v-on="on"
                                ></v-text-field>
                            </template>
                            <v-date-picker
                            v-model="A"
                            no-title
                            scrollable
                            color="primary"
                            >
                                <v-btn
                                    text
                                    color="primary"
                                    @click="$refs.menu2.save(A)"
                                >
                                    Conferma
                                </v-btn>
                            </v-date-picker>
                            </v-menu>
                            -->
                        </v-col>

                        <v-col
                            cols="4"
                            class="pl-5 pr-20"
                        >
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
import { validationMixin } from 'vuelidate'
import { required, maxLength, email } from 'vuelidate/lib/validators'
import DataTable from "@/components/stats-views/DataTable.vue";
import LineChart from "@/components/stats-views/LineChart.vue";

export default {
    mixins: [validationMixin],

    validations: {
      name: { required, maxLength: maxLength(10) },
      email: { required, email },
      select: { required },
      checkbox: {
        checked (val) {
          return val
        },
      },
    },

    components: {
        "data-table" : DataTable,
        "line-chart" : LineChart
    },  
    
    

    
    data () {
      return {
        tab: null,
        sheet: false,
            pippo: "ciao",

            selectDataLevel: null,
            selectTimeUnit: null,

            name: '',
            email: '',
            select: null,
            items: [
                'Item 1',
                'Item 2',
                'Item 3',
                'Item 4',
            ],
            checkbox: false,
      }
    },
    computed:{
        ...mapState("stat", ["configurations", "activeConfiguration", "activeViewType"]),
        ...mapState("navigation", ["page"]),
        view(){
        
        console.log("La vista è: ");

        let view = this.getConfigurationById.views.find(
          (element) => element.type === this.activeViewType.items
        );
        console.log(view);

        return view;
      },

      getConfigurationById(){
        let conf = {};

        this.configurations.items.forEach(configuration => {
          if(configuration.id == this.activeConfiguration.items){
            conf = configuration;
          }
        });
        
        return conf;
      },

      checkboxErrors () {
        const errors = []
        if (!this.$v.checkbox.$dirty) return errors
        !this.$v.checkbox.checked && errors.push('You must agree to continue!')
        return errors
      },
      selectErrors () {
        const errors = []
        if (!this.$v.select.$dirty) return errors
        !this.$v.select.required && errors.push('Item is required')
        return errors
      },
      nameErrors () {
        const errors = []
        if (!this.$v.name.$dirty) return errors
        !this.$v.name.maxLength && errors.push('Name must be at most 10 characters long')
        !this.$v.name.required && errors.push('Name is required.')
        return errors
      },
      emailErrors () {
        const errors = []
        if (!this.$v.email.$dirty) return errors
        !this.$v.email.email && errors.push('Must be valid e-mail')
        !this.$v.email.required && errors.push('E-mail is required')
        return errors
      },

    },

    methods: {
      ...mapActions("stat",{getConfigurationByRole:"getConfigurationByRole", setActiveViewType:"setActiveViewType"}),

      setNewActualView(view){
        this.setActiveViewType({activeViewType:view});
      },

      loadConfiguration(){
        this.getConfigurationByRole({role:"ROLE_COMPANY_ADMIN"});
        console.log(this.configurations.items);
      },

      submit () {
        this.$v.$touch()
      },
      clear () {
        this.$v.$reset()
        this.name = ''
        this.email = ''
        this.select = null
        this.checkbox = false
      },
    },
    created(){
      this.loadConfiguration();
    },
    mounted(){
       this.tab = this.actualViewType;
    },

    watch:{
        activeConfiguration(){
            this.tab = this.actualViewType;
        },
        $route(){
            console.log("E' cambiata la root, ora sei in: ");
            console.log(this.page);
            this.tab = this.actualViewType;
        },
    }
  }
</script>
