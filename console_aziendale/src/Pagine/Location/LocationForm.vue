<template>
  <form action="" id="addLocation">
    <div class="mb-20 flex flex-wrap justify-between">
      <div class="field-group mb-4 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.id.$error }">
          <label class="field-label" for="first_name">Identificativo </label>
          <input
            type="text"
            name="campaignCode"
            placeholder="Identificativo *"
            v-model.trim="$v.id.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
            id="campaignCode"
          />
                    <info-box :msg="'Codice univoco della sede'" />
        </div>
        <div v-if="$v.id.$error">
          <div class="error" v-if="!$v.id.required">
            Il campo Identificativo e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-4 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.address.$error }">
          <label class="field-label" for="first_name">Indirizzo </label>
          <input
            type="text"
            name="campaignAddress"
            placeholder="Indirizzo *"
            v-model.trim="$v.address.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
            id="campaignTitle"
          />
        </div>
        <div v-if="$v.address.$error">
          <div class="error" v-if="!$v.address.required">
            Il campo indirizzo e' richiesto.
          </div>
        </div>
      </div>

      <div class="field-group mb-4 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.streetNumber.$error }">
          <label class="field-label" for="first_name">Numero</label>
          <input
            type="text"
            name="campaignstreetNumber"
            id="campaignstreetNumber"
            placeholder="Number *"
            v-model.trim="$v.streetNumber.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
        </div>
        <div v-if="$v.streetNumber.$error">
          <div class="error" v-if="!$v.streetNumber.required">
            Il campo Numero e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.zip.$error }">
          <label class="field-label" for="password">CAP </label>
          <input
            type="text"
            name="campaignZip"
            id=""
            required
            placeholder="CAP *"
            v-model.trim="$v.zip.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
        </div>
        <div v-if="$v.zip.$error">
          <div class="error" v-if="!$v.zip.required">Il campo CAP e' richiesto.</div>
        </div>
      </div>

      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.city.$error }">
          <label class="field-label" for="password">Cittá </label>
          <input
            type="text"
            name="campaignCity"
            id=""
            required
            placeholder="Cittá *"
            v-model.trim="$v.city.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
        </div>
        <div v-if="$v.city.$error">
          <div class="error" v-if="!$v.city.required">Il campo Cittá e' richiesto.</div>
        </div>
      </div>

      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.province.$error }">
          <label class="field-label" for="password">Provincia</label>
          <input
            type="text"
            name="campaignProvince"
            id=""
            required
            placeholder="Provincia *"
            v-model.trim="$v.province.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
        </div>
        <div v-if="$v.province.$error">
          <div class="error" v-if="!$v.province.required">
            Il campo Provincia e' richiesto.
          </div>
        </div>
      </div>

      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.region.$error }">
          <label class="field-label" for="password">Regione</label>
          <input
            type="text"
            name="campaignRegion"
            id=""
            required
            placeholder="Regione *"
            v-model.trim="$v.region.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
        </div>
        <div v-if="$v.region.$error">
          <div class="error" v-if="!$v.region.required">
            Il campo Regione e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.country.$error }">
          <label class="field-label" for="password">Stato</label>
          <input
            type="text"
            name="campaignCountry"
            id=""
            required
            placeholder="Stato *"
            v-model.trim="$v.country.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
        </div>
        <div v-if="$v.country.$error">
          <div class="error" v-if="!$v.country.required">
            Il campo Stato e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.radius.$error }">
          <label class="field-label" for="password">Raggio</label>
          <input
            type="text"
            name="campaignRadius"
            id=""
            required
            placeholder="Raggio *"
            v-model.trim="$v.radius.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
                              <info-box :msg="'Distanza in metri all\'interno di cui i viaggi dei dipendenti risultano essere validi'" />

        </div>
        <div v-if="$v.radius.$error">
          <div class="error" v-if="!$v.radius.required">
            Il campo Raggio e' richiesto.
          </div>
                    <div class="error" v-if="!$v.radius.numeric">
            Il campo Raggio non risulta essere un numero.
          </div>
        </div>
      </div>
      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.latitude.$error }">
          <label class="field-label" for="password">Latitudine</label>
          <input
            type="text"
            name="campaignMeans"
            id=""
            required
            placeholder="Mezzi *"
            v-model.trim="$v.latitude.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
                                        <info-box :msg="'Latitudine in gradi'" />

        </div>
        <div v-if="$v.latitude.$error">
          <div class="error" v-if="!$v.latitude.required">
            Il campo Latitudine e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.longitude.$error }">
          <label class="field-label" for="password">Longitudine</label>
          <input
            type="text"
            name="campaignLongitude"
            id=""
            required
            placeholder="Longitudine *"
            v-model.trim="$v.longitude.$model"
            class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2"
          />
          <info-box :msg="'Longitudine in gradi'" />
        </div>
        <div v-if="$v.longitude.$error">
          <div class="error" v-if="!$v.longitude.required">
            Il campo Longitudine e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-6 w-full">
        <div class="form-group" :class="{ 'form-group--error': $v.nonWorking.$error }">
          <label class="field-label" for="password">Giorni NON lavorativi</label>
          <input
            
            class="focus:border-blue-600 p-2 mb-2 flex-1 mr-2"
          />
                    <info-box :msg="'I giorni della settimana in cui la sede chiude'"  />
          <div class="container">
          <div v-for="day in arrayDays" v-bind:key="day.value">
            <div class="field-label" />
            <label class="inline-flex items-center">
              <input
                type="checkbox"
                class=" inline-flexform-checkbox"
                v-model.trim="$v.nonWorking.$model"
                :value="day.value"
              />
              <span class="ml-2">{{ day.text }}</span>
            </label>
          </div>
          </div>
        </div>
        <div v-if="$v.nonWorking.$error">
          <div class="error" v-if="!$v.nonWorking.required">
            Il campo Giorni NON lavorativi e' richiesto.
          </div>
        </div>
      </div>
      <div class="field-group mb-6 w-full">
        <div
          class="form-group"
          :class="{ 'form-group--error': $v.nonWorkingDays.$error }"
        >
          <label class="field-label" for="password">Giorni di chiusura</label>
                    <input
            
            class="focus:border-blue-600 p-2 mb-2 flex-1 mr-2"
          />
                                          <info-box :msg="'I giorni della settimana in cui la sede chiude'"  />

         <div>
          <div
            v-for="day in $v.nonWorkingDays.$model"
            :key="day"
            class="inline-flex  justify-center items-center m-1 font-medium py-1 px-2 bg-white rounded-full text-indigo-100 bg-indigo-700 border border-indigo-700"
          >
            <div class="text-xs font-normal leading-none max-w-full flex-initial">
              {{ day }}
            </div>
            <div class="flex flex-auto flex-row-reverse" @click="removeDay(day)">
              <div>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="100%"
                  height="100%"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  class="feather feather-x cursor-pointer hover:text-indigo-400 rounded-full w-4 h-4 ml-2"
                >
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </div>
            </div>
          </div>
         </div>
          <div class="relative ">
            <VueTailwindPicker 
            :start-from-monday="true"
                  @change.self="(v) => ($v.newNonWorkingDay.$model = v)"
                >
                   <input
              type="text"
              name="campaignNonWorkingDays"
              id=""
              required
              placeholder="Giorno di chiusura *"
              v-model.trim="$v.newNonWorkingDay.$model"
              class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2 w-full"
            />
                </VueTailwindPicker>
