<template>
  <v-navigation-drawer
    app
    permanent
    expand-on-hover
    clipped
  >
    <v-list>
      <v-list-item link>
        <v-list-item-content>        
          <profilo-header/>
        </v-list-item-content>
      </v-list-item>
    </v-list>

    <v-divider></v-divider>

    <v-list
      nav
      dense
    >
      <router-link to="/aziende" v-if="role == 'ROLE_ADMIN' && adminCompany == null" >
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-factory</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Gestione Aziende</v-list-item-title>
        </v-list-item>
      </router-link>
      <router-link
            to="/azienda"
            v-if="
              role == 'ROLE_COMPANY_ADMIN' ||
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-home</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Profilo Azienda</v-list-item-title>
        </v-list-item>
      </router-link>
      <router-link
            to="/locations"
            v-if="
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_COMPANY_ADMIN' && actualCompany != null)
            "
          >
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-domain</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Gestione Sedi</v-list-item-title>
        </v-list-item>
      </router-link>
      <router-link
            to="/dipendenti"
            v-if="
              (role == 'ROLE_ADMIN' && adminCompany != null) ||
              (role == 'ROLE_MOBILITY_MANAGER' && actualCompany != null)
            "
          >
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-account-group</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Gestione Dipendenti</v-list-item-title>
        </v-list-item>
      </router-link>
      <router-link
            to="/gestionecampagne"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'"
          >
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-clipboard-text-multiple-outline</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Gestione Campagne</v-list-item-title>
        </v-list-item>
      </router-link>
      <router-link
            to="/stats"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'"
          >
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-chart-line</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Statistiche</v-list-item-title>
        </v-list-item>
      </router-link>
      <router-link to="/" v-on:click.native="logout">
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-logout</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Esci</v-list-item-title>
        </v-list-item>
      </router-link>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ProfiloHeader from "./ProfiloHeader.vue";

export default {
  name: "MenuHeader",
  components: {
    ProfiloHeader,
  },
  data: function () {
    return { isOpen: true };
  },
  computed: {
    ...mapState("account", ["status", "user", "role"]),
    ...mapState("navigation", ["page"]),
    ...mapState("company", ["adminCompany", "actualCompany"]),
  },
  methods: {
    drawer() {
      this.isOpen = !this.isOpen;
    },
    resetCompany() {
      this.resetCompanyAdmin();
    },
    goHome() {
      if ( this.role == 'ROLE_ADMIN' && this.adminCompany == null) 
              {
                this.$router.push("/aziende");
              }
      else {
        this.$router.push("/azienda");
      }
    },
    ...mapActions("account", ["logout"]),
    ...mapActions("company", ["resetCompanyAdmin"]),
  },
};
</script>

<style>
.profile-button {
  position: absolute;
  right: 50px;
}
</style>
