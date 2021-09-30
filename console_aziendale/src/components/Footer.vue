<template>
  <v-footer
    padless
    class="up"
  >
  <v-row>
    <v-col cols="12" class="p-0 m-0">
    <v-card
      flat
      class="white--text d-flex mx-2"
    >
      <v-row class="riga-colore">
        <v-col cols="7">
          <v-card-text class="float-left">
            <strong>Play & Go © </strong> {{ new Date().getFullYear() }}
            design by <a class="link" href="https://www.fbk.eu/it/" target="_blank">FBK</a>. Nell'ambito del progetto: <a class="link" href="https://airbreakferrara.net/" target="_blank">Airbreak</a> e <a class="link" href="https://uia-initiative.eu/en" target="_blank">Urban Innovative Actions</a>
          </v-card-text>
        </v-col>
        <v-col>
          <div class="float-right mt-2">
            <v-btn
              v-for="icon in icons"
              :key="icon.name"
              class="mx-4 white--text "
              icon
              style="overflow:auto"
              :href="icon.link"
              target="_blank"
            >
              <v-icon size="24px">
                {{ icon.name }}
              </v-icon>
            </v-btn>
          </div>
        </v-col>
      </v-row>
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
        {name:'mdi-facebook', link:'https://www.facebook.com/FutureBuiltonKnowledge/'},
        {name:'mdi-instagram', link:'https://www.instagram.com/fbkresearch/?hl=it'},
        {name:'mdi-twitter', link:'https://twitter.com/FBK_research?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor'},
        {name:'mdi-linkedin', link:'https://www.linkedin.com/company/fbkresearch/?originalSubdomain=it'},
        {name:'mdi-youtube', link:'https://www.youtube.com/user/comunicazionefbk'},
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
  position: relative;
  z-index: -10;
}

.link {
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

.riga-colore{
  background-color: #212121;
}
</style>
