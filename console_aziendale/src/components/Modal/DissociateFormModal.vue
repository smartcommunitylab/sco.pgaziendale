<template>
    <modal>
      <template v-slot:header> <div class="text-danger"> {{ popup.title }} </div>  </template>
      <template v-slot:body>
        <p class="text-subtitle-1">Sei sicuro di voler disassociare la campagna <b>{{object.title}}</b>?</p>
      </template>
      <template v-slot:footer>
        <v-btn
          text
          @click="openModal({type:'associateForm', object:null})"
          class="py-8 ml-8"
        >
          Annulla
        </v-btn>
        <v-btn
          color="error"
          text
          @click="deleteConfirm"
          class="py-8 ml-8"
        >
          Conferma
        </v-btn>
      </template>
    </modal>
</template>

<script>
import { mapActions, mapState } from "vuex";
import Modal from "@/components/Modal.vue";

export default {
    components: {Modal},
    data() {
        return{
            popup: {
                title: 'Disassociare la campagna'
            }
        }
    },
    computed: {
        ...mapState("modal", ['object']),
        ...mapState("account", ["role"]),
        ...mapState("company", ["actualCompany", "adminCompany"]),
        ...mapState("campaign", ["allCampaigns"]),
    },
    methods: {
        ...mapActions("modal", {openModal: 'openModal', closeModal: 'closeModal'}),
        ...mapActions("campaign", {deleteCompanyCampaign: 'deleteCompanyCampaign'}),
        deleteConfirm() {
            console.log(this.object);
            this.disassocia(this.object);
            this.closeModal();
        },
        disassocia(campaign) {
            if (this.role == "ROLE_ADMIN" && this.adminCompany && this.adminCompany.item) {
                this.deleteCompanyCampaign({
                companyId: this.adminCompany.item.id,
                campaign: campaign,
                });
            } else {
                this.deleteCompanyCampaign({
                companyId: this.actualCompany.item.id,
                campaign: campaign,
                });
            }
            this.$emit("update:allCampaigns.items", this.allCampaigns.items);
        },
    }
}
</script>