<template>
    <modal>
      <template v-slot:header>Cambia Password Account</template>
      <template v-slot:body>
        <v-row>
            <v-col
            cols="12"
            >
                <v-text-field
                    :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show1 ? 'text' : 'password'"
                    :rules="[rules.required]"
                    label="Vecchia Password"
                    placeholder="Vecchia Password*"
                    v-model="oldPassword"
                    name="passwordOld"
                    id="passwordOld"
                    outlined
                    hide-details
                    @click:append="show1 = !show1"
                  ></v-text-field>
            </v-col>
            <v-col
            cols="12"
            >
                <v-text-field
                    :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show2 ? 'text' : 'password'"
                    :rules="[rules.required]"
                    label="Nuova Password"
                    placeholder="Nuova Password*"
                    v-model="newPassword"
                    name="passwordNew"
                    id="passwordNew"
                    outlined
                    hide-details
                    @click:append="show2 = !show2"
                  ></v-text-field>
            </v-col>
            <v-col
            cols="12"
            >
                <v-text-field
                    :append-icon="show3 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show3 ? 'text' : 'password'"
                    :rules="[rules.required]"
                    label="Ripeti Nuova Password"
                    placeholder="Ripeti Nuova Password*"
                    v-model="newPassword2"
                    name="passwordNew2"
                    id="passwordNew2"
                    outlined
                    hide-details
                    @click:append="show3 = !show3"
                  ></v-text-field>
            </v-col>
            <v-col
                cols="12"
                v-if="passwordDifferent"
            >
                <div class="errore">Le due password non coincidono</div>
            </v-col>
        </v-row>
      </template>

      <template v-slot:footer>
        <v-btn
        text
        @click="openModal({type:'profileSetting', object:null})"
        class="py-8 ml-8"
        >
        Annulla
        </v-btn>
        <v-btn
        color="primary"
        text
        @click="changePwd()"
        class="py-8 ml-8"
        >
            Salva
        </v-btn>
      </template>
    </modal>
</template>


<script>
import Modal from "@/components/modal/ModalStructure.vue";
import { mapActions, mapState } from "vuex";

export default {
    components: {Modal},

    data: function () {
      return {
        profileSetting: false,
        oldPassword: "",
        newPassword: "",
        newPassword2: "",
        passwordFieldTypeFirst: "password",
        passwordFieldTypeSecond: "password",
        passwordFieldTypeThird: "password",
        passwordDifferent: false,
        show1: false,
        show2: false,
        show3: false,
        rules: {
            required: value => !!value || 'Campo richiesto.',
        },
      };
    },

    methods: {
      ...mapActions("modal", {openModal:"openModal", closeModal:"closeModal"}),
      ...mapActions("account", ["changePassword"]),

      changePwd() {
      //check if equal
      if (this.newPassword === this.newPassword2) {
          this.passwordDifferent = false;
          // visible edit pwd
          this.changePassword({
          oldPassword: this.oldPassword,
          newPassword: this.newPassword,
          });
      } else this.passwordDifferent = true;
      },
      switchVisibility(id) {
      this[id] = this[id] === "password" ? "text" : "password";
      },
    },

    computed: {
      ...mapState("account", ["status", "user"]),
    },
}
</script>

<style>
.errore {
  color: red;
  background: transparent;
  text-align: center;
}
</style>