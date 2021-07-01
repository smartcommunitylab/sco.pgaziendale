<template>
  <div class="small" v-if="datacollection">
    <div class="title">{{title}}</div>
    <line-chart :chart-data="datacollection" ></line-chart>
  </div>
</template>

<script>
import LineChart from "./LineChart.js";
import { mapState } from "vuex";
import moment from "moment";
import { campaignService } from "../../services";

export default {
  components: {
    LineChart,
  },
  props: ["selection"],
  data() {
    return {
      datacollection: null,
      means: [],
      title:""
    };
  },
  computed: {
    ...mapState("account", ["role"]),
    ...mapState("stat", ["stat"]),
  },
  mounted() {
    this.means = campaignService.getArrayMeans();
    this.fillData();
  },
  watch: {
    stat() {
      console.log(this.stat);
      if (this.stat && this.stat.items) this.fillData();
    },
  },
  methods: {
    format(string, span) {
      if (span == "date") return moment(string).format("DD-MM-YYYY");
      if (span == "month") return moment(string).format("MM-YYYY");
    },
    createTitle() {
      var title="";
      switch (this.selection.selectedType) {
        case "km_valid":
          title="Km validi"
          break;
        case "km_true":
          title="Km effettivi"
          break;
        case "co2saved":
          title="CO2 salvata"
          break;
        case "trackCount":
          title="Numero di viaggi"
          break;
        default:
          break;
      }

      return title;
    },
    fillData() {
      var labels = [];
      var data = [];
      var span = "";
      if (this.selection.groupBy == "month") span = "month";
      else span = "date";
      var mean = this.means.find((el) => {
        return el.value == this.selection.mean;
      });
      this.title=this.createTitle();
      if (this.stat && this.stat.items && mean) {
        if (
          this.selection.selectedType == "km_valid" ||
          this.selection.selectedType == "km_true"
        )
          {
          for (let i = 0; i < this.stat.items.length; i++) {
          labels.push(this.format(this.stat.items[i][span], span));
          data.push(this.stat.items[i].distances[mean.value]/1000);
        }
          }
        else {
          for (let i = 0; i < this.stat.items.length; i++) {
          labels.push(this.format(this.stat.items[i][span], span));
          data.push(this.stat.items[i][this.selection.selectedType]);

        }
        } 

        this.datacollection = {
          labels: labels,
          datasets: [
            {
              label: mean ? mean.text : "",
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
.title{
      text-align: center;
    font-size: x-large;
    font-weight: bold;
}
</style>
