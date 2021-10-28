<template>
  <v-card
    class="mx-2"
    max-width="300px"
    min-width="300px"
  >

    <v-card-title class="mb-0 pb-0">
      {{title}}
    </v-card-title>

    <v-card-text class="mb-0 pb-0">{{description}}</v-card-text>

    

    <v-card-actions class="pt-0">
      <v-btn
        class="pt-0"
        text
        color="primary"
        @click="method"
      >
        Seleziona
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>


export default {
    props: {
        title: String,
        description: String,
        method: Function,
        selected: Boolean,
    },   
}
</script>


<style scoped>
.opacity {
    opacity: 100;
}
</style>