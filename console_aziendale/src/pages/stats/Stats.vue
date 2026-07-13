<template>
  <div>
    <div class="sticky-header pa-2">
      <stats-selection></stats-selection>
    </div>    <stats-menagement></stats-menagement>
  </div>
</template>

<script>
import StatsMenagement from "@/pages/stats/StatsMenagement.vue";
import StatsSelection from "@/pages/stats/StatsSelection.vue";

export default {
  components: {
    "stats-menagement": StatsMenagement,
    "stats-selection":StatsSelection
  },
  data: function () {
    return {
    }
  },
  methods: {},  
};
</script>

<style>
.customButton {
  top: 30px;
}
.sticky-header {
  position: -webkit-sticky; 
  position: sticky;
  top: 64px; 
  z-index: 3; 
  background-color: #FFFFFF; 
  border-bottom: 2px solid #E0E0E0; 
}
</style>
