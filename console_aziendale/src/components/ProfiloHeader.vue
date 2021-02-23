<template>
              <div>
           <div v-if="role=='ROLE_ADMIN'">
              <div v-if="adminCompany!=null">

            <span>{{adminCompany.item.name}}</span>
              <button @click="resetCompany">esci</button>
              </div>
              <div v-else>
                Amministratore
              </div>
          </div>
           <div v-if="role=='ROLE_COMPANY_ADMIN'&&actualCompany!=null">
            {{actualCompany.item.name}}
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
    drawer() {
      this.isOpen = !this.isOpen;
    },
    resetCompany() {
      this.resetCompanyAdmin();
    },
     ...mapActions('company', ['resetCompanyAdmin'])
  },
}
</script>
<style scoped>

</style>