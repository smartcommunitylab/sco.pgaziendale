<template>
  <modal>
        <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
            <form action="">
                <div class="mb-20 flex flex-wrap justify-between">
                <v-row>
                    <v-col
                    cols="6"
                    >
                    <v-text-field
                        label="Nome"
                        placeholder="Nome *"
                        type="text"
                        name="employeeName"
                        id="employeeName"
                        v-model.trim="$v.name.$model"
                        :error-messages="nameErrors"                                
                        required
                        @input="$v.name.$touch()"
                        @blur="$v.name.$touch()"
                        outlined
                    ></v-text-field>
                    </v-col>
                    <v-col
                    cols="6"
                    >
                    <v-text-field
                        label="Cognome"
                        placeholder="Cognome *"
                        type="text"
                        name="employeeSurname"
                        id="employeeSurname"
                        v-model.trim="$v.surname.$model"
                        :error-messages="surnameErrors"                                
                        required
                        @input="$v.surname.$touch()"
                        @blur="$v.surname.$touch()"
                        outlined
                    ></v-text-field>
                    </v-col>
                    <v-col
                    cols="6"
                    >
                    <v-text-field
                        label="Codice"
                        placeholder="Codice *"
                        type="text"
                        name="employeeCode"
                        id="employeeCode"
                        v-model.trim="$v.code.$model"
                        :error-messages="codeErrors"                                
                        required
                        @input="$v.code.$touch()"
                        @blur="$v.code.$touch()"
                        outlined
                    >
                        <template v-slot:append>
                        <v-tooltip
                            bottom
                            nudge-bottom="10px"
                            nudge-left="100px"
                        >
                            <template v-slot:activator="{ on }">
                            <v-icon v-on="on">
                                mdi-help-circle-outline
                            </v-icon>
                            </template>
                            Il codice univoco con cui viene identificato il dipendente a livello di gioco
                        </v-tooltip>
                        </template>
                    </v-text-field>
                    </v-col>
                    <v-col
                    cols="6"
                    > 
                    <v-autocomplete
                        label="Sede"
                        placeholder="Sede *"
                        name="companyLocation"
                        id="companyLocation"
                        v-model.trim="$v.location.$model"
                        :items="listaSedi"
                        :error-messages="locationErrors"                                
                        required
                        @input="$v.location.$touch()"
                        @blur="$v.location.$touch()"
                        outlined
                    ></v-autocomplete>
                    </v-col>
                </v-row>
                </div>
            </form>
        </template>
        <template v-slot:footer>
            <v-btn
            text
            @click="closeThisModal"
            class="py-8 ml-8"
            >
            Annulla
            </v-btn>
            <v-btn
            color="primary"
            text
            @click="saveEmployee"
            class="py-8 ml-8"
            >
            Conferma
            </v-btn>
        </template>
    </modal>
</template>

<script>
import { validationMixin } from 'vuelidate';
import { required } from "vuelidate/lib/validators";
import { mapState, mapActions } from "vuex";
import Modal from "@/components/modal/ModalStructure.vue";

export default {
  mixins: [validationMixin],

  components: {"modal": Modal},

  props: {typeCall: String},

  validations: {
    name: {
      required,
    },
    surname: {
      required,
    },
    code: {
      required,
    },
    location: {
      required,
    },
  },

  data() {
    return {
      employee: {},
      name: "",
      surname: "",
      code: "",
      location: "",
      locations: [],
      listaSedi: [],
      popup: {
          title: '',
      }
    };
  },

  methods: {
    ...mapActions("modal", {closeModal: 'closeModal'}),
    ...mapActions("employee", {addEmployee: 'addEmployee', updateEmployee: 'updateEmployee'}),
    ...mapActions("location", {getAllLocations: "getAllLocations"}),

    loadLocations() {
      if (this.actualCompany) this.getAllLocations(this.actualCompany.item.id);
    },
    copyFormValues(employee) {
      for (const [key] of Object.entries(employee)) {
        this[key] = employee[key];
      }
    },
    initEmployee() {
      this.employee = {};
      this.id = null;
      this.name = "";
      this.surname = "";
      this.code = "";
      this.location = "";
    },
    createEmployee() {
      this.employee = {
        id: this.id,
        name: this.name,
        surname: this.surname,
        code: this.code,
        location: this.location,
      };
    },
    setModalData(){
      if(this.typeCall == "add"){
          this.popup.title = "Aggiungi Dipendente"
          this.initEmployee();
      }else if(this.typeCall == "edit"){
          this.popup.title = "Modifica Dipendente"
          this.copyFormValues(this.actualEmployee.item);
      }
    },
    saveEmployee() {
      if (!this.$v.$invalid) {
          this.createEmployee();
          if(this.typeCall == "add"){
              this.addEmployee({companyId: this.actualCompany.item.id, employee: this.employee});
              this.closeModal();
          }else if (this.typeCall == "edit") {
              this.updateEmployee({
                  companyId: this.actualCompany.item.id,
                  employee: this.employee,
              });
              this.closeThisModal();
              
          }
      }else{
          this.$v.$touch();
      }
    },
    closeThisModal(){
        this.initEmployee();
        this.$v.$reset();
        this.closeModal();
    }
  },

  computed: {
    ...mapState("employee", ["allEmployees", "actualEmployee"]),
    ...mapState("company", ["actualCompany"]),
    ...mapState("location", ["allLocations", "actualLocation"]),

    //Controls for form validation 
    nameErrors () {
        const errors = []
        if (!this.$v.name.$dirty) return errors
        !this.$v.name.required && errors.push('Campo richiesto.')
        return errors
    },
    surnameErrors () {
        const errors = []
        if (!this.$v.surname.$dirty) return errors
        !this.$v.surname.required && errors.push('Campo richiesto.')
        return errors
    },
    codeErrors () {
        const errors = []
        if (!this.$v.code.$dirty) return errors
        !this.$v.code.required && errors.push('Campo richiesto.')
        return errors
    },
    locationErrors () {
        const errors = []
        if (!this.$v.location.$dirty) return errors
        !this.$v.location.required && errors.push('Campo richiesto.')
        return errors
    },
  },

  watch: {
    allLocations(locations) {
      // Our fancy notification (2).
      if (locations.items)
        {
          for (let i=0;i<locations.items.length; i++)
            {
              this.listaSedi.push(locations.items[i].id);}
          console.log(this.listaSedi);
        }
    },
    typeCall(){
        this.setModalData();
    },
    actualEmployee(){
        this.setModalData();
    }   
  },
  
  created() {
    this.setModalData();
  },

  mounted() {
    this.loadLocations();
    //this.listaSedi.unshift(this.allLocations.items.id);
  },
};
</script>

<style scoped>
</style>
