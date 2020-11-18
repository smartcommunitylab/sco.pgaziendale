<template>
    <div>
    <t-button>
                        <router-link to="/campagne" activeClass="active" tag="li"><a>Entra</a></router-link>
</t-button>
 <t-button>
                            <router-link to="/campagne" activeClass="active" tag="li"><a>Esci</a></router-link>

</t-button>
 <t-button>
                            <router-link to="/campagne" activeClass="active" tag="li"><a>Campagne</a></router-link>
</t-button>
 <t-button>
                                <router-link to="/info" activeClass="active" tag="li"><a>Info</a></router-link>

</t-button>
 <t-button>
                                <router-link to="/contatti" activeClass="active" tag="li"><a>Contatti</a></router-link>
</t-button>
    </div>
</template>
<script>
export default {
    name: "Header"
}
</script>
<style scoped>

</style>