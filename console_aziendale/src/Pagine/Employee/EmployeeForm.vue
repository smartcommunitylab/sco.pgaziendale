<template>
  <form action="" id="addEmployee">
    <div class="mx-auto text-center lg:gap-x-0 lg:mx-auto lg:text-center md:grid grid-cols-2 md:gap-x-3">
      <div class="field-group mb-4 mx-12">
        <div class="form-group" :class="{ 'form-group--error': $v.name.$error }">
          <label class="field-label" for="first_name"><b>Nome</b> </label>
          <input
            type="text"
            name="employeeName"
            placeholder="Nome *"
            v-model.trim="$v.name.$model"
            class="focus:border-blue-600 border-2 p-2 mb-3 flex-1 mr-2"
            id="employeeName"
          />
        </div>
        <div v-if="$v.name.$error">
          <div class="error" v-if="!$v.name.required">Il campo nome e' richiesto.</div>
        </div>
      </div>
      <div class="field-group mb-4 mx-12">
        <div class="form-group" :class="{ 'form-group--error': $v.surname.$error }">
          <label class="field-label" for="first_name"><b>Cognome</b> </label>
          <input
            type="text"
            name="employeeSurname"
            placeholder="Cognome *"
            v-model.trim="$v.surname.$model"
            class="focus:border-blue-600 border-2 p-2 mb-3 flex-1 mr-2"
            id="employeeName"
          />
        </div>
        <div v-if="$v.surname.$error">
          <div class="error" v-if="!$v.surname.required">
            Il campo Cognome e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-4 mx-12">
        <div class="form-group" :class="{ 'form-group--error': $v.code.$error }">
          <label class="field-label" for="first_name"><b>Codice</b></label>
          <input
            type="text"
            name="employeeCode"
            id="employeeCode"
            placeholder="Codice *"
            v-model.trim="$v.code.$model"
            class="focus:border-blue-600 border-2 p-2 mb-3 flex-1 mr-2"
          />
          <info-box
            :msg="'Il codice univoco con cui viene identificato il dipendente a livello di gioco'"
          />
        </div>
        <div v-if="$v.code.$error">
          <div class="error" v-if="!$v.code.required">Il campo codice e' richiesto.</div>
        </div>
      </div>

      <div class="field-group mb-4 mx-12">
        <div
          :class="{ 'form-group--error': $v.location.$error }"
        >
          <label class="field-label" for="password"><b>Sede</b> </label>
          <select required
            class="focus:border-blue-600 border-2 p-2 flex-none mr-2 w-48"
            v-model.trim="$v.location.$model"
          >
            <option v-for="loc in allLocations.items" :key="loc.id" :value="loc.id">
              {{ loc.id }}
            </option>
          </select>
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
import { mapState, mapActions } from "vuex";

export default {
  components: {
  },
  data() {
    return {
      employee: {},
      name: "",
      surname: "",
      code: "",
      location: "",
      locations: [],
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