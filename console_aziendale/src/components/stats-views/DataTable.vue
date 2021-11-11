<template>
  <v-data-table
    :headers="headers"
    :items="desserts"
    :items-per-page="5"
    class="elevation-1"
  >
    <template v-slot:header>
      <!-- Le category vanno popolate con il valore inserito nell'unità temporale tramite (v-for).
           Il "colspan" va popolatocon una variabile, sempre passata come pros, che
           indica il numero di campi selezionati (dataLevel) dall'utente che vuole vedere
      --> 
      <thead>
        <tr>
          <th colspan="1"></th>
          <th v-for="time in timeFilterList" :key="time" :colspan="nDataColumActive">{{time}}</th>
        </tr>
      </thead>
    </template>
  </v-data-table>
</template>


<script>
  export default {

    props:{
      timeFilterList: Array,
      nDataColumActive: Number,
      notFormattedHeaders: Array,
      selectDataLevel: String,
    },

    data () {
      return {

        headers: [],

        headersList: [
          {
            text: 'Dessert (100g serving)',
            align: 'start',
            sortable: false,
            value: 'name',
            width: "200px" 
          },
          { text: 'Calories', value: 'calories', width: "200px" },
          { text: 'Fat (g)', value: 'fat', width: "200px" },
          { text: 'Carbs (g)', value: 'carbs', width: "200px" },
          { text: 'Protein (g)', value: 'protein', width: "200px" },
          { text: 'Iron (%)', value: 'iron', width: "200px" },
        ],
        desserts: [
          {
            name: 'Frozen Yogurt',
            calories: 159,
            fat: 6.0,
            carbs: 24,
            protein: 4.0,
            iron: '1%',
          },
          {
            name: 'Ice cream sandwich',
            calories: 237,
            fat: 9.0,
            carbs: 37,
            protein: 4.3,
            iron: '1%',
          },
          {
            name: 'Eclair',
            calories: 262,
            fat: 16.0,
            carbs: 23,
            protein: 6.0,
            iron: '7%',
          },
          {
            name: 'Cupcake',
            calories: 305,
            fat: 3.7,
            carbs: 67,
            protein: 4.3,
            iron: '8%',
          },
          {
            name: 'Gingerbread',
            calories: 356,
            fat: 16.0,
            carbs: 49,
            protein: 3.9,
            iron: '16%',
          },
          {
            name: 'Jelly bean',
            calories: 375,
            fat: 0.0,
            carbs: 94,
            protein: 0.0,
            iron: '0%',
          },
          {
            name: 'Lollipop',
            calories: 392,
            fat: 0.2,
            carbs: 98,
            protein: 0,
            iron: '2%',
          },
          {
            name: 'Honeycomb',
            calories: 408,
            fat: 3.2,
            carbs: 87,
            protein: 6.5,
            iron: '45%',
          },
          {
            name: 'Donut',
            calories: 452,
            fat: 25.0,
            carbs: 51,
            protein: 4.9,
            iron: '22%',
          },
          {
            name: 'KitKat',
            calories: 518,
            fat: 26.0,
            carbs: 65,
            protein: 7,
            iron: '6%',
          },
        ],
      }
    },

    computed: {
     
    },
     /*
        headers: [
          {
            text: 'Dessert (100g serving)',
            align: 'start',
            sortable: false,
            value: 'name',
            width: "200px" 
          },
          { text: 'Calories', value: 'calories', width: "200px" },
          { text: 'Fat (g)', value: 'fat', width: "200px" },
          { text: 'Carbs (g)', value: 'carbs', width: "200px" },
          { text: 'Protein (g)', value: 'protein', width: "200px" },
          { text: 'Iron (%)', value: 'iron', width: "200px" },
        ],
      */
    methods: {
      formatHeaders(){
        console.log("Sono nel computed");
        let headersAray = [];

        headersAray.push({
          text: this.selectDataLevel,
          align: 'start',
          sortable: false,
          value: 'name',
          width: "200px" 
        },)

        for(let i = 0; i < this.timeFilterList.length; i++){
          this.notFormattedHeaders.forEach(element => {
            headersAray.push({text: element, value: element, width: "200px" });
          });
        }
        this.headers = headersAray;
      },
    },
    watch:{
      notFormattedHeaders(){
        this.formatHeaders();
      }
    }
  }
</script>