<template>
  <div>
    <v-row>
      <v-col>
        <v-btn
          x-large
          color="secondary"
          rounded
          elevation="6"
          @click="openModal({type:'userFormAdd', object:null})/*showModal('Aggiungi utente')*/"
        >
          <v-icon left>mdi-plus</v-icon>
          AGGIUNGI
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <!-- TODO: Cambiare da Card in Tabella con action-->
      <v-col cols="12" class="pb-0">
        <generic-table-action
          :items="adminCompanyUsers.items"
          :headers="headerColumns"
          :title="tableTitle"
        />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import GenericTableAction from '../../components/GenericTableAction.vue';

export default {
  name: "GestioneAzienda",
  components: { GenericTableAction },
  data() {
    return {
      submitStatus: null,
      company: null,
      editModalVisible: false,
      deleteModalVisible: false,
      newUser: true,

      tableTitle: "Utenti",
      headerColumns: [{text:"Cognome", value:"surname"}, {text:"Nome", value:"name"}, {text:"Username", value:"username"}, {text:"Ruoli", value:"rolesComputed"}, { text: 'Azioni', value: 'actions', sortable: false }],
    };
  },
  computed: {
    ...mapState("company", ["adminCompany", "adminCompanyUsers"]),
    ...mapState("campaign", ["allCampaigns"]),
  },

  methods: {
    ...mapActions("modal", {openModal:'openModal'}),
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
    
    ...mapActions("modal", {openModal:"openModal"}),

  },
  mounted() {
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
