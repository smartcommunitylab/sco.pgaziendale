<template>
  <div class="">
    <div class="px-8 bg-blue-600">
      <h1
        class="justify-self-center text-center text-white text-4xl pt-2 md:text-6xl font-semibold pb-6"
      >
        Le Mie Performance
      </h1>
      <div class="flex flex-col justify-center text-md">
        <context-menu
          ref="menuValue"
          class="mx-auto"
          @click.native="changeDataType()"
          v-bind:_options="[
            { name: 'CO2', view_name: 'CO2 Salvata', default: false },
            { name: 'KM', view_name: 'Kilometri', default: true },
            { name: 'trackCount', view_name: 'Viaggi Validi', default: false },
          ]"
        />
      </div>
      <div class="flex flex-col justify-center text-md">
        <context-menu
          ref="menuGroup"
          class="mx-auto"
          @click.native="changeGroup()"
          v-bind:_options="[
            { name: 'month', view_name: 'Mesi', default: false },
            { name: 'day', view_name: 'Giorni', default: true },
          ]"
        />
      </div>

      <div class="pt-2 text-md">
        <nav class="flex flex-row text-white">
          <button
            class="flex-1 py-2 px-6 block focus:outline-none font-medium sm:bg-green-400 hover:bg-blue-700"
            :class="
              mode == 'TAB' ? 'border-blue-300 border-b-4 text-blue-300' : ''
            "
            @click="changeMode('TAB')"
          >
            Tabella</button
          ><button
            class="flex-1 py-2 px-6 block focus:outline-none hover:bg-blue-700"
            :class="
              mode == 'GRAPH' ? ' border-blue-300 border-b-4 text-blue-300' : ''
            "
            @click="changeMode('GRAPH')"
          >
            Grafico
          </button>
        </nav>
      </div>
    </div>

    <div class="bg-opacity-0 py-2">
      <table
        v-show="mode == 'TAB'"
        class="table-fixed justify-center text-center w-full text-xl"
      >
        <thead>
          <tr>
            <th class="w-1/2">{{option_group_selected.view_name}}</th>
            <th class="w-1/2">{{ option_data_selected.view_name }}</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="(element,index) in stats" >
            <tr :key="index" >
              <td>{{ labels[index] }}</td>
              <td>{{ stats[index][option_data_selected.name] }}</td>
            </tr></template
          >
        </tbody>
      </table>
      <div id="chart_container" v-show="mode == 'GRAPH'" class="">
        <canvas ref="canvas" class=""></canvas>
      </div>
    </div>
  </div>
</template>

<script>
import ContextMenu from "../../Components/ContextMenu.vue";
import DataApi from "../../communication/dataApi";
import { MOMENT_DATE_FORMAT } from "../../variables";
import moment from "moment";
import Chart from "chart.js";

