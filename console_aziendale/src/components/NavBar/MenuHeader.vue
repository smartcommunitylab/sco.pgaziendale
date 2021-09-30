<template>
  <v-navigation-drawer
    app
    permanent
    expand-on-hover
    clipped
  >
    <v-list>
      <profilo-header/>
    </v-list>

    <v-divider></v-divider>

    <v-list
      nav
      dense
    >
      <router-link to="/aziende" v-if="role == 'ROLE_ADMIN' && adminCompany == null" >
        <v-list-item link >
          <v-list-item-icon>
            <v-icon
              color = "primary"
              v-if = "isActiveGestioneAziende"
            > mdi-factory
            </v-icon>
            <v-icon
              v-else
            > mdi-factory
            </v-icon>
          </v-list-item-icon>
          <v-list-item-title :class="{ active: isActiveGestioneAziende }">Gestione Aziende</v-list-item-title>
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
            <v-icon
              color = "primary"
              v-if = "isActiveProfiloAzienda"
            >mdi-home</v-icon>
            <v-icon
              v-else
            >mdi-home
            </v-icon>
          </v-list-item-icon>
          <v-list-item-title :class="{ active: isActiveProfiloAzienda }">Profilo Azienda</v-list-item-title>
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
            <v-icon
              color = "primary"
              v-if = "isActiveGestioneSedi"
            >mdi-domain</v-icon>
            <v-icon
              v-else
            >mdi-domain
            </v-icon>
          </v-list-item-icon>
          <v-list-item-title :class="{ active: isActiveGestioneSedi }">Gestione Sedi</v-list-item-title>
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
            <v-icon
              color = "primary"
              v-if = "isActiveGestioneDipendenti"
            >mdi-account-group</v-icon>
            <v-icon
              v-else
            >mdi-account-group
            </v-icon>
          </v-list-item-icon>
          <v-list-item-title :class="{ active: isActiveGestioneDipendenti }">Gestione Dipendenti</v-list-item-title>
        </v-list-item>
      </router-link>
      <router-link
            to="/gestionecampagne"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'"
          >
        <v-list-item link>
          <v-list-item-icon>
            <v-icon
              color = "primary"
              v-if = "isActiveGestioneCamagne"
            >mdi-clipboard-text-multiple-outline</v-icon>
            <v-icon
              v-else
            >mdi-clipboard-text-multiple-outline
            </v-icon>
          </v-list-item-icon>
          <v-list-item-title :class="{ active: isActiveGestioneCamagne }">Gestione Campagne</v-list-item-title>
        </v-list-item>
      </router-link>
      <router-link
            to="/stats"
            v-if="role == 'ROLE_COMPANY_ADMIN' || role == 'ROLE_ADMIN'"
          >
        <v-list-item link>
          <v-list-item-icon>
            <v-icon
              color = "primary"
              v-if = "isActiveStatistiche"
            >mdi-chart-line</v-icon>
            <v-icon
              v-else
            >mdi-chart-line
            </v-icon>
          </v-list-item-icon>
          <v-list-item-title :class="{ active: isActiveStatistiche }">Statistiche</v-list-item-title>
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
    return { 
      isOpen: true,
      isActiveGestioneAziende: false,
      isActiveProfiloAzienda: false,
      isActiveGestioneSedi: false,
      isActiveGestioneDipendenti: false,
      isActiveGestioneCamagne: false,
      isActiveStatistiche: false,
    };
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
    turnOffActive(){
      this.isActiveGestioneAziende = false;
      this.isActiveProfiloAzienda = false;
      this.isActiveGestioneSedi = false;
      this.isActiveGestioneDipendenti = false;
      this.isActiveGestioneCamagne = false;
      this.isActiveStatistiche = false;
    },
    ...mapActions("account", ["logout"]),
    ...mapActions("company", ["resetCompanyAdmin"]),
  },
  watch: {
    $route: function () {
      switch (this.$router.currentRoute.path) {
                case '/aziende':
                    this.turnOffActive();
                    this.isActiveGestioneAziende = true;
                    break;
                case '/azienda':
                    this.turnOffActive();
                    this.isActiveProfiloAzienda = true;
                    break;
                case '/locations':
                    this.turnOffActive();
                    this.isActiveGestioneSedi = true;
                    break;
                case '/dipendenti':
                    this.turnOffActive();
                    this.isActiveGestioneDipendenti = true;
                    break;
                case '/gestionecampagne':
                    this.turnOffActive();
                    this.isActiveGestioneCamagne = true;
                    break;
                case '/stats':
                    this.turnOffActive();
                    this.isActiveStatistiche = true;
                    break;
      }
    },
  },
};
</script>

<style>

.profile-button {
  position: absolute;
  right: 50px;
}
.active {
  color: #0f70b7;
}

</style>