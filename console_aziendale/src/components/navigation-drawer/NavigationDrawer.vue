<!-- 
DESCRIZIONE:
Il "NavigationDrawer.vue" è il Navigation Drawer dell'applicativo. Ovvero la sidebar posta a
sinistra che permette la navigazione in tutte le pagine che l'utente può visualizzare.
Da qui si gestisce infatti anche la parte di permessi di visualizzaizone o meno delle
pagine dall'utente in base al suo login.
-->
<template>
  <v-navigation-drawer
    app
    permanent
    expand-on-hover
    clipped
  >
    <v-list>
      <account-viewer/>
    </v-list>

    <v-divider></v-divider>

    <v-list
      v-if="user"
      nav
      dense
    >
      <router-link to="/GestioneAziende" v-if="adminCompany == null && user.canDo('view', 'companies')" >
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
            to="/ProfiloAzienda"
            v-if="adminCompany && user.canDo('manage', 'company', adminCompany.item.id)"
          >
        <v-list-item link >
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
            to="/GestioneUtenti"
            v-if="adminCompany && user.canDo('manage', 'users', adminCompany.item.id)"
          >
        <v-list-item link >
          <v-list-item-icon>
            <v-icon
              color = "primary"
              v-if = "isActiveGestioneUtenti"
            >mdi-account-tie</v-icon>
            <v-icon
              v-else
            >mdi-account-tie
            </v-icon>
          </v-list-item-icon>
          <v-list-item-title :class="{ active: isActiveGestioneUtenti }">Gestione Utenti</v-list-item-title>
        </v-list-item>
      </router-link>

      <router-link
            to="/GestioneSedi"
            v-if="adminCompany && user.canDo('manage', 'locations', adminCompany.item.id)"
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
            to="/GestioneDipendenti"
            v-if="adminCompany && user.canDo('manage', 'employees', adminCompany.item.id)"
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
            v-if="adminCompany && user.canDo('view', 'campaigns', adminCompany.item.id) || !adminCompany && user.canDo('view', 'campaigns')"
            to="/GestioneCampagne"
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
          <v-list-item-title :class="{ active: isActiveGestioneCamagne }">Campagne</v-list-item-title>
        </v-list-item>
      </router-link>
      <router-link
            to="/Statistiche"
            v-if="user.canDo('view', 'stats', adminCompany ? adminCompany.item.id : null)"
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
        <v-list-item link  @click="doLogout">
          <v-list-item-icon>
            <v-icon>mdi-logout</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Esci</v-list-item-title>
        </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import { mapState, mapActions } from "vuex";
import AccountViewer from "@/components/navigation-drawer/AccountViewer.vue";

export default {
  name: "NavigationDrawer",

  components: {
    "account-viewer": AccountViewer
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
      isActiveGestioneUtenti: false,
    };
  },

  methods: {
    ...mapActions("account", ["logout"]),
    ...mapActions("company", ["resetCompanyAdmin"]),
    
    drawer() {
      this.isOpen = !this.isOpen;
    },
    resetCompany() {
      this.resetCompanyAdmin();
    },
    doLogout() {
      console.log('Do logout');
      this.logout();
      this.$router.push('/');
    },
    goHome() {
      if (this.adminCompany == null){
        this.$router.push("/GestioneAziende");
      }
      else {
        this.$router.push("/ProfiloAzienda");
      }
    },
    turnOffActive(){
      this.isActiveGestioneAziende = false;
      this.isActiveProfiloAzienda = false;
      this.isActiveGestioneSedi = false;
      this.isActiveGestioneDipendenti = false;
      this.isActiveGestioneCamagne = false;
      this.isActiveStatistiche = false;
      this.isActiveGestioneUtenti = false;
    },
    activeRootSelection(){
      switch (this.$router.currentRoute.path) {
                case '/GestioneAziende':
                    this.turnOffActive();
                    this.isActiveGestioneAziende = true;
                    break;
                case '/ProfiloAzienda':
                    this.turnOffActive();
                    this.isActiveProfiloAzienda = true;
                    break;
                case '/GestioneSedi':
                    this.turnOffActive();
                    this.isActiveGestioneSedi = true;
                    break;
                case '/GestioneDipendenti':
                    this.turnOffActive();
                    this.isActiveGestioneDipendenti = true;
                    break;
                case '/GestioneCampagne':
                    this.turnOffActive();
                    this.isActiveGestioneCamagne = true;
                    break;
                case '/Statistiche':
                    this.turnOffActive();
                    this.isActiveStatistiche = true;
                    break;
                case '/GestioneUtenti':
                    this.turnOffActive();
                    this.isActiveGestioneUtenti = true;
      }
    },
  },

  computed: {
    ...mapState("account", ["status", "user"]),
    ...mapState("navigation", ["page"]),
    ...mapState("company", ["adminCompany", "actualCompany"]),
  },
  
  watch: {
    $route: function () {
      this.activeRootSelection();
    },
  },

  created: function () {
    this.activeRootSelection();
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