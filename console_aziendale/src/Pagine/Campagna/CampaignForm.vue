<template>
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
            :rules="[rules.required]"
            v-model.trim="$v.application.$model"
            :items="listaApplications"
            outlined
          ></v-autocomplete>
        </v-col>
      </v-row>

      
      <div class="field-group mb-6 w-full">
        <div class="flex flex-row" :class="{ 'form-group--error': $v.application.$error }">
          <label class="field-label" for="password">Applicazione</label>
          <select
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-none mr-2 w-40"
            v-model.trim="$v.application.$model"
          >
            <option v-for="app in applications" :key="app.id" :value="app.id">
              {{ app.name }}
            </option>
          </select>
        </div>
        <div v-if="$v.application.$error">
          <div class="error" v-if="!$v.application.required">
            Il campo Applicazione e' richiesto.
          </div>
        </div>
      </div>

    </div>
  </form>
</template>
<script>
import { required } from "vuelidate/lib/validators";
import EventBus from "@/components/eventBus";
import { campaignService } from "../../services";
import {mapState} from "vuex";

export default {
  data() {
    return {
      arrayMeans: [],
      applications: [],
      campaign: {},
      id: "",
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
      listaApplications:[],
      inputRules: {
        required: value => !!value || 'Campo richiesto.',
      },
      panel: [0]
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
    initCampaign() {
      this.campaign = {};
      this.id = "";
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
  },
    computed: {
    ...mapState("account", ["status", "user", "role"]),
    meanRules() {
      return [
        this.means.length > 0 || "Seleziona almeno un mezzo."
      ];
    },
  },
  mounted() {
    if (this.role == 'ROLE_ADMIN' && this.adminCompany == null){
    campaignService.getApplications().then((res) => {
      this.applications = res;
      for (let app in this.applications){
          this.listaApplications.push(app.name)      
      }
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

  beforeDestroy() {
    EventBus.$off("CHECK_CAMPAIGN_FORM");
    EventBus.$off("NEW_CAMPAIGN_FORM");
    EventBus.$off("EDIT_CAMPAIGN_FORM");
  },
};
</script>
<style scoped></style>
