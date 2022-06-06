<template>
  <v-data-table v-if="dataTableData"
  
    :headers="dataTableData.subheaders"
    :items="dataTableData.data"
    :items-per-page="10"
    class="elevation-1"
  >
    <template v-slot:header >
    <thead>
      <tr>
         <th colspan="1"></th>
        <th :colspan="dataTableData.headerNumber" v-for="(header,index) in dataTableData.headers" :key="index">{{header}}</th>
      </tr>
    </thead>
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
    watch: {
    dataTableData: {
      handler() {
        if (this.dataTableData){
          this.dataTableData.data.forEach(row => {
            const props = Object.keys(row);
            props.forEach(prop => {
               if(typeof row[prop] == 'number'){
                  Math.abs(Math.round(row[prop]));
                }
            })
        });
        }
        
      },
      immediate: true,
    }
  },
  mounted() {
  },
  }
</script>