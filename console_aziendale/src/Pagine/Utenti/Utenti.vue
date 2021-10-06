<template>
    <div>
        <gestione-azienda v-if="role=='ROLE_COMPANY_ADMIN'||(role=='ROLE_ADMIN'&&adminCompany!=null)"></gestione-azienda>
    </div>
</template>


<script>
import { mapState } from "vuex";
import GestioneAzienda from "../Azienda/GestioneAzienda.vue";

export default {
    components: {
        GestioneAzienda,
    },
    computed: {
        ...mapState("company", ["adminCompany"]),
        ...mapState("account", ["role","user"]),
    },
}
</script>


<style>

</style>