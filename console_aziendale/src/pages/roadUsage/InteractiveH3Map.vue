<template>
    <l-map style="height: 100vh; width: 100%;" :zoom="zoom" :center="center" @click="emitClick">
      <l-tile-layer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"></l-tile-layer>
      
      <l-geo-json 
        v-if="geoData" 
        :geojson="geoData" 
        :options="geoJsonOptions"
      ></l-geo-json>
    </l-map>
  </template>
  
  <script>
  import { LMap, LTileLayer, LGeoJson } from 'vue2-leaflet';
  
  export default { 
    components: { LMap, LTileLayer, LGeoJson },
    props: ['geoData'],
    data() {
      return {
        zoom: 12,
        center: [46.06, 11.12], // Centro su Trento base
      };
    },
    mounted() {
  console.log("Mappa montata! Dati ricevuti:", this.geoData);
},
    computed: {
      geoJsonOptions() {
        return {
          // Funzione per colorare i poligoni in base al valore (es: più scuro = più durata)
          style: (feature) => {
            const val = feature.properties.avg_duration || 0;
            return {
              weight: 2,
              color: '#ECEFF1',
              opacity: 1,
              fillColor: val > 3600 ? '#d32f2f' : '#1976d2',
              fillOpacity: 0.7
            };
          },
          // Aggiungi un tooltip/popup hover
          onEachFeature: (feature, layer) => {
            const props = feature.properties;
            layer.bindTooltip(`
              <b>Cella:</b> ${props.h3_end_parent}<br>
              <b>Tracce:</b> ${props.tracks}<br>
              <b>Durata Media:</b> ${props.avg_duration ? props.avg_duration.toFixed(2) : '-'} s
            `);
          }
        };
      }
    },
    methods: {
      emitClick(e) {
        this.$emit('map-click', e.latlng);
      }
    }
  };
  </script>