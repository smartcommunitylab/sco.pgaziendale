<template>
      <div class="container">
           <div v-if="role=='ROLE_ADMIN'">
              <div v-if="adminCompany!=null">

            <span>{{adminCompany.item.name}}</span>
              <button @click="resetCompany">
                 <span class="img-exit-profile">
                <logout-icon />
              </span>
              </button>
              </div>
              <div v-else>
                Amministratore
              </div>
          </div>
           <div v-if="(role=='ROLE_COMPANY_ADMIN' || role== 'ROLE_MOBILITY_MANAGER')&&actualCompany!=null && actualCompany.item!=null" >
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
    resetCompany() {
      this.resetCompanyAdmin();
    },
     ...mapActions('company', ['resetCompanyAdmin'])
  },
}
</script>
<style scoped>
.container {
    text-align: center;
    border: solid 1px white;
    margin: 10px 20px;
    padding: 8px;
    width: 80%;
    border-radius: 8px;

}
.img-exit-profile {
margin: 2px 4px;
      vertical-align: middle;
    display: inline-block;
}
</style>