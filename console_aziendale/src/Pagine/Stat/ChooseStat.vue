<template>
    <div class="flex">
        <div class="content">
            <div class="card" @click="clickToStats('km')">
                <div class="icons-lg"><bike-icon size="200"></bike-icon></div>
                <div class="icons-sm"><bike-icon size="80"></bike-icon></div>
                    <p class="title">Km fatti e utili</p>
                    <p class="text">descrizione</p>
            </div>
            <div class="card" @click="clickToStats('impatto')">
                <div class="icons-lg"><tree-icon size="200"></tree-icon></div>
                <div class="icons-sm"><tree-icon size="80"></tree-icon></div>
                    <p class="title">Impatto ambientale</p>
                    <p class="text">descrizione</p>
                </div>
            <div class="card" @click="clickToStats('dipendenti')">
                <div class="icons-lg"><human-icon size="200"></human-icon></div>
                <div class="icons-sm"><human-icon size="80"></human-icon></div>
                    <p class="title">Partecipazione dipendenti</p>
                    <p class="text">descrizione</p>
            </div>     
        </div>
    </div>
</template>

<script>
import { mapActions } from "vuex";

export default {
    data() {
        return {
            mapConf: {
                'km': {
                    titolo: 'Km fatti e utili',
                    tipiVis: ['tabella', 'bars', 'lines']
                },
                'impatto': {
                    titolo: 'Impatto ambientale',
                    tipiVis: ['tabella', 'stacked']
                },
                'dipendenti': {
                    titolo: 'Partecipazione dipendenti',
                    tipiVis: ['tabella', 'lines']
                },
            }
        }
    },
    mounted(){
        this.changePage({ title: "Scegli statistiche", route: "/chooseStat" });
    },
    methods: {
        ...mapActions("navigation", { changePage: "changePage" }),
        ...mapActions('stat', {
            setStatMethod: 'setStat'
        }),
        clickToStats(selection) {
            this.setStatMethod(this.mapConf[selection]);
            this.$router.push('/stats');
        }
    }
};
</script>

<style scoped>
.icons-lg {
    padding-top: 10px;
}

.icons-sm {
    padding-top: 20px;
}
@media (max-width: 768px){
  .icons-lg {
    display: none;
  }
}

@media screen and (min-width: 767px) {
    .icons-sm {
        display: none;
    }
}


.card {
   width: 350px;
   height: 500px;
   background-color: #dfded5;
   margin-right: 35px;
   margin-left: 20px;
   border-radius: 10px;
   box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.24);
   border: 3px solid #d6d5c9;
   font-size: 16px;   
   transition: all 0.3s ease;
   position: relative;
   display: flex;
   justify-content: center;
   flex-direction: column;
   cursor: pointer;
   transition: all 0.3s ease;
}
.card .title {
   width: 100%;
   margin: 0;
   text-align: center;
   margin-top: 20px;
   color: rgb(0, 0, 0);
   font-weight: 600;
   letter-spacing: 2px;
}

.card .text {
   color: white;
   width: 80%;
   margin: 0 auto;
   font-size: 15px;
   text-align: center;
   margin-top: 20px;
   font-weight: 200;
   letter-spacing: 2px;
   opacity: 0;
   max-height:0;
   transition: all 0.3s ease;
}

.card:hover {
   padding-bottom: 50px;
   background: rgb(255, 255, 255);
   border: 3px solid rgba(22, 96, 150, 1);
}

.card:hover .info {
   height: 90%;
}
.card:hover .text {
   color: black;
   transition: all 0.3s ease;
   opacity: 1;
   max-height: 40px;
}
.card:hover .icon {
   background-position: -120px;
   transition: all 0.3s ease;
}

.content {
   width: 100%;
   padding: 0 4%;
   padding-top: 140px;
   margin: 0 auto;
   display: flex;
   justify-content: center;
}
@media screen and (max-width: 767px) {
   .wrapper {
      height: 700px;
   }
   .icons {
       font-size: 50px;
   }
   .content {
      padding-top: 50px;
      flex-direction: column;
      align-items: center;
   }
   .card {
       margin-bottom: 30px;
       height: 250px;
       width: 450px;
   }
}

@media screen and (min-width: 768px) {
   .content {
      padding-top: 15px;
      align-items: center;
   }
}
@media screen and (min-width: 1500px) {
   .card {
      height: 700px;
      width: 550px;
   }
   .title {
       font-size: 30px;
   }
   .card:hover .title {
    font-size: 30px;
    }
}
.icon {
   margin: 0 auto;
   width: 100%;
   height: 80px;
   max-width:80px;
   background: linear-gradient(90deg, #FF7E7E 0%, #FF4848 40%, rgba(0, 0, 0, 0.28) 60%);
   border-radius: 100%;
   display: flex;
   justify-content: center;
   align-items: center;
   color: white;
   transition: all 0.8s ease;
   background-position: 0px;
   background-size: 200px;
}
.card:hover .icon i {
   background: linear-gradient(90deg, #FF7E7E, #FF4848);
   -webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
   opacity: 1;
   transition: all 0.3s ease;
}
</style>