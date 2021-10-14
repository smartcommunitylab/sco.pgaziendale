<template>
    <div>
        <v-card>
            <div class="flex">
            <v-card-title class="left">Configuraizone in uso</v-card-title>
            <v-spacer></v-spacer>
            <v-btn
                class="right mt-4 mr-4"
                icon
                @click="method"
            >
               <v-icon>mdi-chevron-right</v-icon> 
            </v-btn>
            </div>
            <v-card-text class="px-5 py-4">
                <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>
                <p v-if="pippo" class="p-0"><b>Riassunto1</b>: {{pippo}}</p>   
            </v-card-text>

            <v-card-actions>
                <v-btn
                    color="primary"
                    text
                    @click="sheet = !sheet"
                >
                    Modifica filtri
                </v-btn>
            </v-card-actions>
        </v-card>

        <v-bottom-sheet v-model="sheet">
            <v-sheet
                class="text-center"
                height="200px"
            >
                <v-btn
                class="mt-6"
                text
                color="red"
                @click="sheet = !sheet"
                >
                close
                </v-btn>
                <div class="py-3">
                This is a bottom sheet using the controlled by v-model instead of activator
                </div>
            </v-sheet>
        </v-bottom-sheet>

    </div>
</template>


<script>
export default {
    props: {
        method: Function,
    },
    data(){
        return {
            sheet: false,
            pippo: "ciao",
        }
    }
}
</script>


<style>
</style>