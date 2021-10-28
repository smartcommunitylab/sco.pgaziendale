<template>
        <v-row>
            <v-col :cols="nColsCalculator" class="p-0 m-0">
                <v-row>
                    <v-col cols="12" class="p-0 m-0 ">
                        <div class="d-flex">
                            <div class="justify-start">
                                <slide-group :items="selectionCards"></slide-group>
                            </div>
                            <v-spacer></v-spacer>
                            <div class="justify-end">
                                <v-card class="customButton">
                                    <v-btn
                                        v-if="!isOpenSidebar"
                                        
                                        icon
                                        @click="changeOpenSidebar()"
                                    >
                                        <v-icon>mdi-chevron-left</v-icon> 
                                    </v-btn>
                                </v-card>
                            </div>
                        </div>
                    </v-col>
                    <v-col cols="12" class="">
                        <tab></tab>
                    </v-col>
                </v-row>
            </v-col>
            <v-col v-if="isOpenSidebar" class="m-0 p-0">
                <sidebar class="ml-4 mt-0 pt-0" :method="changeOpenSidebar"></sidebar>
            </v-col>
        </v-row>
</template>


<script>
import { mapState, mapActions } from "vuex";
import SlideGroup from "@/components/slide-group/SlideGroup.vue";
import Tab from "@/components/tab/Tab.vue";
import Sidebar from "@/components/sidebar/Sidebar.vue";

export default {
    components : {
        "slide-group" : SlideGroup,
        "tab" : Tab,
        "sidebar" : Sidebar,
    },
    data: function() {
        return {
            isOpenSidebar: true,
            selectionCards: [{title:"Km fatti e utili", backgroundImageUrl: "https://upload.wikimedia.org/wikipedia/commons/8/8a/Nuvola503.jpg", method: this.pippo}, {title:"Partecipazione dipendenti", backgroundImageUrl: "https://upload.wikimedia.org/wikipedia/it/4/43/Bender.png", method: this.pippo}, {title:"Impatto ambientale", backgroundImageUrl: "https://upload.wikimedia.org/wikipedia/it/4/43/Bender.png", method: this.pippo}],
        }
    },
    methods: {
        ...mapActions("stat",{getConfigurationByRole:"getConfigurationByRole"}),

        changeOpenSidebar(){
            this.isOpenSidebar = !this.isOpenSidebar;
            this.getConfigurationByRole({role:"ROLE_COMPANY_ADMIN"});
        },
        
    },
    computed: {
        ...mapState("stat", ["configurations"]),
        nColsCalculator: function() {
            if(this.isOpenSidebar){
                return 9;
            }else{
                return 12;
            }
        }
    },
    watch: {
        configurations(){
            console.log(this.configurations);
        }
    }
}
</script>


<style>
.customButton{
    top: 30px;
}

</style>