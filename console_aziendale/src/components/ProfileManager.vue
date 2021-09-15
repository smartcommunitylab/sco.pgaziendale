<template>
  <div>
    <account-cog-icon @click="openModal({type:'profileSetting', object:null})" />
  </div>
</template>


<script>
import { mapActions } from "vuex";

export default {
  methods: {
    ...mapActions("modal", {openModal:"openModal"}),
  },
};
</script>
<style scoped>

</style>
