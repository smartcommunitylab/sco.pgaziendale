<template>
  <v-col cols="4">
    <v-card elevation="2">
        <v-card-title>{{ actualLocation.item.id }}</v-card-title>
        
        <!-- TODO: MAPPA GMAP -->
        <l-map
          ref="myMap"
          @ready="initMap()"
          :zoom="zoom"
          :center="center"
          :options="mapOptions"
          class="map-style"
        >
          <l-tile-layer :url="url" :attribution="attribution" />
          <l-circle
            v-if="actualLocation.item.latitude && actualLocation.item.longitude"
            :lat-lng.sync="center"
            :radius="actualLocation.item.radius"
            :color="'red'"
          />
          <l-marker
            v-if="actualLocation.item.latitude && actualLocation.item.longitude"
            visible
            :lat-lng="center"
          >
            <l-popup>
              <div>
                {{ actualLocation.item.id }}
              </div>
            </l-popup>
          </l-marker>
        </l-map>
        <!-- FINE MAPPA GMAP -->

        <v-card-text class="pb-0">
            <v-list dense>
                <v-list-item-group
                    color="primary"
                >
                    <v-list-item :href="'http://maps.google.com/?q='+actualLocation.item.address + ' ' + actualLocation.item.streetNumber + ', ' + actualLocation.item.city + ', ' + actualLocation.item.zip" target="_blank">
                        <v-list-item-icon>
                            <v-icon>mdi-map-marker</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="actualLocation.item.address + ' ' + actualLocation.item.streetNumber + ', ' + actualLocation.item.city + ', ' + actualLocation.item.zip"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item :href="'http://maps.google.com/?q='+actualLocation.item.latitude + ' ' + actualLocation.item.longitude" target="_blank">
                        <v-list-item-icon>
                            <v-icon>mdi-crosshairs-gps</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title v-text="actualLocation.item.latitude + '; ' + actualLocation.item.longitude"></v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item>
                        <v-list-item-icon>
                            <v-icon>mdi-calendar</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title>Giorni della settimana non lavorativi:
                              <div v-html="getNonWorking(actualLocation.item.nonWorking)"></div>
                            </v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                    <v-list-item>
                        <v-list-item-icon>
                            <v-icon>mdi-calendar</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title>
                               Giorni non lavorativi:
                              <div v-html="getNonWorkingDays(actualLocation.item.nonWorkingDays)"></div>
                            </v-list-item-title>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>
            </v-list>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>

            <v-btn icon @click="editLocation">
                <v-icon>mdi-pencil</v-icon>
            </v-btn>

            <v-btn icon @click="deleteLocation">
                <v-icon>mdi-delete</v-icon>
            </v-btn>

        </v-card-actions>
    </v-card>
  </v-col>
</template>
<script>
import { mapState, mapActions } from "vuex";
import EventBus from "../../components/eventBus";
import { latLng } from "leaflet";
import { locationService } from "../../services";
import moment from "moment";

export default {
  name: "ProfiloLocation",
  data() {
    return {
      zoom: 13,
      url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
      attribution:
        '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
      currentZoom: 11.5,
      showParagraph: false,
      mapOptions: {
        zoomSnap: 0.5,
      },
    };
  },
  computed: {
    ...mapState("location", ["actualLocation"]),
    center() {
      return latLng(
        this.actualLocation.item.latitude,
        this.actualLocation.item.longitude
      );
    },
  },
  methods: {
    ...mapActions("location", { changeActualLocation: "changeActualLocation" }),
    deleteLocation() {
      EventBus.$emit("DELETE_LOCATION", this.actualLocation);
    },
    editLocation() {
      EventBus.$emit("EDIT_LOCATION", this.actualLocation);
    },
    initMap() {},
    getNonWorking(days) {
      var returnDays = "";
      if (days)
        days.forEach((element) => {
          returnDays += locationService.getDayByInt(element) + "<br>";
        });
      return returnDays;
    },
    getNonWorkingDays(days) {
      var returnDays = "";
      if (days)
        days.forEach((element) => {
          returnDays += moment(element).format('DD-MM-YYYY') + "<br>";
        });
      return returnDays;
    },
  },
};
</script>

<style scoped>
.map-style{
  height: 350px;
  width: 100%;
  position: relative;
  z-index: 1;
}
</style>
