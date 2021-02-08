<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2">
      <h1>Sedi</h1>
      <table class="shadow-lg rounded relative w-full">
        <thead class="text-center justify-between">
          <tr class="truncate px-2 flex border-b border-background text-center">
            <th class="w-1/2">Cittá</th>

            <th class="w-1/2">Indirizzo</th>
          </tr>
        </thead>
        <tbody class="bg-white" v-if="allLocations && allLocations.items">
          <template v-for="location in allLocations.items">
            <tr
              class="select-none cursor-pointer flex border-b border-background hover:bg-background transition ease-in duration-100"
              :key="location.id"
              tag="tr"
              @click="showLocationInfo(location)"
              :id="location.id"
            >
              <td class="w-1/2">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ location.city }}
                </p>
              </td>
              <td class="w-1/2">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ location.address }}{{ location.streetNumber }}
                </p>
              </td>

              <td class="w-1/6">
                <pencil-outline-icon />
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
    <profilo-location v-if="actualLocation" />
  </div>
</template>

<script>
import ProfiloLocation from "../components/ProfiloLocation.vue";
import { mapState, mapActions } from "vuex";

export default {
  name: "Locations",
  components: { ProfiloLocation },
  data: function () {
    return {};
  },
  created() {
    this.loadLocations();
  },
  mounted() {},
  computed: {
    ...mapState("location", ["allLocations", "actualLocation"]),
    ...mapState("company", ["actualCompany"]),
    ...mapState("account", ["role"]),
  },
  methods: {
    ...mapActions("location", {
      getAllLocations: "getAllLocations",
      selectActualLocation: "selectActualLocation",
    }),
    loadLocations() {
      if (this.actualCompany) this.getAllLocations(this.actualCompany.item.id);
    },

    showLocationInfo(location){
      this.selectActualLocation(location)
    }
  },
};
</script>

<style></style>
