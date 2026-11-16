<template>
  <v-data-table v-if="dataTableData"
    :headers="dataTableData.subheaders"
    :items="dataTableData.data"
    :items-per-page="10"
    hide-default-header
    :class="{ 'has-total-group': dataTableData.headers[0] === 'Totale' }"
    class="statstable elevation-1"
  >
    <template v-slot:header >
    <thead>
      <tr>
        <th class="cell-agg" colspan="1"></th>
        <th
          v-for="(header, index) in dataTableData.headers"
          :key="index"
          class="cell-agg"
          :class="{ 'total-column': header === 'Totale' }"
          :colspan="dataTableData.headerNumber"
        >
          <strong v-if="header === 'Totale'">{{ header }}</strong>
          <span v-else>{{ header }}</span>
        </th>
      </tr>
      <tr>
        <th
          v-for="(header, index) in dataTableData.subheaders"
          :key="index"
          :class="{
            'total-column':
              dataTableData.headers[0] === 'Totale' &&
              index > 0 &&
              index <= dataTableData.headerNumber,
          }"
        >
          <strong
            v-if="dataTableData.headers[0] === 'Totale' && index > 0 && index <= dataTableData.headerNumber"
          >{{ header.text }}</strong>
          <span v-else>{{ header.text }}</span>
        </th>
      </tr>
    </thead>
  </template>

    <template v-slot:item="{ item, headers }">
      <tr>
        <td
          v-for="(header, index) in headers"
          :key="index"
          :class="[
            index === 0 || index % Math.floor(dataTableData.headerNumber) === 0
              ? 'cell-agg'
              : '',
            dataTableData.headers[0] === 'Totale' &&
            index > 0 &&
            index <= dataTableData.headerNumber
              ? 'total-column'
              : '',
          ]"
          v-html="item[header.value]"
        ></td>
      </tr>
    </template>

    <template v-slot:no-data>
      <div class="pa-8 text-center">
        <v-icon large color="grey lighten-1" class="mb-2">mdi-chart-box-outline</v-icon>
        <div class="text-h6 grey--text darken-1">Nessun dato da visualizzare</div>
      </div>
    </template>


  </v-data-table>


</template>


<script>
  export default {
    props:{
      dataTableData:Object
    },
    data () {
      return {
      
       }
    },
  }
</script>
<style>
  .statstable table > tbody > tr > td:nth-child(1), 
  .statstable table > thead > tr > th:nth-child(1) {
    position: sticky !important; 
    position: -webkit-sticky !important; 
    left: 0; 
    z-index: 2;
    background: white;
  }
  .statstable table > tbody > tr:hover > td:nth-child(1) {
    background: #eeeeee !important;
  }
  .statstable table > tbody > tr > td:not(nth-child(1)) {
        z-index: 1;
  } 

   .statstable table .cell-agg {
    border-right: 1px solid rgba(0, 0, 0, 0.12);
   }
  .statstable table .total-column {
    font-weight: 900 !important;
  }
   .statstable table td, .statstable table th {
    white-space: nowrap;
   }

</style>