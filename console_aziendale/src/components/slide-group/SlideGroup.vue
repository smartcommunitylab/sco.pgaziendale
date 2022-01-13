<template>
  <v-sheet
    class="mx-auto"
  >
    <v-slide-group v-if="configurations"
      class=""
      prev-icon="mdi-chevron-left"
      next-icon="mdi-chevron-right"
      center-active
      show-arrows
    >
      <v-slide-item
        v-for="(item, index) in configurations.items"
        :key="index"
        v-slot="active"
      >
        <selection-card
          :selected="active ? false : true"
          :title="item.name"
          :id="item.id"
          class="ma-4"
          height="200"
          width="100"
        >
        </selection-card>
      </v-slide-item>
    </v-slide-group>
  </v-sheet>
</template>


<script>
import { mapState, mapActions } from "vuex";
import SelectionCard from "@/components/card/SelectionCard.vue";

  export default {
    props: {
        items: Array,
    },
    components: {
        "selection-card": SelectionCard,
    },
    computed:{
      ...mapState("stat", ["configurations"]),
      ...mapState("account", ["role","temporaryAdmin"])
    },
    methods: {
      ...mapActions("stat",{getConfigurationByRole:"getConfigurationByRole"}),

      loadConfiguration(){
        this.getConfigurationByRole({role:this.role,temporaryAdmin:this.temporaryAdmin});
        //console.log(this.configurations.items);
      },
    },
    mounted(){
      this.loadConfiguration();
    },
  }
</script>