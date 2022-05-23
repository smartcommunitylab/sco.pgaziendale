<!-- 
DESCRIZIONE:
Il "Snackbar.vue" è un componente dormiente posto in primo piano dell'applicativo. Il suo scopo è,
quando chiamato e avviato, gestire i messaggi lanciati dall'applicativo e farli visualizzare
all'utente come una snackbar.
-->
<template>
  <v-snackbar v-model="snackbar" class="up" :color="type=='success'?'':type" :timeout="5000">
    {{ message }}
    <v-btn :color="type=='success'? 'primary': 'alert'" text @click="clearAlert"> Chiudi </v-btn>
  </v-snackbar>
</template>

<script>
import { mapActions, mapState } from "vuex";

export default {
  data() {
    return {
      snackbar: false,
    };
  },
  methods: {
    ...mapActions("alert", { clearAlert: "clear" }),
  },
  computed: {
    ...mapState("alert", ["message","type"]),
  },
  watch: {
    message(newMsg) {
      if (newMsg)
      this.snackbar = true;
      else this.snackbar = false;
    },
  },
};
</script>

<style>
.up {
  z-index: 60;
}
</style>
