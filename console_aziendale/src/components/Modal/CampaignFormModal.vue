<template>
  <modal>
    <template v-slot:header> {{ popup.title }} </template>
    <template v-slot:body>
      <form action="" id="addCampaign">
        <div class="mb-20 flex flex-wrap justify-between">
        <v-row>
            <v-col
            cols="6"
            >
            <v-text-field
                label="Id"
                placeholder="Id *"
                type="text"
                name="campaignId"
                :rules="[inputRules.required]"
                id="campaignId"
                v-model.trim="$v.id.$model"
                outlined
                
                :disabled="edit"
            >
                <template v-slot:append>
                <v-tooltip
                    left
                    nudge-bottom="50px"
                    v-if="$v.id.$model == ''"
                >
                    <template v-slot:activator="{ on }">
                    <v-icon v-on="on">
                        mdi-help-circle-outline
                    </v-icon>
                    </template>
                    Codice univoco della campagna
                </v-tooltip>
                </template>
            </v-text-field>
            </v-col>
            <v-col
            cols="6"
            >
            <v-text-field
                label="Logo"
                placeholder="Logo"
                type="text"
                name="campaignLogo"
                id="campaignLogo"
                v-model.trim="$v.logo.$model"
                outlined
            >
                <template v-slot:append>
                <v-tooltip
                    left
                    nudge-bottom="50px"
                >
                    <template v-slot:activator="{ on }">
                    <v-icon v-on="on">
                        mdi-help-circle-outline
                    </v-icon>
                    </template>
                    Inserisci un url che contenga il logo dell'azienda
                </v-tooltip>
                </template>
            </v-text-field>
            </v-col>
            <v-col
            cols="6"
            >
            <v-text-field
                label="Titolo"
                placeholder="Titolo *"
                type="text"
                name="campaignTitle"
                :rules="[inputRules.required]"
                id="campaignTitle"
                v-model.trim="$v.title.$model"
                outlined            
            ></v-text-field>
            </v-col>
            <v-col
            cols="6"
            >
            <v-text-field
                label="Descrizione"
                placeholder="Descrizione *"
                type="text"
                name="campaignDescription"
                :rules="[inputRules.required]"
                id="campaignDescription"
                v-model.trim="$v.description.$model"
                outlined            
            ></v-text-field>
            </v-col>
            <v-col
            cols="6"
            >
            <v-menu
                ref="menu"
                v-model="menu"
                :close-on-content-click="false"
                :return-value.sync="from"
                transition="scale-transition"
                offset-y
                min-width="auto"
                :rules="[inputRules.required]"
                outlined
            >
                <template v-slot:activator="{ on, attrs }">
                <v-text-field
                    v-model.trim="$v.from.$model"
                    label="Da"
                    prepend-icon="mdi-calendar"
                    readonly
                    v-bind="attrs"
                    v-on="on"
                ></v-text-field>
                </template>
                <v-date-picker
                v-model.trim="$v.from.$model"
                no-title
                scrollable
                color="primary"
                >
                <v-spacer></v-spacer>
                <v-btn
                    text
                    color="primary"
                    @click="$refs.menu.save(from)"
                >
                    Conferma
                </v-btn>
                </v-date-picker>
            </v-menu>
            </v-col>
            <v-col
            cols="6"
            >
            <v-menu
                ref="menu2"
                v-model="menu2"
                :close-on-content-click="false"
                :return-value.sync="to"
                transition="scale-transition"
                offset-y
                min-width="auto"
                :rules="[inputRules.required]"
                outlined            
            >
                <template v-slot:activator="{ on, attrs }">
                <v-text-field
                    v-model.trim="$v.to.$model"
                    label="A"
                    prepend-icon="mdi-calendar"
                    readonly
                    v-bind="attrs"
                    v-on="on"
                ></v-text-field>
                </template>
                <v-date-picker
                v-model.trim="$v.to.$model"
                no-title
                scrollable
                color="primary"
                >
                <v-spacer></v-spacer>
                <v-btn
                    text
                    color="primary"
                    @click="$refs.menu2.save(to)"
                >
                    Conferma
                </v-btn>
                </v-date-picker>
            </v-menu>
            </v-col>
            <v-col
            cols="12"
            >
            <v-expansion-panels>
                <v-expansion-panel>
                <v-expansion-panel-header>
                    <p class="text-subtitle-1">Regole</p>
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                    <vue-editor v-model="$v.rules.$model"></vue-editor>
                </v-expansion-panel-content>
                </v-expansion-panel>
            </v-expansion-panels>
            </v-col>
            <v-col
            cols="12"
            >
            <v-expansion-panels>
                <v-expansion-panel>
                <v-expansion-panel-header>
                    <p class="text-subtitle-1">Privacy</p>
                </v-expansion-panel-header>
                <v-expansion-panel-content>
                    <vue-editor v-model="$v.privacy.$model"></vue-editor>
                </v-expansion-panel-content>
                </v-expansion-panel>
            </v-expansion-panels>         
            </v-col>
            <v-col 
            cols="3"
            >
            <v-form
            >
                <p class="text-subtitle-1">Mezzi</p>
                <div v-for="mean in arrayMeans" v-bind:key="mean.value">
                <v-checkbox
                    v-model="$v.means.$model"
                    :label= mean.text
                    :value="mean.value"
                    :rules="meanRules"
                    hide-details
                ></v-checkbox>
                </div>
            </v-form>
            </v-col>
            <v-col 
            cols="3"
            >
            <p class="text-subtitle-1">Attiva Campagna</p>
            <v-switch
                v-if="active === false"
                v-model="active"
                label="La campagna è disattivata"
            ></v-switch>
            <v-switch
                v-if="active === true"
                v-model="active"
                label="La campagna è attivata"
            ></v-switch>
            </v-col>
            <v-col
            cols="6"
            > 
            <v-autocomplete
                label="Applicazione"
                placeholder="Applicazione *"
                :rules="[inputRules.required]"
                v-model.trim="$v.application.$model"
                :items="listaApplications"
                outlined
            ></v-autocomplete>
            </v-col>
        </v-row>
        </div>
    </form>
    </template>
      <template v-slot:footer>
        <v-btn
            text
            @click="closeThisModal"
            class="py-8 ml-8"
        >
            Annulla
        </v-btn>
        <v-btn
            color="primary"
            text
            @click="saveCampaign"
            class="py-8 ml-8"
        >
            Salva
        </v-btn>
        </template>
    </modal>
