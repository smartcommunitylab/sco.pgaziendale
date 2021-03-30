<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-full mx-2 my-2 p-8">
      <profilo-azienda></profilo-azienda>
      <gestione-azienda v-if="role=='ROLE_COMPANY_ADMIN'||(role=='ROLE_ADMIN'&&adminCompany!=null)"></gestione-azienda>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
import ProfiloAzienda from "./ProfiloAzienda.vue";
import GestioneAzienda from "./GestioneAzienda.vue";
export default {
  name: "Azienda",
  components: {
    ProfiloAzienda,
    GestioneAzienda,
  },
  data: function () {
    return {};
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
  computed: {
    ...mapState("company", ["adminCompany","actualCompany"]),
    ...mapState("account", ["role","user"]),
  },
  methods: {
    ...mapActions("company", { getCompanyById: "getCompanyById" }),
        ...mapActions("account", { setDefaultCompany: "setDefaultCompany" }),
        ...mapActions("navigation", { changePage: "changePage" })
  },
};
</script>

<style>
.selected {
  @apply bg-background;
}
</style>
