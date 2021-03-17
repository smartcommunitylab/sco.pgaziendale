<template>
  <div>
    <account-cog-icon @click="profileSetting = true" />
    <modal v-show="profileSetting">
      <template v-slot:header> Account</template>
      <template v-slot:body>
        <div class="mb-20 flex flex-wrap justify-between">
          <div class="field-group mb-4 w-full">
            <div class="name">Nome: {{ user.name }} {{ user.surname }}</div>
            <div class="username">Nome utente: {{ user.username }}</div>
            <div class="role"> Ruoli:
              <span v-for="role in user.roles" :key="JSON.stringify(role)"
                ><label v-if="role.role == 'ROLE_COMPANY_ADMIN'"
                  >AMMINISTRATORE AZIENDALE</label
                ><label v-if="role.role == 'ROLE_MOBILITY_MANAGER'"
                  >MOBILITY MANAGER</label
                ><label v-if="role.role == 'ROLE_ADMIN'"
                  >AMMINISTRATORE DEL SISTEMA</label
                ></span
              >
            </div>
        <button  type="button"
          class="btn-close"
          aria-label="Close modal"
           @click="changingPwd=!changingPwd">
          Cambia password
        </button>
        <div v-if="changingPwd"> 
                <label class="pwd-label" for="first_name">Vecchia Password</label>
                <input
                  type="password"
                  name="companyLogo"
                  id="companyLogo"
                  placeholder="Vecchia password *"
  v-model="oldPassword"
                    class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2 pwd-input"
                />
                <label class="pwd-label" for="first_name">Nuova Password</label>
                <input
                  type="password"
                  name="companyLogo"
                  id="companyLogo"
                  placeholder="Nuova password *"
  v-model="newPassword"
                    class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2 pwd-input"
                />
                                <label class="pwd-label" for="first_name">Ripeti nuova Password</label>
                <input
                  type="password"
                  name="companyLogo"
                  id="companyLogo"
                  placeholder="Nuova password *"
                  v-model="newPassword2"
                    class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2 pwd-input"
                />

        </div>
          </div>
        </div>
      </template>

      <template v-slot:footer>
          <button  type="button"
          class="btn-close"
          aria-label="Close modal"
           @click="changePwd()">
          Salve
        </button>
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
      changingPwd:false,
      oldPassword:"",
      newPassword:"",
      newPassword2:""
    };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
  },
  methods: {
    ...mapActions("account",["changePassword"]),
    changePwd() {
      // visible edit pwd
      this.changePassword({oldPassword:this.oldPassword,newPassword:this.newPassword});
    }
    
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
.pwd-label{
    display: inline-block;
    width: 40%;
    color: black;
}
.pwd-input{
  color:black;
}
</style>
