<template>
    <div>
      <modal>
        <template v-slot:header>
            <div class="text-danger"> 
                <div v-if="typeCall == 'azienda'">Cancella Azienda</div>
                <div v-if="typeCall == 'campaign'">Cancella Campagna</div>
                <div v-if="typeCall == 'user'">Cancella Utente</div>
                <div v-if="typeCall == 'location'">Cancella Sede</div>
                <div v-if="typeCall == 'employee'">Cancella Dipendente</div>
                <div v-if="typeCall == 'employee-block'">Blocco Dipendente</div>
            </div>
        </template>
        <template v-slot:body>
            <p v-if="typeCall != 'employee-block'" class="text-subtitle-1">Sei sicuro di voler cancellare l'elemento selezionato?</p>
            <p v-if="typeCall == 'employee-block'" class="text-subtitle-1">Sei sicuro di voler bloccare/sbloccare l'elemento selezionato?</p>
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
            @click="remove()"
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

    props: {typeCall: String},

    methods: {
        ...mapActions("modal", { closeModal:"closeModal" }),
        ...mapActions("company", { deleteCompany: "deleteCompany", getCompanyById:"getCompanyById", deleteUser:"deleteUser"}),
        ...mapActions("campaign", { deleteCampaign: "deleteCampaign", getAllCompaniesOfCampaign:"getAllCompaniesOfCampaign"}),
        ...mapActions("location", { deleteLocation: "deleteLocation"}),
        ...mapActions("employee", { deleteEmployee: "deleteEmployee", blockEmployee: "blockEmployee"}),

        remove: function() {
            switch (this.typeCall) {
                case 'azienda':
                    console.log("Sono nello switch");
                    console.log(this.actualCompany);
                    this.deleteCompany(this.actualCompany.item);
                    this.getCompanyById(null);
                    break;
                case 'campaign':
                    this.deleteCampaign({
                        companyId: this.adminCompany ? this.actualCompany.item.id : null,
                        campaignId: this.actualCampaign.item.id,
                    });
                    this.getAllCompaniesOfCampaign(null);
                    break;
                case 'user':
                    this.deleteUser({
                        companyId: this.object.adminCompany,
                        user: this.object.user,
                    });
                    break;
                case 'location':
                    this.deleteLocation({
                        companyId: this.object.actCompany.item.id,
                        locationId: this.object.actLocaiton.item.id,
                    });
                    break;
                case 'employee':
                    this.deleteEmployee({
                        companyId: this.object.actCompany.item.id,
                        employeeId: this.object.actEmployee.item.id,
                    });
                    break;
                case 'employee-block':
                    this.blockEmployee({
                        companyId: this.object.actCompany.item.id,
                        employeeId: this.object.actEmployee.item.id,
                        blocked: !this.object.actEmployee.item.blocked,
                    });
                    break;
                default:
                    console.log("ERRORE: la 'typeCall' usata non ha un comportamento dichiarato.");
                    break;   
            }
            this.closeModal();
        }
    },

    computed: {
        ...mapState("modal", ["object"]),
        ...mapState("company", ["actualCompany", "adminCompany"]),
        ...mapState("campaign", ["actualCampaign"]),
        ...mapState("location", ["actualLocation"]),
    }
}
</script>

<style>
</style>