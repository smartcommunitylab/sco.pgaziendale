<template>
  <div>
    <div class="w-full max-w-4xl flex h-full flex-wrap mx-auto my-32 lg:my-0 lg:mr-16">
      <div
        id="profile"
        class="min-w-full w-full lg:w-3/5 rounded-lg lg:rounded-l-lg lg:rounded-r-none bg-white opacity-75 mx-6 lg:mx-0"
      >
        <div class="w-full">
          <button
            @click="deleteLocation"
            class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
          >
            <delete-icon />
          </button>
          <button
            @click="editLocation"
            class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
          >
            <pencil-outline-icon />
          </button>
        </div>
        <div class="p-4 md:p-12 text-center lg:text-left">
          <h1 class="text-3xl font-bold pt-8 lg:pt-0">{{ actualLocation.item.id }}</h1>
          <div
            class="mx-auto lg:mx-0 w-4/5 pt-3 border-b-2 border-green-500 opacity-25"
          ></div>
          <p
            class="pt-4 text-base font-bold flex items-center justify-center lg:justify-start"
          >
            <address-icon />Indirizzo: {{ actualLocation.item.address }}
            {{ actualLocation.item.streetNumber }} {{ actualLocation.item.zip }}
            {{ actualLocation.item.city }}
          </p>

          <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
          >
            <gps-icon /> Coordinate: {{ actualLocation.item.latitude }},
            {{ actualLocation.item.longitude }}
          </p>
          <l-map
            ref="myMap"
            @ready="initMap()"
            :zoom="zoom"
            :center="center"
            :options="mapOptions"
            style="height: 350px; width: 100%"
          >
            <l-tile-layer :url="url" :attribution="attribution" />
            <l-marker :lat-lng="withPopup">
              <l-popup>
                <div>
                  {{ actualLocation.item.id }}
                </div>
              </l-popup>
            </l-marker>
          </l-map>
          <div
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
          v-if="(actualLocation.item.nonWorking && actualLocation.item.nonWorking[0]!=0)">
            <calendar-remove-icon />Giorni della settimana non lavorativi: <div v-html="getNonWorking(actualLocation.item.nonWorking)"></div>
          </div>
          <div
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
           v-if="actualLocation.item.nonWorkingDays">
            <calendar-remove-icon /> Giorni non lavorativi: <div v-html="getNonWorkingDays(actualLocation.item.nonWorkingDays)"></div>
          </div>
        </div>
      </div>
      <div></div>
    </div>
  </div>
</template>
<script>
import { mapState, mapActions } from "vuex";
import EventBus from "../../components/eventBus";
import { latLng } from "leaflet";
import { locationService } from "../../services";
 
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
    withPopup() {
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
    initMap() {

    },
    getNonWorking(days){
      var returnDays="";
      if (days)
      days.forEach(element => {
              returnDays+=locationService.getDayByInt(element)+ "<br>";

      });
      return returnDays;
    } ,
getNonWorkingDays(days){
        var returnDays="";
        if (days)
      days.forEach(element => {
              returnDays+=element+ "<br>";

      });
      return returnDays;
}  },
};
</script>

<style scoped></style>
