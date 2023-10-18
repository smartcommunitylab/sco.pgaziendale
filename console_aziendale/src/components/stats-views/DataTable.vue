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
         <th colspan="1"></th>
        <th :colspan="dataTableData.headerNumber" v-for="(header,index) in dataTableData.headers" :key="index">{{header}}</th>
      </tr>
    </thead>
  </template>

    <template v-slot:item.name="{ item }">
        <span style="white-space: nowrap;">{{ item.name }}</span>
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

</style>