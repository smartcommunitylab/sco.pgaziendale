<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6"><h1>Aziende</h1></div>
    <infobox />
  </div>
</template>

<script>
import Infobox from "../components/Infobox.vue";
import { companies } from "../tmp-data/companies";

export default {
  components: { Infobox },
  name: "Aziende",

  data: function() {
    return {
      companies: [],
    };
  },
  mounted: function() {
    this.companies = companies;
  },
};
</script>

<style></style>
