<template>
    <v-snackbar v-model="snackbar" class="up">
        {{ alert.message }}
        <v-btn
            color="primary"
            text
            @click="snackbar = false"
        >
            Chiudi
        </v-btn>
    </v-snackbar>
</template>

<script>
import { mapActions, mapState } from "vuex";

export default {
    computed: {
        snackbar: Boolean,
        text: String,
        ...mapState({alert: (state) => state.alert}),
        ...mapState('alert', ['message'])
    },
    methods: {
        ...mapActions("alert", { clearAlert: "clear" }),
    },
    watch: {
        message(newAlert,oldAlert){
            console.log(JSON.stringify(newAlert) + JSON.stringify(oldAlert));
            setTimeout(()=>this.clearAlert(),2500)
        },
    },
}
</script>

<style>
.up {
  z-index: 60;
}
</style>