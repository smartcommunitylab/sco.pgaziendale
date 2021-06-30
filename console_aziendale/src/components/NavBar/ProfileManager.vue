<template>
  <div>
    <account-cog-icon @click="profileSetting = true" />
    <modal v-show="profileSetting">
      <template v-slot:header > <div class="text-primary">Account</div></template>
      <template v-slot:body> 
        <div class="mx-auto text-center grid-cols-2">
        <div class="flex justify-center">
          <div class="field-group mb-4">
            <div class="name flex-row">
              <div class="font-bold">Nome:</div>
              <div class="ml-2">{{ user.name }} {{ user.surname }}</div>
            </div>
            <br>
            <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
          <br>
            <div class="username flex-row">
              <div class="font-bold">Username:</div>
              <div class="ml-2">{{ user.username }}</div>
            </div>
            <br>
            <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
          <br>
            <div class="role  flex-row">
              <div class="font-bold">Ruoli:</div>
                <div
                  class="ml-2"
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
          <br>
            <div
            class="mx-auto lg:mr-12 w-full border-b-2 border-secondary opacity-80"
          ></div>
          <br>
            <button
              type="button"
              class="w-64 btn-close text-xs font-medium mt-2 mb-2 mx-2 inline-block px-6 py-2 leading-6 text-center text-white transition bg-primary rounded ripple uppercase hover:bg-primary_light hover:shadow-lg focus:outline-none"
              aria-label="Close modal"
              @click="changingPwd = !changingPwd"
            >
              Cambia password
            </button>
            <div v-if="changingPwd">
              <label class="pwd-label" for="first_name"><b>Vecchia Password</b></label>
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
              <label class="pwd-label" for="first_name"><b>Nuova Password</b></label>
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
              <label class="pwd-label" for="first_name"><b>Ripeti nuova Password</b></label>
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
              <button
                type="button"
                class="btn-close text-xs font-medium mt-2 mb-2 mx-2 inline-block px-6 py-2 leading-6 text-center text-white transition bg-primary rounded ripple uppercase hover:bg-primary_light hover:shadow-lg focus:outline-none"
                aria-label="Close modal"
                @click="changePwd()"
              >
                Salva
              </button>
            </div>
            </div>
          </div>
        </div>
      </template>

      <template v-slot:footer>
        <button
          type="button"
          class="mt-2 mb-2 btn-close text-xs font-medium mx-2 inline-block px-6 py-2 leading-6 text-center text-white transition bg-danger rounded ripple uppercase hover:bg-danger_light hover:shadow-lg focus:outline-none"
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
      passwordDifferent: false,
    };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
  },
  methods: {
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
.password-different {
  /* color: red; */
  animation: shake 0.82s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}
.error {
  color: red;
  text-align: center;
}
</style>
