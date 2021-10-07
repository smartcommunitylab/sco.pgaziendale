<template>
  <form action="" id="addEmployee">
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
<script>
import { validationMixin } from 'vuelidate';
import { required } from "vuelidate/lib/validators";
import EventBus from "@/components/eventBus";
import { mapState, mapActions } from "vuex";

export default {
  mixins: [validationMixin],
  data() {
    return {
      employee: {},
      name: "",
      surname: "",
      code: "",
      location: "",
      locations: [],
      listaSedi: [],
    };
  },
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


  computed: {
    ...mapState("company", ["actualCompany"]),
    ...mapState("location", ["allLocations", "actualLocation"]),
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
    }
  },
  methods: {
    loadLocations() {
      if (this.actualCompany) this.getAllLocations(this.actualCompany.item.id);
    },
    copyFormValues(employee) {
      for (const [key] of Object.entries(employee)) {
        this[key] = employee[key];
      }
    },
    ...mapActions("location", {
      getAllLocations: "getAllLocations",
    }),
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
  },
  mounted() {
    this.loadLocations();
    //this.listaSedi.unshift(this.allLocations.items.id);
    
    EventBus.$on("EDIT_EMPLOYEE_FORM", (employee) => {
      this.copyFormValues(employee);
    });
    EventBus.$on("NEW_EMPLOYEE_FORM", () => {
      this.initEmployee();
    });
    EventBus.$on("CHECK_EMPLOYEE_FORM", () => {
      this.$v.$touch();
      if (this.$v.$invalid) {
        //generate event no
        EventBus.$emit("NO_EMPLOYEE_FORM");
      } else {
        //   generate event ok
        this.createEmployee();
        EventBus.$emit("OK_EMPLOYEE_FORM", this.employee);
        this.$v.$reset();
      }
    });
  },

  beforeDestroy() {
    EventBus.$off("CHECK_EMPLOYEE_FORM");
    EventBus.$off("NEW_EMPLOYEE_FORM");
    EventBus.$off("EDIT_EMPLOYEE_FORM");
  },
};
</script>
<style scoped></style>
