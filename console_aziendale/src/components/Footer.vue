<template>
  <v-footer
    padless
    class="justify-center up"
  >
  <v-row>
    <v-col cols="12" class="p-0 m-0">
    <v-card
      flat
      dark
      class="white--text text-center justify-center"
    >
      <v-card-text>
        <v-btn
          v-for="icon in icons"
          :key="icon"
          class="mx-4 white--text"
          icon
        >
          <v-icon size="24px">
            {{ icon }}
          </v-icon>
        </v-btn>
      </v-card-text>

      <v-card-text class="white--text pt-0">
        Sviluppato da FBK (Fondazione Bruno Kessler)
        Nell'ambito del progetto - Airbreak - Urban Innovative Actions
      </v-card-text>

      <v-divider></v-divider>

      <v-card-text class="white--text">
        {{ new Date().getFullYear() }} — <strong>Play & Go</strong>
      </v-card-text>
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
