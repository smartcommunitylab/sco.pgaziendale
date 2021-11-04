<template>
  <v-card
    class="mx-2"
    max-width="300px"
    min-width="300px"
  >

    <v-card-title class="mb-2 pb-0">
      {{title}}
    </v-card-title>
    
    <v-card-actions class="pt-0">
      <v-btn
        class="pt-0"
        text
        color="primary"
        @click.prevent="selectConfiguration()"
      >
        Seleziona
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import { mapActions } from "vuex";

export default {
    props: {
        title: String,
        description: String,
        id: Number,
        selected: Boolean,
    },  
    methods: {
      ...mapActions("stat",{setActiveConfiguration:"setActiveConfiguration"}),
      selectConfiguration(){
        this.setActiveConfiguration({configurationId: this.id})
      }
      
    } 
}
</script>


<style scoped>
.opacity {
    opacity: 100;
}
</style>