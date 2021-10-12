<template>
  <div>
    <div v-if="role=='ROLE_ADMIN'">
      <div v-if="adminCompany!=null">
        <v-list-item link @click="resetCompany">
          <v-list-item-icon >
            <v-icon>mdi-account-arrow-left</v-icon>
          </v-list-item-icon>
          <v-list-item-title>{{adminCompany.item.name}}</v-list-item-title>
        </v-list-item>
      </div>
      <div v-else>
        <v-list-item link>
          <v-list-item-icon>
            <v-icon>mdi-account-key</v-icon>
          </v-list-item-icon>
          <v-list-item-title>
            Amministratore
          </v-list-item-title>
        </v-list-item>
      </div>
    </div>
  </div>
</template>

<script>
 import { mapActions,mapState } from 'vuex';

export default {
  name: "ProfiloHeader",

  methods: {
    ...mapActions('company', ['resetCompanyAdmin']),

    resetCompany() {
      this.resetCompanyAdmin();
      this.$router.push("/aziende");
    }
  },

  computed: {
      ...mapState('account', ['role']),
      ...mapState('company',['adminCompany','actualCompany'])
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