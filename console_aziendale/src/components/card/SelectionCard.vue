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
        :disabled="disabled"
        @click.prevent="selectConfiguration()"
      >
        <span v-if="!disabled">Seleziona</span><span v-else>Selezionato</span>
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
    props: {
        title: String,
        description: String,
        id: Number,
        selected: Boolean,
    },  
    data(){
      return {
        disabled: false,
      }
    },
    computed:{
      ...mapState("stat", ["activeConfiguration"]),
    },
    methods: {
      ...mapActions("stat",{setActiveConfiguration:"setActiveConfiguration"}),
      selectConfiguration(){
        this.setActiveConfiguration({configurationId: this.id})
      },
      setDisabledButton(){
        this.disabled = true;
      }
      
    },
    watch: {
      activeConfiguration(){
        this.disabled = false;
        if(this.activeConfiguration.items == this.id){
          this.setDisabledButton();
        }
      }
    }
}
</script>


<style scoped>
.opacity {
    opacity: 100;
}
</style>