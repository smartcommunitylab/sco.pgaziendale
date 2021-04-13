<template>
  <div class="small" v-if="datacollection">
    <line-chart :chart-data="datacollection"></line-chart>
    {{ this.selection }}
  </div>
</template>

<script>
import LineChart from "./LineChart.js";
import { mapState } from "vuex";

export default {
  components: {
    LineChart,
  },
  props: ["selection"],
  data() {
    return {
      datacollection: null,
    };
  },
  computed: {
    ...mapState("account", ["role"]),
    ...mapState("stat", ["stat"]),
  },
  mounted() {
    this.fillData();
  },
  watch: {
    stat() {
      // Our fancy notification (2).
      console.log(this.stat);
      if (this.stat && this.stat.items) this.fillData();
    },
  },
  methods: {
    fillData() {
      //check stats and selection and build data
      //   console.log(this.selection)
      // this.datacollection = {
      //   labels: ["label a", "label b"],
      //   datasets: []
      // }
      // for (var i=0; i<this.stat.items.length;i++){
      // }
      //         this.datacollection.datasets.push({

      //       label: this.stat.items[i].month,
      //       backgroundColor: '#f87979',
      //       data: [this.stat.items[i].distances["bikes"]]

      //         })

      // }
      var labels = [];
      var data = [];
      var span =''
      if (this.selection.groupBy=='month')
       span = "month";
      else span="date"
      var mean = this.selection.mean;
      if (this.stat && this.stat.items) {
        for (var i = 0; i < this.stat.items.length; i++) {
          labels.push(this.stat.items[i][span]);
          data.push(this.stat.items[i].distances[mean]);
        }
        this.datacollection = {
          labels: labels,
          datasets: [
            {
              label: mean,
              backgroundColor: "#f87979",
              data: data,
            },
          ],
        };
      }
      // this.datacollection = {
      //   labels: [this.getRandomInt(), this.getRandomInt()],
      //   datasets: [
      //     {
      //       label: 'Data One',
      //       backgroundColor: '#f87979',
      //       data: [this.getRandomInt(), this.getRandomInt()]
      //     }, {
      //       label: 'Data One',
      //       backgroundColor: '#f87979',
      //       data: [this.getRandomInt(), this.getRandomInt()]
      //     }
      //   ]
      // }
    },
    //   getRandomInt () {
    //     return Math.floor(Math.random() * (50 - 5 + 1)) + 5
    //   }
  },
};
</script>

<style>
.small {
  max-width: 600px;
  margin: 20px auto;
}
</style>
