<template>
    <v-card elevation="2" class="frosted-glass" v-if="actualCompany && actualCompany.item">
        <v-card-title>{{ actualCompany.item.name }}</v-card-title>
        
        <v-img
            v-if="/*existImageURL(actualCompany.item.logo)*/actualCompany.item.logo "
            class="block mx-auto h-48 w-48 bg-contain bg-center bg-no-repeat"
            :style="{ backgroundImage: 'url(' + actualCompany.item.logo + ')' }"
            height="200px"
        />
        
        <v-card-text>
            <v-list dense style="background-color: transparent">
                <v-list-item-group
                    color="primary"
                >
                    <v-list-item :href="'http://maps.google.com/?q='+actualCompany.item.address + ' ' + actualCompany.item.streetNumber + ', ' + actualCompany.item.city + ' ' + actualCompany.item.province + ', ' + actualCompany.item.zip" target="_blank">
                        <v-list-item-icon>
                            <v-icon>mdi-map-marker</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="actualCompany.item.address + ' ' + actualCompany.item.streetNumber + ', ' + actualCompany.item.city + ' ' + actualCompany.item.province + ', ' + actualCompany.item.zip"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item :href="actualCompany.item.web" target="_blank">
                        <v-list-item-icon>
                            <v-icon>mdi-web</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-if="actualCompany.item.web" v-text="actualCompany.item.web"></v-list-item-title>
                            <v-list-item-title v-else>Nessun sito web inserito</v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item :href="`mailto:${actualCompany.item.contactEmail}`" target="_blank">
                        <v-list-item-icon>
                            <v-icon>mdi-email</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="actualCompany.item.contactEmail"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item :href="`tel:${actualCompany.item.contactPhone}`" target="_blank">
                        <v-list-item-icon>
                            <v-icon>mdi-phone</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="actualCompany.item.contactPhone"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>

                    <v-list-item>
                        <v-list-item-content>
                            <div v-if="adminCompanyUsers && adminCompanyUsers.items && adminCompanyUsers.items.length > 0">
                                <p class="text-h6">Manager</p>
                                <v-simple-table>
                                    <thead>
                                        <tr><th>Cognome</th><th>Nome</th><th>Username</th></tr>
                                    </thead>
                                    <tbody>
                                        <tr  v-for="tr in adminCompanyUsers.items" :key="tr.id">
                                            <td>{{tr.surname}}</td><td>{{tr.name}}</td><td><a target="_blank" :href="'mailto:'+tr.username">{{tr.username}}</a></td>
                                        </tr>
                                    </tbody>
                                </v-simple-table>
                            </div>
                            <v-list-item-title v-else>Nessun manager definito</v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>
            </v-list>
        </v-card-text>
        <v-card-actions>
            <v-btn
                v-if="!adminCompany && actualCompany && user.canDo('manage', 'company', actualCompany.item.id)"
                text
                color="primary"
                @click="chooseCompanyAdmin"
            >
                DIVENTA AMMINISTRATORE
            </v-btn>
            <v-btn
                v-if="adminCompany && $route.name !== 'ProfiloAzienda'"
                text
                disabled
                color="primary"
            >
                SEI AMMINISTRATORE
            </v-btn>

            <v-spacer></v-spacer>

            <v-btn icon v-if="user.canDo('manage', 'company', actualCompany.item.id)"
            @click="openModal({type:'aziendaFormEdit', object:null})">
                <v-icon>mdi-pencil</v-icon>
            </v-btn>

            <v-btn icon v-if="$route.name !== 'ProfiloAzienda' && user.canDo('manage', 'campaigns', actualCompany.item.id)"
            @click="openModal({type:'associateForm', object:null})">
                <v-icon>mdi-clipboard-text-multiple-outline</v-icon>
            </v-btn>

            <v-btn icon v-show="$route.name !== 'ProfiloAzienda' && user.canDo('manage', 'company', actualCompany.item.id)"
            @click="openModal({type:'updateCompanyState', object:null})">
                <v-icon>{{actualCompany.item.state ? 'mdi-close' : 'mdi-check'}}</v-icon>
            </v-btn>

            <v-btn icon v-show="$route.name !== 'ProfiloAzienda' && user.canDo('manage', 'companies')"
            @click="openModal({type:'deleteAzienda', object:null})">
                <v-icon>mdi-delete</v-icon>
            </v-btn>

        </v-card-actions>
    </v-card>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  name: "ProfiloAzienda",
  
  methods: {
    ...mapActions("company", {
      getCompanyById: "getCompanyById",
      getUsers: "getUsers",
      chooseCompanyAdminCall: "chooseCompanyAdmin",
    }),
    ...mapActions("modal", {openModal: "openModal"}),

    chooseCompanyAdmin() {
      this.chooseCompanyAdminCall(this.actualCompany);
      this.$router.push('/ProfiloAzienda');
    },
  },

  mounted() {
    if (this.actualCompany) {
        this.getUsers(this.actualCompany.item);
    } else if (this.adminCompany) {
        this.getUsers(this.adminCompany.item);
    }
  },

  watch: {
    actualCompany() {
        this.getUsers(this.actualCompany.item);
    },
    adminCompany() {
        this.getUsers(this.adminCompany.item);
    },
  },

  computed: {
    ...mapState("company", ["adminCompany", "actualCompany", "adminCompanyUsers"]),
    ...mapState("account", ["user"]),

    isAdmin: function(){
        if(this.adminCompany){
            return true;
        }else{
            return false;
        }
    }
  },
};
</script>

<style scoped>
.frosted-glass {
  box-shadow: 0 0 1rem 0 rgba(0, 0, 0, .2); 
  border-radius: 5px;
  background-color: rgba(255, 255, 255, .15);
  border-color: transparent;
  backdrop-filter: blur(5px);
}
</style>