export default {
  components: { ContextMenu },
  name: "MyPerformance",
  data: function () {
    return {
      stats: [],
      labels:[],
      from: moment().subtract(7, "d").format(MOMENT_DATE_FORMAT),
      to: moment().format(MOMENT_DATE_FORMAT),
      groupBy: this.option_group_selected,
      withTracks: false,
      mode: "TAB",
      chart: undefined,
      option_data_selected: { name: "trackCount", view_name: "Viaggi Validi" },
      option_group_selected: {name: "day", view_name: "Giorni"},
    };
  },
  methods: {
    getData(campaignId, from, to, groupBy, withTracks) {
      return DataApi.getStats(campaignId, from, to, groupBy, withTracks);
    },
    buildConfig(labels) {
      // let kms = [23, 53, 22, 64, 34, 53, 23, 74];
      // let co2 = [2, 5, 3, 6, 4, 6, 1, 7];
      // let trips = [1, 3, 1, 5, 2, 3, 1, 5];

      // for (let i = 0; i < this.stats.length; i++) {
      //   this.stats.push({
      //     month: months[i],
      //     KM: kms[i],
      //     CO2: co2[i],
      //     TRIPS: trips[i],
      //   });
      // }

      //get the right data from stats
      var data = this.stats.map((stat,index) => {
        return {
               data:labels[index], 
               value:stat.trackCount}
        });
      // trackCount
      // co2saved
      // km
       return {
        type: "line",
        data: {
          labels: labels,
          datasets: [
            {
              label: "",
              borderColor: "rgb(25, 112, 183)",
              data: data,
              fill: false,
            },
          ],
        },
        options: {
          legend: { display: false },
          responsive: true,

          tooltips: {
            mode: "index",
            intersect: false,
          },
          hover: {
            mode: "nearest",
            intersect: true,
          },
          scales: {
            xAxes: [
              {
                display: true,
                scaleLabel: {
                  display: true,
                  labelString: this.option_group_selected.view_name,
                },
              },
            ],
            yAxes: [
              {
                display: true,
                scaleLabel: {
                  display: true,
                  labelString:this.option_data_selected.view_name,
                },
              },
            ],
          },
        },
      };
    },
    buildLabels(stats) {
      console.log(stats)
      var label = [];
      //cicle stats and return the label 
      if (this.groupBy=='months'){
        stats.forEach(elem => {
          label.push(elem.month)
        })
      } else {
                stats.forEach(elem => {
          label.push(elem.date);
        })
      }
      return label
    }, 
    buildChart(stats) {
      console.log(stats)
      this.labels = this.buildLabels(stats)
     

      let ctx = this.$refs.canvas;
      let config =this.buildConfig(this.labels );
      // let config = {
      //   type: "line",
      //   data: {
      //     labels: labels,
      //     datasets: [
      //       {
      //         label: "",
      //         borderColor: "rgb(25, 112, 183)",
      //         data: this.stats.map((element) => element.KM),
      //         fill: false,
      //       },
      //     ],
      //   },
      //   options: {
      //     legend: { display: false },
      //     responsive: true,

      //     tooltips: {
      //       mode: "index",
      //       intersect: false,
      //     },
      //     hover: {
      //       mode: "nearest",
      //       intersect: true,
      //     },
      //     scales: {
      //       xAxes: [
      //         {
      //           display: true,
      //           scaleLabel: {
      //             display: true,
      //             labelString: "Mesi",
      //           },
      //         },
      //       ],
      //       yAxes: [
      //         {
      //           display: true,
      //           scaleLabel: {
      //             display: true,
      //             labelString: "Km",
      //           },
      //         },
      //       ],
      //     },
      //   },
      // };
      this.chart = new Chart(ctx, config);
      //set default data
      // this.option_data_selected = { name: "KM", view_name: "Kilometri" };
    },
    getDataType() {
      return this.option_data_selected.name;
    },
    changeDataType: function () {
      let option = this.$refs["menuGroup"].getCurrentOption();
      if (option.name == this.option_data_selected.name) return;

      this.option_data_selected = option;
      this.changeCurrentOption(this.option_data_selected.name);
      console.log(option.name);
    },
    changeGroup: function () {
      let option = this.$refs["menuValue"].getCurrentOption();
      if (option.name == this.option_group_selected.name) return;

      this.option_group_selected = option;
      this.changeCurrentOption(this.option_group_selected.name);
      console.log(option.name);
    },
    changeCurrentOption(id) {
      this.updateGraph(id);
    },
    updateGraph(id) {
      //todo manage option group and data
      let data = this.stats.map((element) => element[id]);

      this.chart.data.datasets.pop();
      this.chart.data.datasets.push({
        label: "",
        borderColor: "rgb(25, 112, 183)",
        data: data,
        fill: false,
      });
      let label = "Kilometri";
      if (id == "CO2") label = "CO2 Salvata (Kg)";
      else if (id == "TRIPS") label = "# Viaggi validi";

      console.log(this.chart);
      console.log(label);
      this.chart.options.scales.yAxes[0].scaleLabel.labelString = label;
      this.chart.update();
    },
    changeMode(mode) {
      if (this.mode == mode) return;
      this.mode = mode;
    },
  },
  created: function () {
    //get stats with default value in creation hook
    this.getData(
      this.campagna.id,
      this.from,
      this.to,
      this.groupBy,
      this.withTracks
    ).then((stats) => {
      console.log(stats);
      this.stats = stats.data;
      this.buildChart(this.stats);
    });
  },
  computed: {
    campagna() {
      return this.$store.getters.campagna;
    },
  },
};
</script>

<style>
.group:hover .group-hover\:scale-100 {
  transform: scale(1);
}
.group:hover .group-hover\:-rotate-180 {
  transform: rotate(180deg);
}
.scale-0 {
  transform: scale(0);
}
.min-w {
  min-width: 10rem;
}

td {
  @apply py-2;
}
</style>
