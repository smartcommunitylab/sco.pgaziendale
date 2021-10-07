<template>
    <modal>
        <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
            <form action="" id="addUser">
                <div class="mb-20 flex flex-wrap justify-between">
                <v-row>
                    <v-col
                    cols="6"
                    >
                    <v-text-field
                        label="Nome"
                        placeholder="Nome *"
                        type="text"
                        name="name"
                        id="name"
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
                        name="surname"
                        id="surname"
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
                        label="Username"
                        placeholder="Username *"
                        type="text"
                        name="username"
                        id="username"
                        v-model.trim="$v.username.$model"
                        :error-messages="usernameErrors"                                
                        required
                        @input="$v.username.$touch()"
                        @blur="$v.username.$touch()"
                        outlined
                    >
                        <template v-slot:append>
                        <v-tooltip
                            right
                            nudge-right="10px"
                        >
                            <template v-slot:activator="{ on }">
                            <v-icon v-on="on">
                                mdi-help-circle-outline
                            </v-icon>
                            </template>
                            Il campo username é deve essere un'email valida
                        </v-tooltip>
                        </template>
                    </v-text-field>
                    </v-col>
                    <v-col
                    cols="6"
                    >
                    <v-text-field
                        label="Telefono"
                        placeholder="Telefono *"
                        type="text"
                        name="phone"
                        id="phone"
                        v-model.trim="$v.phone.$model"
                        :error-messages="phoneErrors"                                
                        required
                        @input="$v.phone.$touch()"
                        @blur="$v.phone.$touch()"
                        outlined
                    ></v-text-field>
                    </v-col>
                    <v-col cols="12">
                    <v-form>
                        <p class="text-subtitle-1">Ruoli</p>
                        <v-checkbox
                        :error-messages="roleErrors"                                
                        required
                        @input="$v.roles.$touch()"
                        @blur="$v.roles.$touch()"
                        v-model="$v.roles.$model"
                        label="Amministratore Aziendale"
                        value="ROLE_COMPANY_ADMIN"
                        hide-details
                        ></v-checkbox>
                        <v-checkbox
                        :error-messages="roleErrors"                                
                        required
                        @input="$v.roles.$touch()"
                        @blur="$v.roles.$touch()"
                        v-model="$v.roles.$model"
                        label="Mobility Manager"
                        value="ROLE_MOBILITY_MANAGER"
                        ></v-checkbox>
                    </v-form>
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
                @click="save"
                class="py-8 ml-8"
            >
                Salva
            </v-btn>
        </template>
    </modal>
</template>


<script>
import { validationMixin } from 'vuelidate';
import { required, email } from "vuelidate/lib/validators";
import EventBus from "@/components/eventBus";
import { mapActions, mapState } from "vuex";
import Modal from "@/components/Modal.vue";

export default {
  components: {
      "modal": Modal,
  },
  mixins: [validationMixin],
  props: {
      typeCall: String,
  },
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
      roles: ['ROLE_COMPANY_ADMIN'],
      unique: true,
      popup: {
        title: "",
      },
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
    ...mapActions("modal", {closeModal: "closeModal"}),
    ...mapActions("company", {addUser: "addUser", updateUser:"updateUser"}),
    setModalData(){
        if(this.typeCall == "add"){
            this.initUser();
            this.popup.title = "Aggiungi Utente";
        }else if(this.typeCall == "edit"){
            this.copyValues(this.object);
            this.popup.title = "Modifica Utente";
        }
    },
    closeThisModal(){
        this.$v.$reset();
        this.closeModal();
    },
    save(){
        this.$v.$touch();
        if (this.$v.$invalid) {
            //INVALID data in form
            console.log("CAMPI INVALIDI");
        } else {
            //   generate event ok
            this.createUser();
            this.unique = true;
            if (this.typeCall == "add") {
                console.log("ADD");
                this.addUser({ companyId: this.adminCompany.item.id, user: this.user });
            } else if (this.typeCall == "edit") {
                console.log("EDIT");
                this.updateUser({ companyId: this.adminCompany.item.id, user: this.user });
            }
            this.$v.$reset();
            this.setModalData();
            this.closeModal();
        }
    },
    isUsernameUnique(value) {
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
      this.id = "";
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
    ...mapState("modal", ["type","object"]),
    ...mapState("company", ["adminCompany"]),
    ...mapState("company", ["adminCompany", "adminCompanyUsers"]),
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
    usernameErrors () {
        const errors = []
        if (!this.$v.username.$dirty) return errors
        !this.$v.username.required && errors.push('Campo richiesto.')
        return errors
    },
    phoneErrors () {
        const errors = []
        if (!this.$v.phone.$dirty) return errors
        !this.$v.phone.required && errors.push('Campo richiesto.')
        return errors
    },
    roleErrors () {
        const errors = []
        if (!this.$v.roles.$dirty) return errors
        !this.$v.roles.required && errors.push('Seleziona almeno un ruolo.')
        return errors
    },
        
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
  created() {
    this.initUser();
    this.popup.title = "Aggiungi Utente";
  },
  watch: {
    typeCall: function(){
        console.log("WATCH - setModalData");
        this.setModalData();
    },
    object: function(){
        console.log("WATCH - object cambiato setModalData");
        this.setModalData();
    },
  },
  beforeDestroy() {
    EventBus.$off("CHECK_USER_FORM");
    EventBus.$off("NEW_USER_FORM");
    EventBus.$off("EDIT_USER_FORM");
  },
};
</script>
<style scoped></style>
