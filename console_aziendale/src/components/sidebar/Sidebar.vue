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
                    class="mt-6"
                    text
                    color="red"
                    @click="sheet = !sheet"
                    >
                    close
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

                <!--
                <form class="mx-4">
                    <v-row>
                        <v-col cols="3">
                            <v-select
                            v-model="select"
                            :items="items"
                            :error-messages="selectErrors"
                            label="Seleziona una campagna"
                            required
                            @change="$v.select.$touch()"
                            @blur="$v.select.$touch()"
                            ></v-select>
                            <v-select
                            v-model="select"
                            :items="items"
                            :error-messages="selectErrors"
                            label="Seleziona un'azienda"
                            required
                            @change="$v.select.$touch()"
                            @blur="$v.select.$touch()"
                            ></v-select>
                            <v-select
                            v-model="select"
                            :items="items"
                            :error-messages="selectErrors"
                            label="Seleziona il tipo di dato"
                            required
                            @change="$v.select.$touch()"
                            @blur="$v.select.$touch()"
                            ></v-select>
                        </v-col>

                        <v-col cols="3">
                          <v-select
                            v-model="select"
                            :items="items"
                            :error-messages="selectErrors"
                            label="Visualizzazione Temporale"
                            required
                            @change="$v.select.$touch()"
                            @blur="$v.select.$touch()"
                          ></v-select>
                          <v-select
                            v-model="select"
                            :items="items"
                            :error-messages="selectErrors"
                            label="Seleziona il mese"
                            required
                            @change="$v.select.$touch()"
                            @blur="$v.select.$touch()"
                          ></v-select>
                        </v-col>

                        <v-col cols="3">
                          <v-select
                            v-model="select"
                            :items="items"
                            :error-messages="selectErrors"
                            label="Seleziona il mezzo"
                            required
                            @change="$v.select.$touch()"
                            @blur="$v.select.$touch()"
                          ></v-select>
                        </v-col>

                        <v-col cols="3">
                          <v-select
                            v-model="select"
                            :items="items"
                            :error-messages="selectErrors"
                            label="Raggruppa per"
                            required
                            @change="$v.select.$touch()"
                            @blur="$v.select.$touch()"
                          ></v-select>
                        </v-col>

                    </v-row>
                    <v-checkbox
                    v-model="checkbox"
                    :error-messages="checkboxErrors"
                    label="Do you agree?"
                    required
                    @change="$v.checkbox.$touch()"
                    @blur="$v.checkbox.$touch()"
                    ></v-checkbox>

                    <v-btn
                    class="mr-4"
                    @click="submit"
                    >
                    submit
                    </v-btn>
                    <v-btn @click="clear">
                    clear
                    </v-btn>
                </form>
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