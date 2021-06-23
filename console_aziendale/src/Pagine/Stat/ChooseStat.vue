<template>
      <div class="container">
          <div class="box">
            <div class="card transition">
            <h2 class="transition">Km fatti e utili<br></h2>
            <div class="cta-container transition"><a href="#" class="cta">Button</a></div>
                <div class="card_circle transition"></div>
            </div>
          </div>
          <div class="box">
            <div class="card transition">
                <h2 class="transition">Partecipazione dipendenti<br></h2>
                <div class="cta-container transition"><a href="#" class="cta">Button</a></div>
                    <div class="card_circle transition"></div>
            </div>        
          </div>
          <div class="box">
            <div class="card transition">
            <h2 class="transition">Impatto ambientale<br></h2>
            <div class="cta-container transition"><button @click="clickToStats" class="cta">asdsdadada</button></div>
                <div class="card_circle transition"></div>
            </div>        
          </div>
      </div>
</template>

<script>
export default {
  methods: {
    clickToStats(){
      this.$router.push("/stats");
    }
  },
};
</script>

<style scoped>
    @import url(https://fonts.googleapis.com/css?family=Noto+Sans:400,700,400italic,700italic);
    body{
        background-color: white;
        margin: 0;
        height: fit-content;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .container{
        width: 90%;
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        grid-gap: 60px;
    }

    .box{
        height: 400px;
        width: 30rem;
        color: black;
        position: relative;
        margin-top: 4rem;
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

    .transition {
        transition: .3s cubic-bezier(.3, 0, 0, 1.3)
    }

    .card:hover {
        box-shadow: 0px 30px 30px rgba(0,0,0,0.2);
    }

    .card_circle {
        height: 400px;
        width: 450px;
        background-size: cover;
        position: absolute;
        border-radius: 50%;
        margin-left: -75px;
        margin-top: -270px;
    }

    h2 {
        text-align: center;
        margin-top: 190px;
        position: absolute;
        z-index: 9999;
        font-size: 26px;
        color: #2196F3;
        width: 100%;
        font-family: 'Noto Sans', serif;
    }

    h2 small {
        font-weight: normal;
        font-size: 65%;
        color: rgba(0,0,0,0.5);
    }

    .card:hover h2 small {
        color: #fff;
    }

    .cta-container {
        text-align: center;
        margin-top: 320px;
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
