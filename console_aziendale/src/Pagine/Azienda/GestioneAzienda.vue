<template>
  <div>
    <div>
      <div class="text-center">UTENTI</div>
      <div
        v-if="
          adminCompanyUsers &&
          adminCompanyUsers.items &&
          adminCompanyUsers.items.length > 0
        "
      >
        <div v-for="user in adminCompanyUsers.items" v-bind:key="user.id">
          <div class="user">
            <div class="w-full">
              <div class="flex">
                <div ><span class="title-header" v-if="user.name"> Nome: </span><span>{{ user.name }}</span></div>
                <div ><span class="title-header" v-if="user.surname"> Cognome: </span><span>{{ user.surname }}</span></div>
                <div ><span class="title-header" v-if="user.phone"> Telefono: </span><span>{{ user.phone }}</span></div>
              </div>
              <div class="flex">
                                <div ><span class="title-header"> Username:</span><span>{{ user.username }}</span></div>
              </div>
              <div class="flex">
                <div class="title-header">Ruoli:</div>
                <div v-for="role in user.roles" :key="JSON.stringify(role)">
                  <label
                    class="mr-2"
                    v-if="
                      role.role == 'ROLE_COMPANY_ADMIN' &&
                      role.companyId == adminCompany.item.id
                    "
                    >AMMINISTRATORE AZIENDALE</label
                  >
                  <label
                    class="mr-2"
                    v-if="
                      role.role == 'ROLE_MOBILITY_MANAGER' &&
                      role.companyId == adminCompany.item.id
                    "
                    >MOBILITY MANAGER</label
                  >
                </div>
              </div>
            </div>
            <div class="buttons">
              <button
                @click="deleteUser(user)"
                class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
              >
                <delete-icon />
              </button>
              <button
                @click="editUser(user)"
                class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
              >
                <pencil-outline-icon />
              </button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty-row">Non sono presenti elementi</div>

      <div class="ml-auto pt-4 pr-4">
        <button
          @click="showModal('Aggiungi utente')"
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <add-icon class="add-icon" />
        </button>
      </div>
    </div>

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
  </div>
</template>
<script>
import { mapState, mapActions } from "vuex";
import Modal from "@/components/Modal.vue";
import UserForm from "./UserForm.vue";
import EventBus from "@/components/eventBus";

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
