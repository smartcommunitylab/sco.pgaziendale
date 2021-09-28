<template>
    <modal>
      <template v-slot:header> Account</template>
      <template v-slot:body>
        <div class="mb-20 flex flex-wrap justify-between">
          <div class="field-group mb-4 w-full">
            <div class="name flex flex-row">
              <div class="font-bold">Nome:</div>
              <div class="w-full text-right">{{ user.name }} {{ user.surname }}</div>
            </div>
            <div class="username flex flex-row">
              <div class="font-bold">Username:</div>
              <div class="w-full text-right">{{ user.username }}</div>
            </div>
            <div class="role flex flex-row">
              <div class="font-bold">Ruoli:</div>
              <div class="w-full">
                <div
                  class="w-full text-right"
                  v-for="role in user.roles"
                  :key="JSON.stringify(role)"
                >
                  <label v-if="role.role == 'ROLE_COMPANY_ADMIN'"
                    >AMMINISTRATORE AZIENDALE</label
                  >
                    <label v-if="role.role == 'ROLE_MOBILITY_MANAGER'"
                    >MOBILITY MANAGER</label
                  >
                  <label v-if="role.role == 'ROLE_ADMIN'"
                    >AMMINISTRATORE DEL SISTEMA</label
                  >
                </div>
              </div>
            </div>
            <button
              type="button"
              class="btn-close"
              aria-label="Close modal"
              @click="changingPwd = !changingPwd"
            >
              Cambia password
            </button>
            <div v-if="changingPwd">
              <label class="pwd-label" for="first_name">Vecchia Password</label>
              <div class="relative">
                <input
                  :type="passwordFieldTypeFirst"
                  name="companyLogo"
                  id="companyLogo"
                  placeholder="Vecchia password *"
                  v-model="oldPassword"
                  class="block appearance-none w-full bg-white pwd-input border border-grey-light hover:border-grey px-2 py-2 rounded shadow"
                />
                <div
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm leading-5 pwd-input"
                >
                  <eye-off-icon
                    @click="switchVisibility('passwordFieldTypeFirst')"
                    :class="{
                      block: passwordFieldTypeFirst == 'password',
                      hidden: passwordFieldTypeFirst != 'text',
                    }"
                  />
                  <eye-icon
                    @click="switchVisibility('passwordFieldTypeFirst')"
                    :class="{
                      block: passwordFieldTypeFirst != 'password',
                      hidden: passwordFieldTypeFirst == 'text',
                    }"
                  />
                </div>
                <info-box :msg="'Inserisci un url che contiene il logo dell\'azienda'" />
              </div>
              <label class="pwd-label" for="first_name">Nuova Password</label>
              <div class="relative">
                <input
                  :type="passwordFieldTypeSecond"
                  name="companyLogo"
                  id="companyLogo"
                  placeholder="Nuova password *"
                  v-model="newPassword"
                  class="block appearance-none w-full bg-white pwd-input border border-grey-light hover:border-grey px-2 py-2 rounded shadow"
                />
                <div
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm leading-5 pwd-input"
                >
                  <eye-off-icon
                    @click="switchVisibility('passwordFieldTypeSecond')"
                    :class="{
                      block: passwordFieldTypeSecond == 'password',
                      hidden: passwordFieldTypeSecond != 'text',
                    }"
                  />
                  <eye-icon
                    @click="switchVisibility('passwordFieldTypeSecond')"
                    :class="{
                      block: passwordFieldTypeSecond != 'password',
                      hidden: passwordFieldTypeSecond == 'text',
                    }"
                  />
                </div>
              </div>
              <label class="pwd-label" for="first_name">Ripeti nuova Password</label>
              <div class="relative">
                <input
                  :type="passwordFieldTypeThird"
                  :class="{ 'password-different': passwordDifferent }"
                  name="companyLogo"
                  id="companyLogo"
                  placeholder="Nuova password *"
                  v-model="newPassword2"
                  class="block appearance-none w-full bg-white pwd-input border border-grey-light hover:border-grey px-2 py-2 rounded shadow"
                />
                <div
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm leading-5 pwd-input"
                >
                  <eye-off-icon
                    @click="switchVisibility('passwordFieldTypeThird')"
                    :class="{
                      block: passwordFieldTypeThird == 'password',
                      hidden: passwordFieldTypeThird != 'text',
                    }"
                  />
                  <eye-icon
                    @click="switchVisibility('passwordFieldTypeThird')"
                    :class="{
                      block: passwordFieldTypeThird != 'password',
                      hidden: passwordFieldTypeThird == 'text',
                    }"
                  />
                </div>
              </div>
              <div v-if="passwordDifferent">
                <div class="error">Le due password non coincidono</div>
              </div>
              <v-row>
                <v-spacer></v-spacer>
                <v-btn
                color="primary"
                text
                @click="changePwd()"
                class="py-8 ml-8"
                >
                Salva
                </v-btn>
              </v-row>
            </div>
          </div>
        </div>
      </template>

      <template v-slot:footer>
        <v-btn
        text
        @click="closeModal()"
        class="py-8 ml-8"
        >
        Chiudi
        </v-btn>
      </template>
    </modal>
</template>


<script>
import Modal from "@/components/Modal.vue";
import { mapActions, mapState } from "vuex";
import InfoBox from "@/components/InfoBox.vue";

export default {
    components: { Modal, InfoBox },
    data: function () {
    return {
      profileSetting: false,
      changingPwd: false,
      oldPassword: "",
      newPassword: "",
      newPassword2: "",
      passwordFieldTypeFirst: "password",
      passwordFieldTypeSecond: "password",
      passwordFieldTypeThird: "password",
      passwordDifferent: false,
    };
    },
    computed: {
        ...mapState("account", ["status", "user", "role"]),
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
}
</script>


<style>

</style>