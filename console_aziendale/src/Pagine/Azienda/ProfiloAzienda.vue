<template>
  <v-col cols="4">
    <v-card elevation="2">
        <v-card-title>{{ actualCompany.item.name }}</v-card-title>
        
        <v-img
            v-if="/*existImageURL(actualCompany.item.logo)*/actualCompany.item.logo "
            class="block mx-auto h-48 w-48 bg-contain bg-center bg-no-repeat"
            :style="{ backgroundImage: 'url(' + actualCompany.item.logo + ')' }"
            height="200px"
        />
        
        <v-card-text>
            <v-list dense>
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
                    <v-list-item :href="`mailto:${actualCompany.item.contactEmail}`">
                        <v-list-item-icon>
                            <v-icon>mdi-email</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="actualCompany.item.contactEmail"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item :href="`tel:${actualCompany.item.contactPhone}`">
                        <v-list-item-icon>
                            <v-icon>mdi-phone</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="actualCompany.item.contactPhone"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>
            </v-list>
        </v-card-text>
        <v-card-actions>
            <v-btn
                v-if="!adminCompany && actualCompany && role == 'ROLE_ADMIN'"
                text
                color="teal accent-4"
                @click="chooseCompanyAdmin"
            >
                DIVENTA AMMINISTRATORE
            </v-btn>
            <v-btn
                v-if="adminCompany && $route.name !== 'azienda'"
                text
                disabled
                color="teal accent-4"
            >
                SEI AMMINISTRATORE
            </v-btn>

            <v-spacer></v-spacer>

            <v-btn icon v-if="role == 'ROLE_ADMIN'"
            @click="editAzienda">
                <v-icon>mdi-pencil</v-icon>
            </v-btn>

            <v-btn icon v-show="$route.name !== 'azienda'"
            @click="deleteAzienda">
                <v-icon>mdi-delete</v-icon>
            </v-btn>

        </v-card-actions>
    </v-card>
  </v-col>
</template>

<script>
import { mapState, mapActions } from "vuex";
import EventBus from "@/components/eventBus";

export default {
  name: "ProfiloAzienda",

  computed: {
    ...mapState("company", ["adminCompany", "actualCompany"]),
    ...mapState("account", ["role"]),
  },
  methods: {
    ...mapActions("company", {
      getCompanyById: "getCompanyById",
      chooseCompanyAdminCall: "chooseCompanyAdmin",
      /*
      existImageURL: function(url) {
        var request = new XMLHttpRequest();
        request.open("GET", url, true);
        request.send();
        request.onload = function() {
            if (request.status == 200) //if(statusText == OK)
            {
                return true;
            } else {
                return false;
            }
        }
      }*/
    }),

    deleteAzienda() {
      EventBus.$emit("DELETE_COMPANY", this.actualCompany);
    },
    editAzienda() {
      EventBus.$emit("EDIT_COMPANY", this.actualCompany);
    },
    chooseCompanyAdmin() {
      this.chooseCompanyAdminCall(this.actualCompany);
    },
  },
};
</script>

<style scoped>

</style>
