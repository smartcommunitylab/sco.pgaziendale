<template>
  <v-sheet
    class="mx-auto"
  >
    <v-slide-group
      class=""
      prev-icon="mdi-chevron-left"
      next-icon="mdi-chevron-right"
      center-active
      show-arrows
    >
      <v-slide-item
        v-for="(item, index) in items"
        :key="index"
        v-slot="{ active, toggle }"
      >
        <selection-card
          :selected="active ? false : true"
          :title="item.title"
          :description="item.description"
          :method="item.method"
          class="ma-4"
          height="200"
          width="100"
          @click="toggle"
        >
        </selection-card>
      </v-slide-item>
    </v-slide-group>
  </v-sheet>
</template>


<script>
import SelectionCard from "@/components/card/SelectionCard.vue";

  export default {
    props: {
        items: Array,
    },
    components: {
        "selection-card": SelectionCard,
    },
  }
</script>