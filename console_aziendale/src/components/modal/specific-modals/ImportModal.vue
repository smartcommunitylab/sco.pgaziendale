<template>
    <div>
      <modal>
        <template v-slot:header>
            <div> 
              <div v-if="typeCall == 'location'">Importa Sedi</div>
              <div v-if="typeCall == 'employee'">Importa Dipendenti</div>
            </div>
        </template>
        <template v-slot:body> 
        <v-row
          justify="center"
          class="mt-5 mb-8"
        >
          <v-btn
            outlined
            color="primary"
          >
            <a v-if="typeCall == 'location'" href="/files/exampleLocations.csv" download>Scarica file di esempio</a>
            <a v-if="typeCall == 'employee'" href="/files/exampleEmployee.csv" download>Scarica file di esempio</a>
          </v-btn>
        </v-row>
        <v-row>
          <v-col
            cols="12"
          >
            <v-file-input
              label="Clicca qui per caricare il file .csv"
              type="file"
              ref="file"
              v-model="fileUploaded"
              accept=".csv"
              @change="/*onFileUploaderChange*/"
              outlined
              dense
            ></v-file-input>
          </v-col>
        </v-row>
      </template>
        <template v-slot:footer>
          <v-btn
            text
            @click="closeModal()"
            class="py-8 ml-8"
          >
            Annulla
          </v-btn>
          <v-btn
            color="primary"
            text
            @click="importCSV()"
            class="py-8 ml-8"
          >
            Importa
          </v-btn>
        </template>
      </modal>
    </div>
</template>

<script>
import { mapActions } from "vuex";
import Modal from "@/components/modal/ModalStructure.vue";

export default {
    components: {
        "modal": Modal,
    },
    props: {
        typeCall: String,
    },
    methods: {
        ...mapActions("modal", { closeModal:"closeModal" }),
        importCSV() {
            switch (this.typeCall) {
                case 'location':
                    this.importLocations();
                    break;
                case 'employee':
                    this.importEmployee();
                    break;
                default:
                    console.log("ERRORE: la 'typeCall' usata non ha un comportamento dichiarato.");
                    break;   
            }
            this.closeModal();
        },

        importLocations() {
          //TODO
        },
        importEmployee() {
          //TODO
        }
    }
}
</script>

<style>

</style>