<template>
  <div>
    <account-cog-icon @click="profileSetting = true" />
    <modal v-show="profileSetting">
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
              <div class="w-full text-right" v-for="role in user.roles" :key="JSON.stringify(role)"
                ><label v-if="role.role == 'ROLE_COMPANY_ADMIN'"
                  >AMMINISTRATORE AZIENDALE</label
                ><label v-if="role.role == 'ROLE_MOBILITY_MANAGER'"
                  >MOBILITY MANAGER</label
                ><label v-if="role.role == 'ROLE_ADMIN'"
                  >AMMINISTRATORE DEL SISTEMA</label
                ></div
              >
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
              <button
                type="button"
                class="btn-close"
                aria-label="Close modal"
                @click="changePwd()"
              >
                Salva
              </button>
            </div>
          </div>
        </div>
      </template>

      <template v-slot:footer>
        <button
          type="button"
          class="btn-close"
          aria-label="Close modal"
          @click="profileSetting = false"
        >
          Chiudi
        </button>
      </template>
    </modal>
  </div>
</template>
<script>
import Modal from "@/components/Modal.vue";
import { mapActions, mapState } from "vuex";

export default {
  name: "ProfileManager",
  components: { Modal },

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
    };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
  },
  methods: {
    ...mapActions("account", ["changePassword"]),
    changePwd() {
      // visible edit pwd
      this.changePassword({
        oldPassword: this.oldPassword,
        newPassword: this.newPassword,
      });
    },
    switchVisibility(id) {
      this[id] = this[id] === "password" ? "text" : "password";
    },
  },
};
</script>
<style scoped>
.name {
  color: black;
}
.username {
  color: black;
}
.role {
  color: black;
}
.pwd-label {
  display: inline-block;
  text-align: center;
  width: 100%;
  color: black;
  margin-top: 10px;
}
.pwd-input {
  color: black;
}
</style>
