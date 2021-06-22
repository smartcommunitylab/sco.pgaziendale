<template>
<div class="">
  <br>
  <button
    type="button"
    class="btn-close mt-6 mb-6 mx-6 text-center text-white bg-primary rounded uppercase hover:bg-primary_light"
    @click="scegli"
    aria-label="Close modal"
    >
    KM fatti e utili
  </button>
  <br>
  <button
    type="button"
    class="btn-close mt-6 mb-6 mx-6 text-center text-white bg-primary rounded uppercase hover:bg-primary_light"
    @click="scegli"
    aria-label="Close modal"
    >
    Partecipazione dipendenti
  </button>
  <br>
  <button
    type="button"
    class="btn-close mt-6 mb-6 mx-6 text-center text-white bg-primary rounded uppercase hover:bg-primary_light"
    @click="scegli"
    aria-label="Close modal"
    >
    Partecipazione dipendenti
  </button>
</div>
</template>
<script>
export default {
  methods: {
    scegli() {
      this.$router.push("/stats");
    },
  },
};
</script>
<!--<template>
<div>
    <div class="card">
    <h2>KM fatti e utili</h2>
    <div class="cta-container"><a href="/stats" class="cta">Cerca</a></div>
    <div class="card_circle"></div>
    </div>
</div>
</template>
<template>
<div class="ml-32">
    <div class="card">
    <h2>Partecipazione dipendenti</h2>
    <div class="cta-container"><a href="/stats" class="cta">Cerca</a></div>
    <div class="card_circle"></div>
    </div>
</div>
</template>
<template>
<div>
    <div class="card">
    <h2>Partecipazione dipendenti</h2>
    <div class="cta-container"><a href="/stats" class="cta">Cerca</a></div>
    <div class="card_circle"></div>
    </div>
</div>
</template>
<script>
export default {
    methods: {
        stati() {
        this.$router.push("/stats");
        },
    }
};
</script>
<style>
@import url(https://fonts.googleapis.com/css?family=Noto+Sans:400,700,400italic,700italic);

html,
body {
  background-color: #eaeaea;
}
.card {
  background-color: #fff;
  height: 400px;
  width: 300px;
  position: absolute;
  margin: auto;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  box-shadow: 0px 0px 10px rgba(0,0,0,0.3);
  overflow: hidden;
}

h2 {
  text-align: center;
  margin-top: 190px;
  position: absolute;
  z-index: 9999;
  font-size: 32px;
  color: #2196F3;
  width: 100%;
  font-family: 'Noto Sans', serif;
}

.cta-container {
  text-align: center;
  margin-top: 290px;
  position: absolute;
  z-index: 9999;
  width: 100%;
  font-family: 'Noto Sans', serif;
}

.cta {
  color: #fff;
  border: 2px solid #2196F3;
  background-color: #2196F3;
  padding: 10px 25px;
  border-radius: 50px;
  font-size: 17px;
  text-decoration: none;
  font-weight: bold;
}
</style>
-->