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
            v-for="item in items"
            :key="item"
            
            >
                {{ item }}
            </v-tab>
        </v-tabs>
        <v-tabs-items v-model="tab" class="mt-5">
            <v-tab-item key="Tabella">
                <data-table></data-table>
            </v-tab-item>
            <v-tab-item key="Grafico a barre">
                <v-card flat>
                    <line-chart></line-chart>
                </v-card>
            </v-tab-item>
            <v-tab-item key="Mappa">
                <v-card flat>
                </v-card>
            </v-tab-item>
        </v-tabs-items>  
    </v-card>
</template>


<script>
import DataTable from "@/components/stats-views/DataTable.vue";
import LineChart from "@/components/stats-views/LineChart.vue";

export default {
    components: {
        "data-table" : DataTable,
        "line-chart" : LineChart,
    },    
    data () {
      return {
        tab: null,
        items: [
          'Tabella', 'Grafico a barre', 'Mappa',
        ],
      }
    },
  }
</script>


