<!-- 
DESCRIZIONE:
Il "GenericTable.vue" è una tabella generalizzata così da poterla usare con qualsiasi tipo di
dato e intestazione vogliamo all'interno dell'applicativo.
-->
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
      class="row-pointer elevation"
      :headers="headers"
      :items="items"
      :search="search"
      @click:row="method"
      :header-props="{'sortByText': 'Ordina per'}"
      :footer-props="{
        'items-per-page-text':'righe per pagina',
        pageText: '{0}-{1} di {2}'
      }"
      no-results-text="La ricerca non ha dato risultati"
      no-data-text="Non ci sono dati inseriti"
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
  
  data() {
    return {
      search: '',
    }
  },

  watch: {
    data: {
      // the callback will be called immediately after the start of the observation
      immediate: true, 
      handler (val, oldVal) {
        console.log(val);
        console.log(oldVal)
      }
    },
  },

  filters: {
    capitalize: function(str) {
      return str.charAt(0).toUpperCase() + str.slice(1);
    }
  },
}
</script>

<style scoped>
.row-pointer:hover {
  cursor: pointer;
}
.elevation{
  z-index: 60;
}
</style>