<template>
  <v-card>
    <v-card-title>
      {{title}}
      <v-spacer></v-spacer>
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Cerca"
        single-line
        hide-details
      ></v-text-field>
    </v-card-title>
    <v-data-table
      class="row-pointer"
      :headers="headers"
      :items="items"
      :search="search"
      @click:row="method"
      :footer-props="{
        'items-per-page-text':'righe per pagina'
      }"
    >
    </v-data-table>
  </v-card>
</template>

<script>
export default {
  props: {
    items: Array,
    headers: Array,
    title: String,
    // filterKey: String,
    method:Function
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
  filters: {
    capitalize: function(str) {
      return str.charAt(0).toUpperCase() + str.slice(1);
    }
  },
  method: {
  }
}
</script>
<style scoped>
.row-pointer:hover {
  cursor: pointer;
}
</style>