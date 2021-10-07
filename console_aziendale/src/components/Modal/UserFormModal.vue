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
                        :rules="[rules.required]"
                        id="name"
                        v-model.trim="$v.name.$model"
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
                        :rules="[rules.required]"
                        id="surname"
                        v-model.trim="$v.surname.$model"
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
                        :rules="userRules"
                        id="username"
                        v-model.trim="$v.username.$model"
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
                        :rules="[rules.required]"
                        id="phone"
                        v-model.trim="$v.phone.$model"
                        outlined
                    ></v-text-field>
                    </v-col>
                    <v-col cols="12">
                    <v-form>
                        <p class="text-subtitle-1">Ruoli</p>
                        <v-checkbox
                        :rules="roleRules"
                        v-model="roles"
                        label="Amministratore Aziendale"
                        value="ROLE_COMPANY_ADMIN"
                        hide-details
                        ></v-checkbox>
                        <v-checkbox
                        :rules="roleRules"
                        v-model="roles"
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
                @click="closeModal()"
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
import { required, email } from "vuelidate/lib/validators";
import EventBus from "@/components/eventBus";
import { mapActions, mapState } from "vuex";
import Modal from "@/components/Modal.vue";

export default {
  components: {
      "modal": Modal,
  },
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
      rules: {
          required: value => !!value || 'Campo richiesto.',
      },
      provinceRules: [
        value => this.isInListaProvince(value) || 'Campo richiesto.'
      ],
      userRules: [
          v => !!v || 'Campo richiesto.', 
          v => this.validateEmail(v) || 'Il campo username non risulta valido.',
          v => this.isUsernameUnique(v) || 'Questo username risulta giá registrato.'
      ],
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
    validateEmail(email) 
    {
        var re = /\S+@\S+\.\S+/;
        return re.test(email);
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
    roleRules() {
      return [
        this.roles.length > 0 || "Seleziona almeno un ruolo."
      ];
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
    this.setModalData();
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
