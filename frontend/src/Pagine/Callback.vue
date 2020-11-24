<template>
  <h1>Callback</h1>
</template>

<script>
export default {
  name: "Callback",
  methods: {
      
  },
  data() { 
      return {
      loginData:{},
      hashData:""
      }
  },
  beforeMount () {
    this.hasData = new URLSearchParams(
  window.location.hash.substr(1) // skip the first char (#)
);
    var expires_in = this.hasData.get("expires_in"); 
    var access_token = this.hasData.get("access_token"); 
    this.$store.dispatch('loginWithToken',{idToken:access_token,expiresIn:expires_in}).then(() => {
     this.$router.push('campagne').catch(()=>{});
   })
    
}
};
</script>

<style></style>
