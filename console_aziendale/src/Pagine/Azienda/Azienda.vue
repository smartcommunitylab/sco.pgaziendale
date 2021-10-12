<template>
  <div>
    <v-row>
      <v-col cols="12">
        <profilo-azienda></profilo-azienda>
      </v-col>
    </v-row>
    

  </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
import ProfiloAzienda from "./ProfiloAzienda.vue";
import EventBus from "@/components/eventBus";

export default {
  name: "Azienda",

  components: {ProfiloAzienda},

  data: function () {
    return {
      editModalVisible: false,
      popup: {
        title: "",
      },
      submitStatus: null,
    };
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

  created() {
    //check actualCompany otherwise get it by id
    if (!this.actualCompany) {
      //get company Id from profile
      this.setDefaultCompany(this.user);
      //this.actualCompany = this.getCompanyById(this.role)
    }
  },

  mounted() {
    this.changePage({title: 'Profilo azienda', route: '/azienda'})
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
};
</script>

<style>
.selected {
  @apply bg-background;
}
</style>
