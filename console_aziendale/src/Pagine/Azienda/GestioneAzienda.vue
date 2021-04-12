<template>
  <div>
    <div>
      Utenti
      <div
        v-if="
          adminCompanyUsers &&
          adminCompanyUsers.items &&
          adminCompanyUsers.items.length > 0
        "
      >
        <div v-for="user in adminCompanyUsers.items" v-bind:key="user.id">
          <div class="user">
            <div>
              <span>{{ user.name }}</span
              ><span> {{ user.surname }} </span><span>{{ user.username }} </span
              ><span v-for="role in user.roles" :key="JSON.stringify(role)"
                ><label v-if="role.role == 'ROLE_COMPANY_ADMIN'"
                  >AMMINISTRATORE AZIENDALE</label
                ><label v-if="role.role == 'ROLE_MOBILITY_MANAGER'"
                  >MOBILITY MANAGER</label
                ></span
              >
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
      // this.role = role;
      this.popup = {
        title: title,
      };
    },
    closeModal() {
      this.editModalVisible = false;
      this.$v.$reset();
    },

    saveUser() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      // console.log("submit!");
      EventBus.$emit("CHECK_USER_FORM");
      // console.log(this.roles);
      // this.$v.$touch();
      // if (this.$v.$invalid) {
      //   this.submitStatus = "ERROR";
      //   return;
      // } else {
      //   this.createUser();
      //   this.submitStatus = "SUCCESS";
      //   if (this.newUser) {
      //     this.addUserCall({ companyId: this.adminCompany.item.id, user: this.user });
      //   } else {
      //     this.updateUserCall({ companyId: this.adminCompany.item.id, user: this.user });
      //   }
      //   this.$v.$reset();
      // }
      // this.editModalVisible = false;
      // this.newUser = false;
    },
    editUser(user) {
      this.editModalVisible = true;
      this.user = user;
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
    this.changePage({ title: "Lista aziende", route: "/aziende" });
    // this.getAllCompanies();

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
  },
  beforeDestroy() {
    EventBus.$off("NO_USER_FORM");
    EventBus.$off("OK_USER_FORM");
    EventBus.$off("DELETE_USER");
    EventBus.$off("EDIT_USER");
  },
};
</script>

<style scoped>
.empty-row {
  text-align: center;
  font-size: large;
}
.user {
  height: 50px;
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
}
</style>
