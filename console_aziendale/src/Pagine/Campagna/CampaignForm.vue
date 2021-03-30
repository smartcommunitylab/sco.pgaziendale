<template>
  <form action="" id="addCampaign">
          <div class="mb-20 flex flex-wrap justify-between">
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.id.$error }">
                <label class="field-label" for="first_name">Id </label>
                <input
                  type="text"
                  name="campaignId"
                  placeholder="Id *"
                  v-model.trim="$v.id.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="campaignLogo"
                />
              </div>
              <div v-if="$v.logo.$error">
                <div class="error" v-if="!$v.id.required">Il campo Id e' richiesto.</div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.logo.$error }">
                <label class="field-label" for="first_name">Logo </label>
                <input
                  type="text"
                  name="campaignLogo"
                  placeholder="Logo *"
                  v-model.trim="$v.logo.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="campaignLogo"
                />
              </div>
              <div v-if="$v.logo.$error">
                <div class="error" v-if="!$v.logo.required">
                  Il campo Logo e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-4 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.title.$error }">
                <label class="field-label" for="first_name">Titolo </label>
                <input
                  type="text"
                  name="campaignTitle"
                  placeholder="Titolo *"
                  v-model.trim="$v.title.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  id="campaignTitle"
                />
              </div>
              <div v-if="$v.title.$error">
                <div class="error" v-if="!$v.title.required">
                  Il campo titolo e' richiesto.
                </div>
              </div>
            </div>

            <div class="field-group mb-4 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.description.$error }"
              >
                <label class="field-label" for="first_name">Descrizione</label>
                <input
                  type="text"
                  name="campaignDescription"
                  id="campaignDescription"
                  placeholder="Descrizione *"
                  v-model.trim="$v.description.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.description.$error">
                <div class="error" v-if="!$v.description.required">
                  Il campo descrizione e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group flex"
                :class="{ 'form-group--error': $v.from.$error }"
              >
                <label class="field-label" for="password">Da </label>
                <VueTailwindPicker
                  :theme="{
                    background: '#1A202C',
                    text: 'text-white',
                    border: 'border-gray-700',
                    currentColor: 'text-gray-200',
                    navigation: {
                      background: 'bg-gray-800',
                      hover: 'hover:bg-gray-700',
                      focus: 'bg-gray-700',
                    },
                    picker: {
                      rounded: 'rounded-md',
                      selected: {
                        background: 'bg-teal-400',
                        border: 'border-teal-400',
                        hover: 'hover:border-teal-400',
                      },
                      holiday: 'text-red-400',
                      weekend: 'text-green-400',
                      event: 'bg-blue-500',
                    },
                    event: {
                      border: 'border-gray-700',
                    },
                  }"
                  @change="(v) => ($v.from.$model = v)"
                >
                  <input
                    type="text"
                    name="campaignFrom"
                    id=""
                    required
                    placeholder="Da *"
                    autocomplete="off"
                    v-model.trim="$v.from.$model"
                    class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  />
                </VueTailwindPicker>
              </div>
              <div v-if="$v.from.$error">
                <div class="error" v-if="!$v.from.required">
                  Il campo da e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group flex" :class="{ 'form-group--error': $v.to.$error }">
                <label class="field-label" for="password">A </label>
                <VueTailwindPicker
                  :theme="{
                    background: '#1A202C',
                    text: 'text-white',
                    border: 'border-gray-700',
                    currentColor: 'text-gray-200',
                    navigation: {
                      background: 'bg-gray-800',
                      hover: 'hover:bg-gray-700',
                      focus: 'bg-gray-700',
                    },
                    picker: {
                      rounded: 'rounded-md',
                      selected: {
                        background: 'bg-teal-400',
                        border: 'border-teal-400',
                        hover: 'hover:border-teal-400',
                      },
                      holiday: 'text-red-400',
                      weekend: 'text-green-400',
                      event: 'bg-blue-500',
                    },
                    event: {
                      border: 'border-gray-700',
                    },
                  }"
                  @change="(v) => ($v.to.$model = v)"
                >
                  <input
                    type="text"
                    name="campaignTo"
                    id=""
                    required
                    placeholder="A *"
                    autocomplete="off"
                    v-model.trim="$v.to.$model"
                    class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                  />
                </VueTailwindPicker>
              </div>
              <div v-if="$v.to.$error">
                <div class="error" v-if="!$v.to.required">Il campo a e' richiesto.</div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.rules.$error }">
                <label class="field-label" for="password">Regole</label>
                <vue-editor v-model="$v.rules.$model"></vue-editor>
              
              </div>
              <div v-if="$v.rules.$error">
                <div class="error" v-if="!$v.rules.required">
                  Il campo Regole e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.privacy.$error }">
                <label class="field-label" for="password">Privacy</label>
                <vue-editor v-model="$v.privacy.$model"></vue-editor>
             
              </div>
              <div v-if="$v.privacy.$error">
                <div class="error" v-if="!$v.privacy.required">
                  Il campo privacy e' richiesto.
                </div>
              </div>
            </div>
            <div class="block">
              <span class="text-gray-700">Mezzi</span>
              <div class="field-group mb-6 w-full">
                <div class="form-group" :class="{ 'form-group--error': $v.means.$error }">
                  <div v-for="mean in arrayMeans" v-bind:key="mean.value">
                    <label class="inline-flex items-center">
                      <input
                        type="checkbox"
                        class="form-checkbox"
                        v-model.trim="$v.means.$model"
                        :value="mean.value"
                      />
                      <span class="ml-2">{{mean.text}}</span>
                    </label>
                  </div>
                 
                </div>
                <div v-if="$v.means.$error">
                  <div class="error" v-if="!$v.means.required">
                    Il campo Mezzi e' richiesto.
                  </div>
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div class="form-group" :class="{ 'form-group--error': $v.active.$error }">
                <label class="field-label" for="password">Attiva</label>
                <input
                  type="checkbox"
                  name="campaignActive"
                  id=""
                  required
                  placeholder="Attiva *"
                  v-model.trim="$v.active.$model"
                  class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
                />
              </div>
              <div v-if="$v.active.$error">
                <div class="error" v-if="!$v.active.required">
                  Il campo Attiva e' richiesto.
                </div>
              </div>
            </div>
            <div class="field-group mb-6 w-full">
              <div
                class="form-group"
                :class="{ 'form-group--error': $v.application.$error }"
              >
                <label class="field-label" for="password">Applicazione</label>
                  <select class="form-select mt-1 block w-full" v-model.trim="$v.application.$model">
                    <option v-for="app in applications" :key="app.id" :value="app.id">{{app.name}}</option>

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
import VueTailwindPicker from "vue-tailwind-picker";
import { campaignService } from '../../services';

export default {
  components: {VueTailwindPicker},
  data() {
    return {
      arrayMeans: [],
      applications:[],
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
  mounted() {
        campaignService.getApplications().then(res=> {
       this.applications=res;
    })
        this.arrayMeans=campaignService.getArrayMeans();

    EventBus.$on("EDIT_CAMPAIGN_FORM", campaign => {
        this.copyFormValues(campaign);
    });
     EventBus.$on("NEW_CAMPAIGN_FORM", () => {
         this.initCampaign()
    });
    EventBus.$on("CHECK_CAMPAIGN_FORM", () => {
      this.$v.$touch();
      if (this.$v.$invalid) {
        //generate event no
        EventBus.$emit("NO_CAMPAIGN_FORM");
      } else {
        //   generate event ok
        this.createCampaign();
        EventBus.$emit("OK_CAMPAIGN_FORM",this.campaign);
        this.$v.$reset();
      }
    });
  },

 beforeDestroy () {
    EventBus.$off('CHECK_CAMPAIGN_FORM');
    EventBus.$off('NEW_CAMPAIGN_FORM');
    EventBus.$off('EDIT_CAMPAIGN_FORM');
 },
};
</script>
<style scoped></style>
