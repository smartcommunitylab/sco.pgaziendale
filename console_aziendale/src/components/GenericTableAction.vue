<!-- 
DESCRIZIONE:
Il "GenericTableAction.vue" è una tabella generalizzata così da poterla usare con qualsiasi
tipo di dato e intestazione vogliamo all'interno dell'applicativo (come la GenericTable.vue)
con la differenza che ogni riga della tabella contiene 2 azioni (modifica e elimina).
-->
<template>
  <v-card>
    <v-data-table 
        class="row-pointer elevation"
        :headers="headers"
        :items="items"
        :search="search"
        :header-props="{'sortByText': 'Ordina per'}"
        :footer-props="{
            'items-per-page-text':'righe per pagina',
            pageText: '{0}-{1} di {2}'
        }"
        no-results-text="La ricerca non ha dato risultati"
        no-data-text="Non ci sono dati inseriti"
    >
        <template v-slot:top>
        <v-toolbar
            flat
        >
            <v-toolbar-title>{{title}}</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-text-field
                v-model="search"
                append-icon="mdi-magnify"
                label="Cerca"
                single-line
                hide-details
            ></v-text-field>
        </v-toolbar>
        </template>
        <template v-slot:[`item.actions`]="{ item }">
        <v-icon
            small
            class="mr-2"
            @click="openModal({type:'userFormEdit', object:item})"
        >
            mdi-pencil
        </v-icon>
        <v-icon
            small
            @click="openModal({type:'deleteUser', object: {user: item, adminCompany: adminCompany.item.id}})"
        >
            mdi-delete
        </v-icon>
        </template>
    </v-data-table>
  </v-card>
</template>

<script>
import { mapActions, mapState } from 'vuex'

export default {
    props: {
        items: Array,
        headers: Array,
        title: String
    },

    data() {
        return {
        search: '',
        componentKey: 0
        }
    },

    methods: {
        ...mapActions("modal", {openModal:'openModal'}),
    },

    computed: {
        ...mapState("company",["adminCompany"]),
    }
}
</script>

<style scoped>
.elevation{
  z-index: 60;
}
</style>