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
          ref="menu"
          class="mx-auto"
          @click.native="changeDataType()"
          v-bind:_options="[
            { name: 'CO2', view_name: 'CO2 Salvata', default: false },
            { name: 'KM', view_name: 'Kilometri', default: true },
            { name: 'TRIPS', view_name: 'Viaggi Validi', default: false },
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
            class="flex-1 py-2 px-6 block  focus:outline-none hover:bg-blue-700"
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

    <div class=" bg-opacity-0 py-2 ">
      <table
        v-show="mode == 'TAB'"
        class="table-fixed justify-center text-center w-full text-xl"
      >
        <thead>
          <tr>
            <th class="w-1/2 ">Mesi</th>
            <th class="w-1/2 ">{{ option_selected.view_name }}</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="element in stats">
            <tr :key="element.month">
              <td>{{ element.month }}</td>
              <td>{{ element[getDataType()] }}</td>
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
import ContextMenu from "../Components/ContextMenu.vue";
export default {
  components: { ContextMenu },
  name: "MyPerformance",
  data: function() {
    return {
      stats: [],
      mode: "TAB",
      chart: undefined,
      option_selected: {},
    };
  },
  methods: {
    getDataType() {
      return this.option_selected.name;
    },
    changeDataType: function() {
      let option = this.$refs["menu"].getCurrentOption();
      if (option.name == this.option_selected) return;

      this.option_selected = option;
      this.changeCurrentOption(this.option_selected.name);
      console.log(option.name);
    },
    changeCurrentOption(id) {
      this.updateGraph(id);
    },
    updateGraph(id) {
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
  mounted: function() {
    let months = [
      "Gennaio",
      "Febbraio",
      "Marzo",
      "Aprile",
      "Maggio",
      "Giugno",
      "Luglio",
      "Agosto",
    ];
    let kms = [23, 53, 22, 64, 34, 53, 23, 74];
    let co2 = [2, 5, 3, 6, 4, 6, 1, 7];
    let trips = [1, 3, 1, 5, 2, 3, 1, 5];

    for (let i = 0; i < kms.length; i++) {
      this.stats.push({
        month: months[i],
        KM: kms[i],
        CO2: co2[i],
        TRIPS: trips[i],
      });
    }

    let Chart = require("chart.js");
    let ctx = this.$refs.canvas;

    let config = {
      type: "line",
      data: {
        labels: months,
        datasets: [
          {
            label: "",
            borderColor: "rgb(25, 112, 183)",
            data: this.stats.map((element) => element.KM),
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
                labelString: "Mesi",
              },
            },
          ],
          yAxes: [
            {
              display: true,
              scaleLabel: {
                display: true,
                labelString: "Km",
              },
            },
          ],
        },
      },
    };
    this.chart = new Chart(ctx, config);

    this.option_selected = { name: "KM", view_name: "Kilometri" };
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
