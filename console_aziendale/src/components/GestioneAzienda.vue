<template>
  <div>
      <div>Amministratori Aziendali</div>
    <div>Mobility Manager</div>
    <div>Campagne associate</div>
  </div>
</template>
<script>
import { mapState, mapActions } from "vuex";
export default {
  name: "GestioneAzienda",
  data() {
    return {};
  },
  computed: {
    ...mapState("company", ["actualCompany"]),
  },
  methods: {
	...mapActions("company", { getCompanyById: "getCompanyById" }),

  }
};
</script>

<style scoped></style>
