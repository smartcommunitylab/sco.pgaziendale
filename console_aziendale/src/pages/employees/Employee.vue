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
                        <v-list-item-content>
                            <div v-if="employeeCampaigns && employeeCampaigns.length > 0">
                                <p class="text-h6">Iscrizioni</p>
                                <v-simple-table>
                                    <thead>
                                        <tr><th></th><th>Iscrizione</th><th>Abbandono</th><th>Ultima attività</th></tr>
                                    </thead>
                                    <tbody>
                                        <tr  v-for="tr in employeeCampaigns" :key="tr.id">
                                            <td>{{tr.title}}</td><td>{{toD(tr.registration)}}</td><td>{{toD(tr.leave)}}</td><td>{{toD(tr.tracking)}}</td>
                                        </tr>
                                    </tbody>
                                </v-simple-table>
                            </div>
                            <v-list-item-title v-else>Nessuna campagna associata</v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>
            </v-list>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn icon @click="openModal({type:'employeeFormEdit', object:null})">
                <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn icon @click="openModal({type:'deleteEmployee', object:{actCompany: actualCompany, actEmployee: actualEmployee}})">
                <v-icon>mdi-delete</v-icon>
            </v-btn>
        </v-card-actions>
    </v-card>
  </v-col>
</template>

<script>
import { mapActions, mapState } from "vuex";
import moment from "moment";

export default {
  name: "ProfiloEmployee",
  data() {
    return {
        employeeCampaigns: null
    }
  },

  methods: {
    ...mapActions("modal", {openModal:"openModal"}),
    //  ...mapActions("campaign", {
    //   getCampaignTitleById: "getCampaignTitleById"}),
	editEmployee() {
	},

    toD(time) {
        return time ? moment(time).format('DD-MM-YY') : '-';
    },
    toDT(time) {
        return time ? moment(time).format('DD-MM-YY HH:mm') : '-';

    },

    updateEmployeeData() {
        console.log('triggering');
        if (this.actualEmployee && this.actualEmployee.item && this.allCampaigns && this.allCampaigns.items) {
            console.log('updating');
            let list = (this.actualEmployee.item.campaigns || []).concat(this.actualEmployee.item.trackingRecord ? Object.keys(this.actualEmployee.item.trackingRecord) : []);
            this.employeeCampaigns =  Array.from(new Set(list)).map(cId => {
                let tr = this.actualEmployee.item.trackingRecord && this.actualEmployee.item.trackingRecord[cId] ? this.actualEmployee.item.trackingRecord[cId] : {registration: new Date().getTime()}
                tr.id = cId;
                tr.title = (this.allCampaigns.items.find(c => c.id === cId) || {title: cId}).title;
                return tr;
            });
        }
    }
  },
  mounted() {
    this.updateEmployeeData();
  },  
  computed: {
    ...mapState("employee", ["actualEmployee"]),
    ...mapState("company", ["actualCompany"]),
    ...mapState("campaign", ["allCampaigns"]),
  },
  watch: {
    actualEmployee() {
        this.updateEmployeeData();
    },
    allCampaigns() {
        this.updateEmployeeData();
    }
  }
};
</script>

<style scoped>
</style>