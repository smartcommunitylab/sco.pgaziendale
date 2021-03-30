<template>
  <form action="" id="addEmployee">
    <div class="mb-20 flex flex-wrap justify-between">
      <div class="field-group mb-4 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.name.$error }">
          <label class="field-label" for="first_name">Nome </label>
          <input
            type="text"
            name="employeeName"
            placeholder="Nome *"
            v-model.trim="$v.name.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
            id="employeeName"
          />
        </div>
        <div v-if="$v.name.$error">
          <div class="error" v-if="!$v.name.required">Il campo nome e' richiesto.</div>
        </div>
      </div>
      <div class="field-group mb-4 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.surname.$error }">
          <label class="field-label" for="first_name">Cognome </label>
          <input
            type="text"
            name="employeeSurname"
            placeholder="Cognome *"
            v-model.trim="$v.surname.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
            id="employeeName"
          />
        </div>
        <div v-if="$v.surname.$error">
          <div class="error" v-if="!$v.surname.required">
            Il campo Cognome e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-4 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.code.$error }">
          <label class="field-label" for="first_name">Codice</label>
          <input
            type="text"
            name="employeeCode"
            id="employeeCode"
            placeholder="Codice *"
            v-model.trim="$v.code.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
          <info-box
            :msg="'Il codice univoco con cui viene identificato il dipendente a livello di gioco'"
          />
        </div>
        <div v-if="$v.code.$error">
          <div class="error" v-if="!$v.code.required">Il campo codice e' richiesto.</div>
        </div>
      </div>

      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.location.$error }">
          <label class="field-label" for="password">Sede </label>
          <input
            type="text"
            name="employeeLocation"
            id=""
            required
            placeholder="Sede *"
            v-model.trim="$v.location.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
          <info-box :msg="'Il codice delle sede associata al dipendente'" />
        </div>

        <div v-if="$v.location.$error">
          <div class="error" v-if="!$v.location.required">
            Il campo Sede e' richiesto.
          </div>
        </div>
      </div>
    </div>
  </form>
</template>
<script>
import { required } from "vuelidate/lib/validators";
import EventBus from "@/components/eventBus";
import InfoBox from "@/components/InfoBox.vue";

export default {
  components: {
    InfoBox,
  },
  data() {
    return {
      employee: {},
      name: "",
      surname: "",
      code: "",
      location: "",
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
  methods: {
    copyFormValues(employee) {
      for (const [key] of Object.entries(employee)) {
        this[key] = employee[key];
      }
    },
    initEmployee() {
      this.employee = {};
      this.id = "";
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
