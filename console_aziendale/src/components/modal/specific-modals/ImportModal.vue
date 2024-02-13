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
        <v-row >
          <v-col cols="12">
            <div v-if="typeCall == 'location'">
              <p>Il file deve seguire il formato dell'esempio e tutti i campi devono essere valorizzati.</p>
              <p>I valori nella colonna <b>Identificativo sede</b> devono essere univoci.</p>
              <p>Il processo di import aggiungerà al database tutte le sedi indicate nel file oppure, se nel database dovessero già esistere delle sedi con l'identificativo valorizzato nel file, ne aggiornerà tutti i dati con i valori del file.</p>
            </div>
            <div v-if="typeCall == 'employee'">
              <p>Il file deve seguire il formato dell'esempio e tutte le colonne devono essere valorizzate.</p>
              <p>Il valore indicato nella colonna <b>Codice dipendente</b> è utilizzato per identificare il dipendente, dunque deve essere univoco e non possono esistere duplicati.</p>
              <p>La procedura di import aggiungerà al database tutti i dipendenti inseriti nel file a meno che il loro codice non corrisponda a quello di un dipendente già inserito nel database; in questo caso tutti i dati del dipendente già inserito verranno aggiornati con i valori indicati nel file importato.</p>
              <p>Il valore indicato nella colonna <b>Codice sede</b> è utilizzato per associare il dipendente ad una specifica sede, dunque deve corrispondere esattamente ad all'identificativo di una delle sedi inserite nel database (si veda 'Gestione Sedi').</p>
            </div>
          </v-col>
        </v-row>
        <v-row justify="center" class="mt-5 mb-8">
          <v-btn outlined color="primary">
            <a v-if="typeCall == 'location'" href="/files/exampleLocations.csv" download
              >Scarica file di esempio</a
            >
            <a v-if="typeCall == 'employee'" href="/files/exampleEmployee.csv" download
              >Scarica file di esempio</a
            >
          </v-btn>
        </v-row>
        <v-row>
          <v-col cols="12">
            <v-file-input
              label="Clicca qui per caricare il file .csv"
              type="file"
              ref="file"
              v-model="fileUploaded"
              accept=".csv"
              @change="onFileUploaderChange"
              outlined
              dense
            ></v-file-input>
          </v-col>
        </v-row>
      </template>
      <template v-slot:footer>
        <v-btn text @click="closeModal()" class="py-8 ml-8"> Annulla </v-btn>
        <v-btn color="primary" text @click="importCSV()" class="py-8 ml-8">
          Importa
        </v-btn>
      </template>
    </modal>
  </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
import Modal from "@/components/modal/ModalStructure.vue";

export default {
  components: {
    modal: Modal,
  },
  props: {
    typeCall: String,
  },
  data() {
    return {
      fileUploaded: null,
    };
  },
  computed: {
    ...mapState("company", ["actualCompany"]),
  },
  methods: {
    ...mapActions("modal", { closeModal: "closeModal" }),
     ...mapActions("employee", { importDataEmployees: "importEmployees"}),
     ...mapActions("location", { importDataLocations: "importLocations"}),
    importCSV() {
      switch (this.typeCall) {
        case "location":
          this.importLocations();
          break;
        case "employee":
          this.importEmployee();
          break;
        default:
          console.log("ERRORE: la 'typeCall' usata non ha un comportamento dichiarato.");
          break;
      }
      this.closeModal();
    },

    importLocations() {
      console.log(this.fileUploaded);
      this.modalImportLocationsOpen = false;
      var formData = new FormData();
      formData.append("file", this.fileUploaded);
      this.importDataLocations({ companyId: this.actualCompany.item.id, file: formData });
    },
    importEmployee() {
      console.log(this.fileUploaded);
      this.modalImportEmployeesOpen = false;
      var formData = new FormData();
      formData.append("file", this.fileUploaded);
      this.importDataEmployees({ companyId: this.actualCompany.item.id, file: formData });
    },
    onFileUploaderChange(file) {
      this.fileUploaded = file;
    },
    fileName() {
      return this.fileUploaded.item(0).name;
    },
  },
};
</script>

<style></style>
