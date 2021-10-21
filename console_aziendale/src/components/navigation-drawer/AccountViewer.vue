
<!-- 
DESCRIZIONE:
L' "AccountViewer.vue" è un componente posto, in posizione di default, all'interno del
NavigtionDrawer nella sua parte più in alto. Il componente permette di visualizzare il
ruolo ricoperto all'intenro dell'applicativo (il ruolo/account con il quale si è loggati)
e permette agli admin loggati come amministratori di una specifica azienda di, cliccando
l'apposita icona, tornare allo stato di admin globale.
-->
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
      this.$router.push("/GestioneAziende");
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