</template>


<script>
import { required } from "vuelidate/lib/validators";
import EventBus from "@/components/eventBus";
import { campaignService } from "../../services";
import {mapActions, mapState} from "vuex";
import Modal from "@/components/Modal.vue";

export default {
  props:{
      typeCall: String,
  },
  components:{
    "modal": Modal,
    },
  data() {
    return {
      arrayMeans: [],
      applications: [],
      campaign: {},
      id: null,
      logo: "",
      title: "",
      description: "",
      from: "",
      to: "",
      rules: "",
      privacy: "",
      means: [],
      active: false,
      application: "",
      edit: false,
      listaApplications:['ciao','pippo'],
      menu:false,
      menu2:false,
      inputRules: {
        required: value => !!value || 'Campo richiesto.',
      },
      panel: [0],
      popup: {
          title: "",
      }
    };
  },
  validations: {
    id: {
      required,
    },
    logo: {
      required,
    },
    title: {
      required,
    },
    description: {
      required,
    },
    from: {
      required,
    },
    to: {
      required,
    },
    rules: {
      required,
    },
    privacy: {
      required,
    },
    means: {
      required,
    },
    active: {
      required,
    },
    application: {
      required,
    },
  },
  methods: {
    ...mapActions("modal", {closeModal:"closeModal"}),
    ...mapActions("campaign", {addCampaign: "addCampaign", updateCampaign:"updateCampaign"}),
    initCampaign() {
      this.campaign = {};
      this.id = null;
      this.logo = "";
      this.title = "";
      this.description = "";
      this.from = "";
      this.to = "";
      this.rules = "";
      this.privacy = "";
      this.means = [];
      this.active = false;
      this.application = "";
    },
    copyFormValues(campaign) {
      for (const [key] of Object.entries(campaign)) {
        this[key] = campaign[key];
      }
    },
    createCampaign() {
      this.campaign = {
        id: this.id,
        logo: this.logo,
        title: this.title,
        description: this.description,
        from: this.from,
        to: this.to,
        rules: this.rules,
        privacy: this.privacy,
        means: this.means,
        active: this.active,
        application: this.application,
      };
    },
    setModalData(){
        if(this.typeCall == "add"){
            this.initCampaign();
            this.popup.title = "Aggiungi Campagna";
            console.log("Modalità AGGIUNGI");
        }else if (this.typeCall == "edit") {
            this.copyFormValues(this.actualCampaign.item);
            this.popup.title = "Modifica Campagna";
            console.log("Modalità MODIFICA");
        }
    },
    saveCampaign() {    
        //EventBus.$emit("CHECK_COMPANY_FORM");
        this.createCampaign();
        if(this.typeCall == "add"){
            this.addCampaign({companyId: this.actualCompany.item.id, campaign: this.campaign});
            this.closeModal();
        }else if (this.typeCall == "edit") {
            console.log(this.campaign);
            this.updateCampaign({companyId: this.actualCompany.item.id, campaign: this.campaign});
            this.closeModal();
            
        }
        this.$v.$touch();
    },
    closeThisModal(){
        this.$v.$reset();
        this.closeModal();
    },
  },
    computed: {
    ...mapState("account", ["status", "user", "role"]),
    ...mapState("campaign", ["actualCampaign"]),
    ...mapState("company", ["actualCompany"]),
    meanRules() {
      return [
        this.means.length > 0 || "Seleziona almeno un mezzo."
      ];
    },
  },
  mounted() {
    if (this.role == 'ROLE_ADMIN' && this.adminCompany == null){
    campaignService.getApplications().then((res) => {
      console.log(this.applications);
      this.applications = res;
      for (let i=0;i<this.applications.length; i++){
          this.listaApplications.push(this.applications[i].name)      
      }
      console.log(JSON.stringify(this.listaApplications))
    });
    }
    this.arrayMeans = campaignService.getArrayMeans();

    EventBus.$on("EDIT_CAMPAIGN_FORM", (campaign) => {
            this.edit = true;
           this.copyFormValues(campaign);
    });
    EventBus.$on("NEW_CAMPAIGN_FORM", () => {
            this.edit = false;

      this.initCampaign();
    });
    EventBus.$on("CHECK_CAMPAIGN_FORM", () => {
      this.$v.$touch();
      if (this.$v.$invalid) {
        //generate event no
        EventBus.$emit("NO_CAMPAIGN_FORM");
      } else {
        //   generate event ok
        this.createCampaign();
        EventBus.$emit("OK_CAMPAIGN_FORM", this.campaign);
        this.$v.$reset();
      }
    });

  },
    created() {
        this.setModalData();
    },
    watch: {
        typeCall: function(){
            this.setModalData();
        },
        actualCampaign: function(){
            this.setModalData();
        }
    },

  beforeDestroy() {
    EventBus.$off("CHECK_CAMPAIGN_FORM");
    EventBus.$off("NEW_CAMPAIGN_FORM");
    EventBus.$off("EDIT_CAMPAIGN_FORM");
  },
};
</script>
<style scoped></style>
