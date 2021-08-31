<template>
  <div>
    <div v-if="role=='ROLE_ADMIN'">
      <div v-if="adminCompany!=null">
        <v-list-item-title class="text-h6">{{adminCompany.item.name}}</v-list-item-title>
        <v-list-item-subtitle @click="resetCompany" class="mt-1 linked">Logout</v-list-item-subtitle>
      </div>
      <div v-else>
        <v-list-item-title class="text-h6">
          Amministratore
        </v-list-item-title>
      </div>
    </div>
  </div>
</template>
<script>
 import { mapActions,mapState } from 'vuex';


export default {
    name: "ProfiloHeader",

    computed: {
        ...mapState('account', ['role']),
        ...mapState('company',['adminCompany','actualCompany'])
    },
  methods: {
    resetCompany() {
      this.resetCompanyAdmin();
      this.$router.push("/aziende");
    },
     ...mapActions('company', ['resetCompanyAdmin'])
  },
}
</script>

<style scoped>
.linked{
  font-style: italic;
}

.linked:hover {
  text-decoration: underline;
  cursor: pointer;
}
</style>