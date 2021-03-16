<template>
  <div>
    <div>
      Utenti
      <div v-if="adminCompany.item.users && adminCompany.item.users.length > 0">
        <div v-for="user in adminCompany.item.users" v-bind:key="user.id">
          <div>
            {{ user.name }} {{ user.surname }} {{ user.username }} <span v-for="role in user.roles" :key="JSON.stringify(role)"><label v-if=" role.role=='ROLE_COMPANY_ADMIN'">AMMINISTRATORE AZIENDALE</label><label v-if=" role.role=='ROLE_MOBILITY_MANAGER'">MOBILITY MANAGER</label></span>
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
        <form action="" id="addUser">
          <div class="mb-20 flex flex-wrap justify-between">
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.name.$error }">
                <label class="field-label" for="first_name">Nome </label>
                <input
                  type="text"
                  name="name"
                  placeholder="Nome *"
                  v-model.trim="$v.name.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="name"
                />
              </div>
              <div v-if="$v.name.$error">
                <div class="error" v-if="!$v.name.required">
                  Il campo nome e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.surname.$error }">
                <label class="field-label" for="first_name">Cognome </label>
                <input
                  type="text"
                  name="surname"
                  placeholder="Cognome *"
                  v-model.trim="$v.surname.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="surname"
                />
              </div>
              <div v-if="$v.surname.$error">
                <div class="error" v-if="!$v.surname.required">
                  Il campo cognome e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.username.$error }"
              >
                <label class="field-label" for="password">Username </label>
                <input
                  type="text"
                  name="username"
                  id=""
                  required
                  placeholder="Username *"
                  v-model.trim="$v.username.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.username.$error">
                <div class="error" v-if="!$v.username.required">
                  Il campo username e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.phone.$error }"
              >
                <label class="field-label" for="phone">Telefono </label>
                <input
                  type="text"
                  name="phone"
                  id=""
                  required
                  placeholder="Telefono *"
                  v-model.trim="$v.phone.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.phone.$error">
                <div class="error" v-if="!$v.phone.required">
                  Il campo telefono e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.roles.$error }">
                <label class="field-label" for="password">Ruoli </label>
                <input
                  type="checkbox"
                  id="aa"
                  value="ROLE_COMPANY_ADMIN"
                  v-model="roles"
                />
                <label for="aa">Amministratore Aziendale</label>
                <input
                  type="checkbox"
                  id="mm"
                  value="ROLE_MOBILITY_MANAGER"
                  v-model="roles"
                />
                <label for="mm">Mobility Manager</label>
                <!-- <input
                  type="text"
                  name="role"
                  id=""
                  required
                  placeholder="Ruoli *"
                  v-model.trim="$v.roles.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                /> -->
              </div>
              <div v-if="$v.roles.$error">
                <div class="error" v-if="!$v.username.required">
                  Il campo ruoli e' richiesto.
                </div>
              </div>
            </div>
          </div>
        </form>
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
  </div>
</template>
<script>
import { mapState, mapActions } from "vuex";
import { required } from "vuelidate/lib/validators";
import Modal from "@/components/Modal.vue";

export default {
  name: "GestioneAzienda",
  components: { Modal },
  data() {
    return {
      submitStatus: null,
      company: null,
      editModalVisible: false,
      // role: null,
      name: "",
      surname: "",
      mail: "",
      phone: "",
      popup: {
        title: "",
      },
      roles: [],
    };
  },
  computed: {
    ...mapState("company", ["adminCompany"]),
    ...mapState("campaign", ["allCampaigns"]),
  },
  validations: {
    name: {
      required,
    },
    surname: {
      required,
    },
    username: {
      required,
    },
    phone: {
      required,
    },
    roles: {
      required,
    },
  },
  methods: {
    ...mapActions("company", {
      getCompanyById: "getCompanyById",
      getUsers: "getUsers",
      addUserCall: "addUser",
      updateUserCall: "updateUser",
      deleteUserCall: "deleteUser",
    }),
    ...mapActions("campaign", { getAllCampaigns: "getAll" }),
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
    createUser() {
      this.user = {
        name: this.name,
        surname: this.surname,
        username: this.username,
        roles: this.roles.map((elem) =>{
        return {
            companyId: this.adminCompany.item.id,
            locations: null,
            role: elem,
            subscriptions: null
            }
            })
      };
    },
    saveUser() {
      //check fields
      // eslint-disable-next-line no-constant-condition
      // console.log("submit!");
      console.log(this.roles)
      this.$v.$touch();
      if (this.$v.$invalid) {
        this.submitStatus = "ERROR";
        return;
      } else {
        this.createUser();
        this.submitStatus = "SUCCESS";
        if (this.newUser) {
          this.addUserCall({ companyId: this.adminCompany.item.id, user: this.user });
        } else {
          this.updateUserCall({ companyId: this.adminCompany.item.id, user: this.user });
        }
        this.$v.$reset();
      }
      this.editModalVisible = false;
      this.newUser = false;
    },
    deleteConfirm() {
      this.deleteModalVisible = false;
      this.deleteUserCall({ companyId: this.adminCompany.item.id, user: this.user });
    },
  },

  mounted() {
    // this.company= this.adminCompany? this.adminCompany :this.actualCompany
    if (this.adminCompany) {
      this.getUsers(this.adminCompany.item);
      this.getAllCampaigns(this.adminCompany.item.id);
    }
  },
};
</script>

<style scoped>
.empty-row {
  text-align: center;
  font-size: large;
}
</style>
