<template>
  <l-map
    ref="map"
    class="map-style"
    @click="onMapClick"
    @ready="initMap()"
    :zoom="zoom"
    :center="[
      position.lat || userLocation.lat || defaultLocation.lat,
      position.lng || userLocation.lng || defaultLocation.lng,
    ]"
  >
    <l-tile-layer :url="tileProvider.url" :attribution="tileProvider.attribution" />
    <v-geosearch :options="geoSearchOptions"></v-geosearch>
    <l-circle
      v-if="position.lat && position.lng && dragging == false"
      :lat-lng.sync="position"
      :radius="radius"
      :color="'red'"
    />
    <l-marker
      v-if="position.lat && position.lng"
      visible
      draggable
      :lat-lng.sync="position"
      @dragstart="dragging = true"
      @dragend="dragging = false"
    >
      <l-tooltip :content="tooltipContent" :options="{ permanent: true }" />
    </l-marker>
  </l-map>
</template>

<script>
import { LMap, LMarker, LTileLayer, LTooltip } from "vue2-leaflet";
import { OpenStreetMapProvider } from "leaflet-geosearch";
import GeoSearch from '@/components/leaflet-map/Geosearch.vue'

export default {
  name: "LocationInput",

  components: {
    LMap,
    LTileLayer,
    LMarker,
    LTooltip,
    "v-geosearch": GeoSearch
  },

  props: {

    latLng: {
      lat: Number,
      lng: Number,
    },

    radius: {
      type: Number,
      deafult: 0,
    },

    value: {
      type: Object,
      required: true,
    },

    defaultLocation: {
      type: Object,
      default: () => ({
        lat: 41.902782,
        lng: 12.496366,
      }),
    },
  },

  data() {
    return {
      loading: false,
      geoSearchOptions: {
        provider: new OpenStreetMapProvider(),
        animateZoom: true,
        showMarker: false,
        autoClose: true,
        style: 'bar',
        searchLabel: 'Inserisci l\'indirizzo'
      },
      userLocation: {},
      position: {},
      address: "",
      tileProvider: {
        attribution:
          '&copy; <a target="_blank" href="http://osm.org/copyright">OpenStreetMap</a> contributors',
        url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
      },
      zoom: 12,
      dragging: false,
    };
  },

  methods: {
    initMap(location) {
      setTimeout(() => {
        if (location)
          {this.position ={ lat: location.latitude, lng: location.longitude };
        this.zoom=location.radius;}
        this.$refs.map.mapObject.invalidateSize();
      }, 250);
    },
    getStringAddress(structuredValue) {
      var returnAddress = "";
      if (structuredValue.amenity) returnAddress += "<br />" + structuredValue.amenity;
      if (structuredValue.office) returnAddress += "<br />" + structuredValue.office;
      if (structuredValue.road) returnAddress += "<br />" + structuredValue.road;
      if (structuredValue.house_number) returnAddress += ", " + structuredValue.house_number;
      if (structuredValue.city) returnAddress += "<br />" + structuredValue.city;
      if (structuredValue.country) returnAddress += "<br />" + structuredValue.country;
      if (structuredValue.postcode) returnAddress += "<br />" + structuredValue.postcode;
      if (structuredValue.state) returnAddress += "<br />" + structuredValue.state;
      if (structuredValue.county) returnAddress += "<br />" + structuredValue.county;
      return returnAddress;
    },
    async getAddress() {
      this.loading = true;
      let address = {
        string: "Unresolved address",
        structuredValue: {},
        pos: {
          lat: 0,
          lng: 0,
        },
      };
      try {
        const { lat, lng } = this.position;
        const result = await fetch(
          `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`
        );
        if (result.status === 200) {
          const body = await result.json();
          address.string = body.display_name;
          address.structuredValue = body.address;
          address.pos = {
            lat: lat,
            lng: lng,
          };
        }
      } catch (e) {
        console.error("Reverse Geocode Error->", e);
      }
      this.loading = false;
      return address;
    },
    async onMapClick(value) {
      this.position = value.latlng;
    },
    onSearch(value) {
      console.log(value);
      const loc = value.location;
      this.position = { lat: loc.y, lng: loc.x };
    },
    async getUserPosition() {
      if (navigator.geolocation) {
        // get GPS position
        navigator.geolocation.getCurrentPosition((pos) => {
          // set the user location
          this.userLocation = {
            lat: pos.coords.latitude,
            lng: pos.coords.longitude,
          };
        });
      }
    },
  },

  computed: {
    tooltipContent() {
      if (this.dragging) return "...";
      if (this.loading) return "Loading...";
      return `<strong>${
        this.address && this.address.structuredValue
          ? this.getStringAddress(this.address.structuredValue)
          : ""
      }</strong> <hr/><strong>lat:</strong> ${
        this.position.lat
      }<br/> <strong>lng:</strong> ${this.position.lng}`;
    },
  },

  watch: {
    position: {
      deep: true,
      async handler(value) {
        this.address = await this.getAddress();
        this.$emit("poschanged", { position: value, address: this.address });
      },
    },

    // latLng(){
    //   //Fai una query per latlong come search
    //   console.log(this.latLng)
    //   this.position = this.latLng;
    // },
  },

  mounted() {
    this.getUserPosition();
    this.$refs.map.mapObject.on("geosearch/showlocation", this.onSearch);
    this.$refs.map.mapObject.invalidateSize();
  },
};
</script>

<style scoped>
.map-style{
  border: solid 1px;
  border-radius: 8px;
}
</style>
