<template>
  <div>
  <div class="flex mb-4">
  <div class="flex-1 h-12">
      <t-button>
      <router-link to="/campagne" activeClass="active" tag="li"
        ><a>Entra</a></router-link
      >
    </t-button></div>
  <div class="flex-1  h-12">
   <t-button>
      <router-link to="/campagne" activeClass="active" tag="li"
        ><a>Esci</a></router-link
      >
    </t-button></div>
  <div class="flex-1  h-12">
      <t-button>
      <router-link to="/campagne" activeClass="active" tag="li"
        ><a>Campagne</a></router-link
      >
    </t-button></div>
  <div class="flex-1   h-12">
      <t-button>
      <router-link to="/info" activeClass="active" tag="li"
        ><a>Info</a></router-link
      >
    </t-button></div>
  <div class="flex-1   h-12">
      <t-button>
      <router-link to="/contatti" activeClass="active" tag="li"
        ><a>Contatti</a></router-link
      >
    </t-button></div>
    
</div>

   



  </div>
</template>
<script>
export default {
  name: "Header",
};
</script>
<style scoped></style>
