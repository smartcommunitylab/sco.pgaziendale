<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-full mx-2 my-2 p-8">
      <profilo-azienda></profilo-azienda>
      <gestione-azienda v-if="role=='ROLE_COMPANY_ADMIN'||(role=='ROLE_ADMIN'&&adminCompany!=null)"></gestione-azienda>
       <modal v-show="editModalVisible">
      <template v-slot:header> {{ popup.title }} </template>
      <template v-slot:body>
        <azienda-form />
      </template>
      <template v-slot:footer>
        <button
          type="button"
          class="btn-close"
          @click="saveCompany"
          aria-label="Close modal"
        >
          Salva
        </button>
        <button
          type="button"
          class="btn-close"
          @click="closeModal"
          aria-label="Close modal"
        >
          Annulla
        </button>
        <p class="typo__p" v-if="submitStatus === 'ERROR'">
          Riempire i dati nel modo corretto
        </p>
      </template>
    </modal>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
import ProfiloAzienda from "./ProfiloAzienda.vue";
import GestioneAzienda from "./GestioneAzienda.vue";
import EventBus from "@/components/eventBus";
import Modal from "@/components/Modal.vue";
import AziendaForm from "./AziendaForm.vue"

export default {
  name: "Azienda",
  components: {
    ProfiloAzienda,
    GestioneAzienda,
    Modal,
    AziendaForm
  },
  data: function () {
    return {
            editModalVisible: false,
                  popup: {
        title: "",
      },
      submitStatus: null,
    };
  },
  created() {
    this.changePage({title: 'Profilo azienda',
                route: '/azienda'})
    //check actualCompany otherwise get it by id
    if (!this.actualCompany) {
      //get company Id from profile
      this.setDefaultCompany(this.user);
      //this.actualCompany = this.getCompanyById(this.role)
    }
  },
  mounted() {
        EventBus.$on("EDIT_COMPANY", (company) => {
      this.editModalVisible = true;
      EventBus.$emit("EDIT_COMPANY_FORM", company.item);
      this.popup = {
        title: "Modifica",
      };
    });
    EventBus.$on("OK_COMPANY_FORM", (company) => {
        if (this.newCompany) {
          this.addCompanyCall(company);
        } else {
          this.updateCompanyCall(company);
      }
      this.editModalVisible = false;
    });
        EventBus.$on("OK_COMPANY_FORM", (company) => {
        {
          this.updateCompanyCall(company);
      }
      this.editModalVisible = false;
    });
  },
      beforeDestroy() {
    EventBus.$off("OK_COMPANY_FORM");
    EventBus.$off("EDIT_COMPANY");
  },
  computed: {
    ...mapState("company", ["adminCompany","actualCompany"]),
    ...mapState("account", ["role","user"]),
  },
  methods: {
    ...mapActions("company", { getCompanyById: "getCompanyById",updateCompanyCall: "updateCompany", }),
        ...mapActions("account", { setDefaultCompany: "setDefaultCompany" }),
        ...mapActions("navigation", { changePage: "changePage" }),
        saveCompany() {
            EventBus.$emit("CHECK_COMPANY_FORM");


    },
        closeModal() {
      this.editModalVisible = false;
    },
  },
};
</script>

<style>
.selected {
  @apply bg-background;
}
</style>
