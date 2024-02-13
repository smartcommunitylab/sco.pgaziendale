<template>
    <div>
      <modal>
        <template v-slot:header>
            <div class="text-danger"> 
                <div>Cambia Stato</div>
            </div>
        </template>
        <template v-slot:body>
          <p v-if="!actualCompany.item.state" class="text-subtitle-1">Cambiando lo stato attesti che i dati aziendali, quali profilo aziendale, associazione campagne, sedi aziendali, lista dipendenti, sono stati inseriti e controllati. Sei sicuro di voler cambiare lo stato di compilazione in <b>'verificato'</b>?</p>
          <p v-if="actualCompany.item.state" class="text-subtitle-1">Sei sicuro di voler cambiare lo stato di compilazione in <b>'da verificare'</b>?</p>
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
            color="error"
            text
            @click="update()"
            class="py-8 ml-8"
          >
            Conferma
          </v-btn>
        </template>
      </modal>
    </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
import Modal from "@/components/modal/ModalStructure.vue";

export default {
    components: {"modal": Modal},

    methods: {
        ...mapActions("modal", { closeModal:"closeModal" }),
        ...mapActions("company", { updateCompanyState: "updateCompanyState", getCompanyById:"getCompanyById"}),

        update: function() {
            console.log("Sono nello switch");
            console.log(this.actualCompany);
            this.actualCompany.item.state = !this.actualCompany.item.state;
            this.updateCompanyState(this.actualCompany.item);
            this.getCompanyById(null);
            this.closeModal();
        }
    },

    computed: {
        ...mapState("modal", ["object"]),
        ...mapState("company", ["actualCompany", "adminCompany"]),
    }
}
</script>

<style>
</style>