<template>
    <modal>
      <template v-slot:header>Account</template>
      <template v-slot:body>
        <v-row
          justify="center"
          class="mb-8"
        >
          <v-col
          cols="4"
          >
            <div class="font-bold">Nome:</div>
            <div>{{ user.name }} {{ user.surname }}</div>
          </v-col>
          <v-col
          cols="4"
          >
            <div class="font-bold">Username:</div>
            <div>{{ user.username }}</div>
          </v-col>
          <v-col
          cols="4"
          >
              <div class="font-bold">Ruoli:</div>
              <div
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
          </v-col>
        </v-row>
        <v-divider> </v-divider>
        <v-row
          justify="center"
          class="mt-8"
        >
          <v-btn
            outlined
            color="primary"
            @click="openModal({type:'changePasswordModal', object:null})"
          >
            Cambia password
          </v-btn>
        </v-row>
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

export default {
    components: { Modal},
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