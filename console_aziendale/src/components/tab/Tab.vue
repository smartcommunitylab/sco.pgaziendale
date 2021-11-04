<!--
DESCRIZIONE:
Il componente "Tabs.vue" cicla la cartella "tab" e prende tutti i componenti "Tab.vue".
-->
<template>
    <v-card>
        <v-tabs
            v-model="tab"
            align-with-title
        >
            <v-tabs-slider color="primary"></v-tabs-slider>

            <v-tab
            v-for="item in configurations.items[0].views"
            :key="item.type"
            @click.prevent="setNewActualView(item.type)"
            >
                {{ item.type }}
            </v-tab>
        </v-tabs>
        
        <v-tabs-items v-model="tab" class="mt-5">
            <v-tab-item key="Tabella">
                <data-table></data-table>
            </v-tab-item>
            <v-tab-item key="Grafico a Barre Orizzontali">
            </v-tab-item>
            <v-tab-item key="Grafico a Linee">
                <line-chart></line-chart>
            </v-tab-item>
            <v-tab-item key="Grafico a Barre">
            </v-tab-item>
            <v-tab-item key="Mappa???">
            </v-tab-item>
        </v-tabs-items>  
    </v-card>
</template>


<script>
import { mapState, mapActions } from "vuex";
import DataTable from "@/components/stats-views/DataTable.vue";
import LineChart from "@/components/stats-views/LineChart.vue";

export default {
    components: {
        "data-table" : DataTable,
        "line-chart" : LineChart
    },    
    data () {
      return {
        tab: null,
        items: [
          'Tabella', 'Grafico a barre', 'Mappa',
        ],
      }
    },
    computed:{
      ...mapState("stat", ["configurations", "activeConfiguration", "actualViewType"]),
    },
    methods: {
      ...mapActions("stat",{getConfigurationByRole:"getConfigurationByRole", setActiveViewType:"setActiveViewType"}),
      
      setNewActualView(view){
        this.setActiveViewType({activeViewType:view});
      },

      loadConfiguration(){
        this.getConfigurationByRole({role:"ROLE_COMPANY_ADMIN"});
        console.log(this.configurations.items);
      },
    },
    created(){
      this.loadConfiguration();
    },

    watch:{
        activeConfiguration(){
            this.tab = this.actualViewType;
        }
    }
  }
</script>


