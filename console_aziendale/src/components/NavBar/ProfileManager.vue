<template>
  <div>
    <account-cog-icon @click="profileSetting = true" />
    <modal v-show="profileSetting">
      <template v-slot:header> Account</template>
      <template v-slot:body>
        <div class="mb-4 flex flex-wrap justify-between">
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
import Modal from "../Modal.vue";
import { mapState } from "vuex";

export default {
  name: "ProfileManager",
  components: { Modal },

  data: function () {
    return {
      profileSetting: false,
    };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
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
</style>
