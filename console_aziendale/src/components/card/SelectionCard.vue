<template>
  <v-card
    class="mx-2"
    max-width="300px"
    @click="method"
  >
    <v-img
      :src="backgroundImageUrl"
      height="75px"
      :class="{opacity: selected}"
    >
        <v-card-title>
            {{title}}
        </v-card-title>
    </v-img>

    
  </v-card>
</template>

<script>


export default {
    props: {
        title: String,
        backgroundImageUrl: String,
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