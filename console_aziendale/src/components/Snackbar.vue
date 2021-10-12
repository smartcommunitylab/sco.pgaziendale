<template>
    <div v-if="snackbarOpen">
        <v-snackbar v-model="snackbar" class="up">
            {{ alert.message }}
            <v-btn
                color="primary"
                text
                @click="snackbarOpen = false"
            >
                Chiudi
            </v-btn>
        </v-snackbar>
    </div>
</template>

<script>
import { mapActions, mapState } from "vuex";

export default {
    data() {
        return{
            snackbarOpen: true,
        }
    },

    methods: {
        ...mapActions("alert", { clearAlert: "clear" }),
    },

    computed: {
        ...mapState({alert: (state) => state.alert}),
        ...mapState('alert', ['message']),

        snackbar: Boolean,
        text: String,
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