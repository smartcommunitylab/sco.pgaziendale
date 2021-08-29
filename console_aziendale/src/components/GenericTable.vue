<template>
  <v-card>
    <v-card-title>
      {{title}}
      <v-spacer></v-spacer>
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        single-line
        hide-details
      ></v-text-field>
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="items"
      :search="search"
    ></v-data-table>
  </v-card>
</template>

<script>
export default {
  props: {
    items: Array,
    headers: Array,
    title: String,
    // filterKey: String,
    /*method:Function*/
  },
  
  watch: {
    data: {
      // the callback will be called immediately after the start of the observation
      immediate: true, 
      handler (val, oldVal) {
        console.log(val);
        console.log(oldVal)
      }
    }
  },
  data() {
    return {
        search: '',
    }
  },
  computed: {
    filteredData: function() {
      var sortKey = this.sortKey;
      // var filterKey = this.filterKey && this.filterKey.toLowerCase();
      var order = this.sortOrders[sortKey] || 1;
      var data = this.data;
      // if (filterKey) {
      //   data = data.filter(function(row) {
      //     return Object.keys(row).some(function(key) {
      //       return (
      //         String(row[key])
      //           .toLowerCase()
      //           .indexOf(filterKey) > -1
      //       );
      //     });
      //   });
      // }
      if (sortKey) {
        data = data.slice().sort(function(a, b) {
          a = a[sortKey];
          b = b[sortKey];
          return (a === b ? 0 : a > b ? 1 : -1) * order;
        });
      }
      return data;
    }
  },
  filters: {
    capitalize: function(str) {
      return str.charAt(0).toUpperCase() + str.slice(1);
    }
  },
  methods: {
    sortBy: function(key) {
      //find sortkey
      this.sortKey = key;
      this.sortOrders[key] = this.sortOrders[key] * -1;
    }
  }
}
</script>
<style scoped>

</style>