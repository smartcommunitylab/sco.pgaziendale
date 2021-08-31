<template>
  <v-col cols="4">
    <v-card elevation="2">
        <v-card-title>{{ actualEmployee.item.name }} {{ actualEmployee.item.surname }}</v-card-title>
        
        <v-card-text>
            <v-list dense>
                <v-list-item-group
                    color="primary"
                >
                    <v-list-item>
                        <v-list-item-icon>
                            <v-icon>mdi-map-marker</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="actualEmployee.item.location"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item>
                        <v-list-item-icon>
                            <v-icon>mdi-card-account-details</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="actualEmployee.item.code"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item>
                        <v-list-item-icon>
                            <v-icon>mdi-format-list-text</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-if="actualEmployee.item.campaigns.lenght == 0" v-text="actualEmployee.item.campaigns"></v-list-item-title>
                            <v-list-item-title v-else>Nessuna campagna associata</v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>
            </v-list>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn icon @click="editEmployee">
                <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn icon @click="deleteEmployee">
                <v-icon>mdi-delete</v-icon>
            </v-btn>
        </v-card-actions>
    </v-card>
  </v-col>
</template>
<script>
import { mapState } from "vuex";
import EventBus from "@/components/eventBus";
export default {
  name: "ProfiloEmployee",
  data() {
    return {};
  },

  computed: {
    ...mapState("employee", ["actualEmployee"]),
  },
  methods: {
	deleteEmployee() {
		EventBus.$emit("DELETE_EMPLOYEE",this.actualEmployee);
	},
	editEmployee() {
		EventBus.$emit("EDIT_EMPLOYEE",this.actualEmployee);
	},
  getCampaings(campaigns) {
    var returnCampaigns=" ";
    campaigns.forEach(element => {
      returnCampaigns+="<div> "+element+" </div>";
    });
    return returnCampaigns;
  }
  },
};
</script>

<style scoped></style>
