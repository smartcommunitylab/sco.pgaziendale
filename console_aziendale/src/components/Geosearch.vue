<template>
  <div>
  </div>
</template>

<script>
// fix problem with search and click on the item
import { GeoSearchControl } from 'leaflet-geosearch';

export default {
  props: {
    options: {
      required: true
    },
  },

  name: 'v-geosearch',

  methods: {
    deferredMountedTo(parent) {
      const searchControl = new GeoSearchControl(this.options);
      parent.addControl(searchControl);      
      searchControl.getContainer().onclick = e => { e.stopPropagation(); };
    },
    remove() {
      if (this.markerCluster) {
        this.$parent.removeLayer(this.markerCluster);
      }      
    },
    add() {
      if (this.$parent._isMounted) {
        this.deferredMountedTo(this.$parent.mapObject);
      }
    },
  },

  mounted() {
    this.add();
  },

  beforeDestroy() {
    this.remove();
  },
};
</script>