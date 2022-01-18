<template>
  <v-data-table v-if="dataTableData"
    :headers="dataTableData.subheaders"
    :items="dataTableData.data"
    :items-per-page="10"
    class="elevation-1"
  >
    <!-- <template v-slot:header >
    <thead>
      <tr>
        <th colspan="4" v-for="(header,index) in dataTableData.headers" :key="index">{{header}}</th>
      </tr>
    </thead>
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
    mounted() {
      console.log(this.dataTableData);
    },
    watch: {
      dataTableData:{
        handler: function (newVal) {
        console.log(newVal);
      },
      deep:true
      }
    },
  }
</script>