<!--             
            <input
              type="text"
              name="campaignNonWorkingDays"
              id=""
              required
              placeholder="Giorno di chiusura *"
              v-model.trim="$v.newNonWorkingDay.$model"
              class="focus:border-blue-600 border-2 p-2 mb-2 flex-1 mr-2 w-full"
            /> -->

            <div
              class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm leading-5"
            >
              <add-icon @click="addDays($v.newNonWorkingDay.$model)" />
            </div>
          </div>
        </div>
        <div v-if="$v.nonWorkingDays.$error">
          <div class="error" v-if="!$v.nonWorkingDays.required">
            Il campo Giorni di chiusura e' richiesto.
          </div>
        </div>
      </div>
    </div>
  </form>
</template>
<script>
import { required,numeric } from "vuelidate/lib/validators";
import { locationService } from "../../services";
import EventBus from "@/components/eventBus";
import InfoBox from "@/components/InfoBox.vue";
import VueTailwindPicker from "vue-tailwind-picker";

export default {
    components: {
    InfoBox,
    VueTailwindPicker
  },
  data() {
    return {
      location: {},
      id: "",
      address: "",
      streetNumber: "",
      zip: "",
      city: "",
      province: "",
      region: "",
      latitude: 0,
      longitude: 0,
      country: "",
      radius: 200,
      nonWorkingDays: [],
      nonWorking: [],
      arrayDays: [],
    };
  },
  methods: {
    stopTheEvent (event) {
      console.log(event)
       event.stopPropagation() 
    },
    addDays(day) {
      if (this.nonWorkingDays==null)
      this.nonWorkingDays=[];
      if ( !this.nonWorkingDays.includes(day))
      this.nonWorkingDays.push(day);
      else {
        //giorno gia' inserito
        console.log("giorno gia' inserito")
      }
    },
    removeDay(day) {
      this.nonWorkingDays = this.nonWorkingDays.filter((elem) => elem != day);
    },
        copyFormValues(location) {
      for (const [key] of Object.entries(location)) {
        this[key] = location[key];
      }
    },
        initLocation() {
      this.id = "";
      this.address = "";
      this.streetNumber = "";
      this.zip = "";
      this.city = "";
      this.province = "";
      this.region = "";
      this.latitude = 0;
      this.longitude = 0;
      this.country = "";
      this.radius = 200;
      this.nonWorkingDays = [];
      this.nonWorking = [];
    },
      createLocation() {
      this.location = {
        id: this.id,
        address: this.address,
        streetNumber: this.streetNumber,
        zip: this.zip,
        city: this.city,
        province: this.province,
        region: this.region,
        latitude: Number.parseFloat(this.latitude),
        longitude: Number.parseFloat(this.longitude),
        nonWorking: this.nonWorking,
        nonWorkingDays: this.nonWorkingDays,
        country: this.country,
        radius: Number.parseInt(this.radius),
      };
    },
  },
  mounted() {
    this.arrayDays = locationService.getArrayDays();
    EventBus.$on("EDIT_LOCATION_FORM", location => {
        this.copyFormValues(location);
    });
     EventBus.$on("NEW_LOCATION_FORM", () => {
         this.initLocation()
    });
    EventBus.$on("CHECK_LOCATION_FORM", () => {
      this.$v.$touch();
      if (this.$v.$invalid) {
        //generate event no
        EventBus.$emit("NO_LOCATION_FORM");
      } else {
        //   generate event ok
        this.createLocation();
        EventBus.$emit("OK_LOCATION_FORM",this.location);
        this.$v.$reset();
      }
    });
  },

 beforeDestroy () {
    EventBus.$off('CHECK_LOCATION_FORM');
    EventBus.$off('NEW_LOCATION_FORM');
    EventBus.$off('EDIT_LOCATION_FORM');
 },
  validations: {
    id: {
      required,
    },
    address: {
      required,
    },
    streetNumber: {
      required,
    },
    zip: {
      required,
    },
    city: {
      required,
    },
    province: {
      required,
    },
    region: {
      required,
    },
    country: {
      required,
    },
    radius: {
      required,
      numeric
    },
    latitude: {
      required,
    },
    longitude: {
      required,
    },
    newNonWorkingDay: {},
    nonWorkingDays: {
      required,
    },
    nonWorking: {
      required,
    },
  },
};
</script>
<style scoped>
.tooltip-day{
transform: translateY(0px);}
.container {
  position: relative;
top: -30px;
}</style>
