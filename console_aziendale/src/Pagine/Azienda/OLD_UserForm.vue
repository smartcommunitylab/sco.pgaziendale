<template>
  <form action="" id="addUser">
    <div class="mb-20 flex flex-wrap justify-between">
      <div class="field-group mb-4 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.name.$error }">
          <label class="field-label" for="first_name">Nome </label>
          <input
            type="text"
            name="name"
            placeholder="Nome *"
            v-model.trim="$v.name.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
            id="name"
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
            name="surname"
            placeholder="Cognome *"
            v-model.trim="$v.surname.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
            id="surname"
          />
        </div>
        <div v-if="$v.surname.$error">
          <div class="error" v-if="!$v.surname.required">
            Il campo cognome e' richiesto.
          </div>
        </div>
      </div>

      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.username.$error }">
          <label class="field-label" for="password">Username </label>
          <input
            type="text"
            name="username"
            id=""
            required
            placeholder="Username *"
            v-model.trim="$v.username.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
          <info-box :msg="'Il campo username é deve essere un\'email valida'" />
        </div>

        <div v-if="$v.username.$error">
          <div class="error" v-if="!$v.username.required">
            Il campo username e' richiesto.
          </div>
          <div class="error" v-if="!$v.username.email">
            Il campo username non risulta valido.
          </div>
          <div class="error" v-if="!$v.username.isUnique">
            Questo username risulta giá registrato
          </div>
        </div>
      </div>
      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.phone.$error }">
          <label class="field-label" for="phone">Telefono </label>
          <input
            type="text"
            name="phone"
            id=""
            required
            placeholder="Telefono *"
            v-model.trim="$v.phone.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
        </div>
        <div v-if="$v.phone.$error">
          <div class="error" v-if="!$v.phone.required">
            Il campo telefono e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.roles.$error }">
          <label class="field-label" for="password">Ruoli </label>
          <input type="checkbox" id="aa" value="ROLE_COMPANY_ADMIN" v-model="roles" />
          <label for="aa">Amministratore Aziendale</label>
          <!-- <input type="checkbox" id="mm" value="ROLE_MOBILITY_MANAGER" v-model="roles" />
          <label for="mm">Mobility Manager</label> -->
        </div>
        <div v-if="$v.roles.$error">
          <div class="error" v-if="!$v.roles.required">Il campo ruoli e' richiesto.</div>
        </div>
      </div>
    </div>
  </form>
</template>
<script>
import { required, email } from "vuelidate/lib/validators";
import EventBus from "@/components/eventBus";
import { mapState } from "vuex";
import InfoBox from "@/components/InfoBox.vue";

export default {
  components: { InfoBox },
  data() {
    return {
      user: null,
      id: "",
      playerId: "",
      resetDate: "",
      activated: true,
      name: "",
      surname: "",
      username: "",
      phone: "",
      roles: [],
      unique: true,
    };
  },
  validations: {
    name: {
      required,
    },
    surname: {
      required,
    },
    username: {
      required,
      email,
      isUnique(value) {
        console.log(value);
        //check user is present
        if (
          this.id == null && this.adminCompanyUsers.items.find((ele) => {
            console.log(ele.username == this.username);
            return ele.username == this.username;
          })
        ) {
          return false;
        }
        return true;
      },
    },
    phone: {
      required,
    },
    roles: {
      required,
    },
  },
  methods: {
    copyValues(user) {
      for (const [key] of Object.entries(user)) {
        if (key != "roles") this[key] = user[key];
        else {
          for (var i = 0; i < user["roles"].length; i++) {
            if (user["roles"][i].companyId == this.adminCompany.item.id)
              this["roles"].push(user["roles"][i].role);
          }
        }
      }
    },
    initUser() {
      //clean form
      this.user = {};
      this.playerId = "";
      this.resetDate = "";
      this.activated = true;
      this.name = "";
      this.surname = "";
      this.username = "";
      this.phone = "";
      this.roles = [];
    },
    createUser() {
      this.user = {
        id: this.id ? this.id : null,
        playerId: this.playerId,
        activated: this.activated,
        resetDate: this.resetDate,
        name: this.name,
        surname: this.surname,
        username: this.username,
        phone: this.phone,
        roles: this.roles.map((elem) => {
          return {
            companyId: this.adminCompany.item.id,
            locations: null,
            role: elem,
            subscriptions: null,
          };
        }),
      };
    },
  },
  computed: {
    ...mapState("company", ["adminCompany"]),
    ...mapState("company", ["adminCompany", "adminCompanyUsers"]),
  },
  mounted() {
    EventBus.$on("EDIT_USER_FORM", (user) => {
      this.copyValues(user);
    });
    EventBus.$on("NEW_USER_FORM", () => {
      this.initUser();
    });
    EventBus.$on("CHECK_USER_FORM", () => {
      this.$v.$touch();
      if (this.$v.$invalid) {
        //generate event no
        EventBus.$emit("NO_USER_FORM");
      } else {
        //   generate event ok
        this.createUser();
        this.unique = true;
        EventBus.$emit("OK_USER_FORM", this.user);
        this.$v.$reset();
      }
    });
  },

  beforeDestroy() {
    EventBus.$off("CHECK_USER_FORM");
    EventBus.$off("NEW_USER_FORM");
    EventBus.$off("EDIT_USER_FORM");
  },
};
</script>
<style scoped></style>