<template>
  <div>
    <stats-selection></stats-selection>
    <v-row>
      <v-col cols="12">
        <slide-group :items="selectionCards"></slide-group>
      </v-col>
    </v-row>
    <stats-menagement></stats-menagement>
  </div>
</template>

<script>
import { mapState /*, mapActions*/ } from "vuex";
import SlideGroup from "@/components/slide-group/SlideGroup.vue";
import StatsMenagement from "@/pages/stats/StatsMenagement.vue";
import StatsSelection from "@/pages/stats/StatsSelection.vue";

export default {
  components: {
    "slide-group": SlideGroup,
    "stats-menagement": StatsMenagement,
    "stats-selection":StatsSelection
  },
  data: function () {
    return {
      selectionCards: [
        {
          title: "Km fatti e utili",
          description: "Visualizzazione Km totali fatti per unitá temporale",
          method: this.pippo,
        },
        {
          title: "Partecipazione dipendenti",
          description: "Registrazioni, iscrizioni e partecipazione dei dipendenti",
          method: this.pippo,
        },
        {
          title: "Impatto ambientale",
          description: "Prova prova prova",
          method: this.pippo,
        },
        {
          title: "Impatto ambientale",
          description: "Prova prova prova",
          method: this.pippo,
        },
        {
          title: "Impatto ambientale",
          description: "Prova prova prova",
          method: this.pippo,
        },
        {
          title: "Impatto ambientale",
          description: "Prova prova prova",
          method: this.pippo,
        },
      ],
    };
  },
  methods: {},
  computed: {
    ...mapState("stat", ["configurations"]),
    nColsCalculator: function () {
      if (this.isOpenSidebar) {
        return 9;
      } else {
        return 12;
      }
    },
  },
  
};
</script>

<style>
.customButton {
  top: 30px;
}
</style>
