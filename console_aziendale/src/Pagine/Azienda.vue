<template>
  <div class="flex flex-col lg:flex-row">
    <div class="bg-green-300 lg:w-4/6 mx-2 my-2">
      <h1>Profilo azienda</h1>
      <profilo-azienda></profilo-azienda>
    </div>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import ProfiloAzienda from '../components/ProfiloAzienda.vue'
export default {
  name: "Azienda",
  components:{
ProfiloAzienda
  },
  data: function() {
    return {

    }
  }
  ,
  created() {
    //check actualCompany otherwise get it by id
    if (!this.actualCompany)
      { //get company Id from profile
      this.setDefaultCompany();
        //this.actualCompany = this.getCompanyById(this.role)
      }
  },
    computed: {
    ...mapState("company", ["actualCompany"]),
    ...mapState("account", ["role"]),
    },
    methods: {
      ...mapActions("company",{getCompanyById:"getCompanyById"})
    },
};
</script>

<style>
.selected {
  @apply bg-background;
}
</style>
