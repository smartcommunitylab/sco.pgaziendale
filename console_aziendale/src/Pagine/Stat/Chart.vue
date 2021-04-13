<template>
  <div class="small" v-if="datacollection">
    <line-chart :chart-data="datacollection"></line-chart>
  </div>
</template>

<script>
import LineChart from "./LineChart.js";
import { mapState } from "vuex";
import moment from "moment";
import { campaignService } from '../../services';

export default {
  components: {
    LineChart,
  },
  props: ["selection"],
  data() {
    return {
      datacollection: null,
      means:[]
    };
  },
  computed: {
    ...mapState("account", ["role"]),
    ...mapState("stat", ["stat"]),
  },
  mounted() {
    this.fillData();
    this.means=campaignService.getArrayMeans()
  },
  watch: {
    stat() {
      // Our fancy notification (2).
      console.log(this.stat);
      if (this.stat && this.stat.items) this.fillData();
    },
  },
  methods: {
    format(string,span){
      if (span=="date")
        return moment(string).format('DD-MM-YYYY')
        if (span =="month")
                return moment(string).format('MM-YYYY')

    },
    fillData() {
      var labels = [];
      var data = [];
      var span =''
      if (this.selection.groupBy=='month')
       span = "month";
      else span="date"
      var mean = this.means.find((el) => {return el.value == this.selection.mean;})
      if (this.stat && this.stat.items && mean) {
        for (var i = 0; i < this.stat.items.length; i++) {
          labels.push(this.format(this.stat.items[i][span],span));
          data.push(this.stat.items[i].distances[mean.value]);
        }
        this.datacollection = {
          labels: labels,
          datasets: [
            {
              label: mean? mean.text:"",
              backgroundColor: "#f87979",
              data: data,
            },
          ],
        };
      }
    },
  },
};
</script>

<style>
.small {
  max-width: 600px;
  margin: 20px auto;
}
</style>
