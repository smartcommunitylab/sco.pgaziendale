<template>
  <v-data-table v-if="dataTableData"
    :headers="dataTableData.subheaders"
    :items="dataTableData.data"
    :items-per-page="10"
    class="statstable elevation-1"
  >
    <template v-slot:header >
    <thead>
      <tr>
        <th class="cell-agg" colspan="1"></th>
        <th class="cell-agg" :colspan="dataTableData.headerNumber" v-for="(header,index) in dataTableData.headers" :key="index">{{header}}</th>
      </tr>
      <!-- <tr>
        <th v-for="(header,index) in dataTableData.subheaders" :key="index">{{header.text}}</th>
      </tr> -->
    </thead>
  </template>

    <template v-slot:item="{ item, headers }">
      <tr>
        <td :class="index == 0 || ((index) % Math.floor(dataTableData.headerNumber) ==0) ? 'cell-agg': ''" v-for="(header,index) in headers" :key="index">{{item[header.value]}}</td>
      </tr>
    </template>

    <!-- <template v-slot:item.name="{ item }">
        <span style="white-space: nowrap;">{{ item.name }}</span>
    </template> -->


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
   .statstable table td, .statstable table th {
    white-space: nowrap;
   }

</style>