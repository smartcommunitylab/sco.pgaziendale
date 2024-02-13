<template>
  <div>
    <stats-selection></stats-selection>
    <stats-menagement></stats-menagement>
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
</style>
