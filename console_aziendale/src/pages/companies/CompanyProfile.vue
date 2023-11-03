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
import ProfiloAzienda from "./Company.vue";

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
    ...mapState("account", ["user"]),
  },

  methods: {
    ...mapActions("company", { getCompanyById: "getCompanyById",updateCompanyCall: "updateCompany", }),
    ...mapActions("account", { setDefaultCompany: "setDefaultCompany" }),
    ...mapActions("navigation", { changePage: "changePage" }),

    saveCompany() {
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
    }
  },

  mounted() {
    this.changePage({title: 'Profilo azienda', route: '/ProfiloAzienda'})
  },
};
</script>

<style>
</style>
