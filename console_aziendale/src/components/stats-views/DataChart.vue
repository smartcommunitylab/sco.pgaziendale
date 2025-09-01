<template>
<!--

Visualization:
- separate graph for each data column
- separate group of graphs for each mean if group by means
- line chart for timegroup day, week, month, year
- [pie chart] for timegroup total
- barchart for timegroup dayOfWeek or hour

-->
<div>
  <v-row v-for="(group, index) in groups" :key="index">
    <v-col v-for="chart in group" :key="chart.id" :cols="chart.width">
      <Plot :data="chart.data" :layout="chart.layout" />
    </v-col> 
  </v-row>  
</div>
</template>



<script>
  import Plot from './Plot';

  function meanLabel(label, mean) {
    switch (mean) {
        case 'bike':
            return label.replace('_mezzo_', 'bici');
        case 'car':
            return label.replace('_mezzo_', 'auto');
        case 'train':
            return label.replace('_mezzo_', 'treno');
        case 'walk':
            return label.replace('_mezzo_', 'piedi');
        case 'bus':
            return label.replace('_mezzo_', 'autobus');
        case 'boat':
            return label.replace('_mezzo_', 'barca');
        default:
            return label;
    }
  }


  export default {
    props:{
      dataChartData: Object,
      configuration: Object
    },
    components: {
      Plot
    },
    data () {
      return {
        groups: [],
        chartData: [{
          x: [1, 2, 3, 4, 5],
          y: [10, 15, 13, 17, 22],
          type: 'scatter'
        }],
        chartLayout: {
          title: 'Sample Chart'
        }
      }
    },
    methods: {
      initCharts() {
        console.log('initCharts', this.dataChartData);
        if (this.configuration.groupByMean) {
          const means = this.configuration.means || this.configuration.campaign.means;
          this.groups = means.map(m => this.createGroup(m));
        } else {
          this.groups = [this.createGroup()];
        }
        console.log(this.groups);
      },
      createGroup(mean) {
        const elems = this.configuration.dataColumns.map(dc => this.createChart(dc, mean));
        const width = elems.length <=4 ? 12 / elems.length : 3;
        elems.forEach(element => element.width = width);
        return elems;
      },
      createChart(dc, mean) {
        const chart = {id: dc.apiField + (mean ? '_' + mean : '')};
        switch (this.configuration.timeUnit.apiField) {
          case "day":
          case "week":
          case "month":
          case "year":
            chart.type = "scatter";
            break;  
          case "total":
            chart.type = "pie";
            break;  
          case "dayOfWeek":
          case "hour":
            chart.type = "bar";
            break; 
        }
        chart.title = dc.label;
        if (mean) {
          chart.title = meanLabel(dc.meanLabel, mean);
        }    
        const prefix = mean ? ( mean  + '_mean_' + dc.apiField) : dc.apiField;
        if (chart.type === 'pie') {
          chart.data = [{
            labels: this.dataChartData.data.map(row => row.name),
            values: this.dataChartData.data.map(row => row[prefix]),
            type: 'pie'
          }];
        } else {
          chart.data = this.dataChartData.data.map(row => this.row2chartData(row, this.dataChartData.headers, prefix, chart.type));
        }
        chart.layout = {
          title: {text: chart.title}
        }
        if (chart.type === 'bar') {
          chart.layout.barmode = 'stack';
        }
        return chart;
      },
      row2chartData(row, headers, prefix, chartType) {
        return {
          x: headers,
          y: Object.keys(row).filter(k => k.startsWith(prefix)).map(k => row[k]),
          type: chartType,
          name: row.name
        }
      }
    },
    watch: {
      dataChartData: {
        handler() {
          console.log('data');
          this.initCharts();
        },
        deep: true
      }

    },
    mounted() {
      console.log('mounted');
      this.initCharts();
    }
  }
</script>