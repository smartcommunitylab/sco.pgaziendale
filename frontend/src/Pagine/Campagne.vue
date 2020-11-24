<template>
<div>
  <h1>Campagne</h1>
        <t-button class="w-2/12 mt-8 m-auto" variant="green"><p class="text-center w-full" @click="dettaglio"> dettaglio </p></t-button>

</div>
</template>

<script>
export default {
  name: "Campagne",
  methods: {
    dettaglio() {
      this.$store.dispatch('enterCampagna',{id:'1'}).then(() => {
        this.$router.push({name:'campagna',params: { id: '1' }}).catch(()=>{});
       })
    }
  },
};
</script>

<style>
</style>
