<template>
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
                height="500px"
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
                  {{getConfigurationById}}
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

                <!-- Componenti per il filtraggio -->

                <!--
                <v-row justify="center">
                  <v-col
                    cols="4"
                    class="pl-5 pr-20"
                  >
                    <p class="text-subtitle-1">Livello Aggregazione</p>
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
                        v-model="unitaTemporale"
                        :items="listaUnitaTemporali"
                        outlined
                    ></v-autocomplete>
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
                  </v-col>

                  <v-col
                    cols="4"
                    class="pl-5 pr-20"
                  >
                    <p class="text-subtitle-1">Colonne Dati</p>
                  </v-col>
                </v-row>
            -->

            </v-sheet>
        </v-bottom-sheet>

    </div>
</template>


<script>
import {mapActions, mapState} from "vuex";
import { validationMixin } from 'vuelidate'
import { required, maxLength, email } from 'vuelidate/lib/validators'

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

    props: {
        method: Function,
    },

    data(){
        return {
            sheet: false,
            pippo: "ciao",

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
            menu: false,
            menu2: false,
            DA: null,
            A: null,
            radioGroup: "Intera durata campagna",
            unitaTemporale:"Settimana",
            listaUnitaTemporali: ['Mese','Settimana','Giorno','Campagna'],
        }
    },

    computed: {
      ...mapState("stat", ["configurations", "activeViewType", "activeConfiguration"]),

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

      ...mapActions("stat",{getConfigurationByRole:"getConfigurationByRole"}),
      
      loadConfiguration(){
        this.getConfigurationByRole({role:"ROLE_COMPANY_ADMIN"});
        console.log(this.configurations.items);
      },

    },

    created(){
      this.loadConfiguration();
    },
}
</script>


<style>
</style>