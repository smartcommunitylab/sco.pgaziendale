<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          class="fab"
          fab
          color="cyan accent-2"
          @click="showModal('Aggiungi utente')"
        >
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" class="pb-0">
        <div class="text-h6 mb-0">Utenti</div>
      </v-col>
      <v-col
        v-if="
          adminCompanyUsers &&
          adminCompanyUsers.items &&
          adminCompanyUsers.items.length > 0
        "
      >
        <v-row>
          <v-col cols="4" v-for="user in adminCompanyUsers.items" v-bind:key="user.id">
            <v-card>
            <v-card-title>{{user.name + " " + user.surname}}</v-card-title>
            <v-card-subtitle>Username: {{ user.username }}</v-card-subtitle>
            <v-card-text>
              <p class="font-weight-bold mb-0">Ruoli:</p>
              <div v-for="role in user.roles" :key="JSON.stringify(role)">
                <label
                  class="mr-2"
                  v-if="
                    role.role == 'ROLE_COMPANY_ADMIN' &&
                    role.companyId == adminCompany.item.id
                  "
                  >AMMINISTRATORE AZIENDALE
                </label>
                <label
                  class="mr-2"
                  v-if="
                    role.role == 'ROLE_MOBILITY_MANAGER' &&
                    role.companyId == adminCompany.item.id
                  "
                  >MOBILITY MANAGER
                </label>
              </div>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn icon @click="editUser(user)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon @click="deleteUser(user)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      
      <!-- MODALE AGGIUNTA DIPENDENTE -->
      <modal v-show="editModalVisible">
        <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
          <user-form />
        </template>
        <template v-slot:footer>
          <button
            type="button"
            class="btn-close"
            @click="saveUser"
            aria-label="Close modal"
          >
            Salva
          </button>
          <button
            type="button"
            class="btn-close"
            @click="closeModal"
            aria-label="Close modal"
          >
            Annulla
          </button>
          <p class="typo__p" v-if="submitStatus === 'ERROR'">
            Riempire i dati nel modo corretto
          </p>
        </template>
      </modal>
      <modal v-show="deleteModalVisible">
        <template v-slot:header> Cancella Utente </template>
        <template v-slot:body>
          <span>Sei sicuro di voler cancellare l'utente selezionato?</span>
        </template>
        <template v-slot:footer>
          <button
            type="button"
            class="btn-close"
            @click="deleteConfirm"
            aria-label="Close modal"
          >
            Conferma
          </button>
          <button
            type="button"
            class="btn-close"
            @click="closeDeleteModal"
            aria-label="Close modal"
          >
            Annulla
          </button>
        </template>
      </modal>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import EventBus from "@/components/eventBus";
import Modal from "@/components/Modal.vue";
import UserForm from "./UserForm.vue";

export default {
  name: "GestioneAzienda",
  components: { Modal, UserForm },
  data() {
    return {
      submitStatus: null,
      company: null,
      editModalVisible: false,
      deleteModalVisible: false,
      newUser: true,
      popup: {
        title: "",
      },
    };
  },
  computed: {
    ...mapState("company", ["adminCompany", "adminCompanyUsers"]),
    ...mapState("campaign", ["allCampaigns"]),
  },

  methods: {
    ...mapActions("company", {
      getCompanyById: "getCompanyById",
      getUsers: "getUsers",
      addUserCall: "addUser",
      updateUserCall: "updateUser",
      deleteUserCall: "deleteUser",
      getAllCompanies: "getAll",
    }),
    ...mapActions("campaign", { getAllCampaigns: "getAll" }),
    ...mapActions("navigation", { changePage: "changePage" }),
    showModal(title) {
      this.editModalVisible = true;
      this.newUser = true;
      EventBus.$emit("NEW_USER_FORM");
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.editModalVisible = false;
    },

    saveUser() {
      EventBus.$emit("CHECK_USER_FORM");
    },
    editUser(user) {
      this.editModalVisible = true;
      this.user = user;
      this.newUser = false;
      EventBus.$emit("EDIT_USER_FORM", user);
      this.popup = {
        title: "Modifica",
      };
    },

    deleteUser(user) {
      this.deleteModalVisible = true;
      this.user = user;
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteUserCall({
        companyId: this.adminCompany.item.id,
        user: this.user,
      });
    },
    closeDeleteModal() {
      this.deleteModalVisible = false;
    },
  },

  mounted() {
    if (this.adminCompany) {
      this.getUsers(this.adminCompany.item);
      this.getAllCampaigns(this.adminCompany.item.id);
    }
    EventBus.$on("OK_USER_FORM", (user) => {
      if (this.newUser) {
        this.addUserCall({ companyId: this.adminCompany.item.id, user: user });
      } else {
        this.updateUserCall({ companyId: this.adminCompany.item.id, user: user });
      }
      this.editModalVisible = false;
      this.newUser = false;
    });
    EventBus.$on("NO_USER_FORM", () => {
      this.submitStatus = "ERROR";
    });
    // EventBus.$on("USER_EXISTS", () => {
    //   this.submitStatus = "ERROR";
    // });
  },
  beforeDestroy() {
    EventBus.$off("NO_USER_FORM");
    EventBus.$off("OK_USER_FORM");
    EventBus.$off("DELETE_USER");
    EventBus.$off("EDIT_USER");
    // EventBus.$off("USER_EXISTS");
  },
};
</script>

<style scoped>
.empty-row {
  text-align: center;
  font-size: large;
}
.user {
  height: 100%;
  border: solid 1px;
  line-height: 50px;
  padding: 8px;
  border-radius: 8px;
  margin: 8px;
  display: flex;
  align-items: center;
}
.buttons {
  margin-left: auto;
  min-width: 130px;
}
.title-header {
  font-weight: bold;
  margin: 0px 8px;
}
</style>
