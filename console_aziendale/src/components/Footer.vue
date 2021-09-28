<template>
  <v-footer
    padless
    class="up"
  >
  <v-row>
    <v-col cols="12" class="p-0 m-0">
    <v-card
      flat
      dark
      class="white--text d-flex mx-2"
    >
      
        <v-card-text class="float-left">
          <strong>Play & Go © </strong> {{ new Date().getFullYear() }}
          design by FBK - nell'ambito del progetto: Airbreak e Urban Innovative Actions
        </v-card-text>
        
        <div class="float-right mt-2" style="width:340px">
          <v-btn
            v-for="icon in icons"
            :key="icon"
            class="mx-4 white--text "
            icon
            style="overflow:auto"
          >
            <v-icon size="24px">
              {{ icon }}
            </v-icon>
          </v-btn>
        </div>
        
    </v-card>
    </v-col>
  </v-row>
  </v-footer>
</template>

<script>
export default {
  data() {
    return {
      date: new Date().getFullYear(),
      icons: [
        'mdi-facebook',
        'mdi-twitter',
        'mdi-linkedin',
        'mdi-instagram',
      ],
    }
  },
    methods: {
    openTab(url){
      window.open(url, '_blank');
    }
  },
}
</script>
<style>

.up {
  z-index: 50;
}

</style>
