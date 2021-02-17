<template>
  <div>
    <div class="w-full max-w-4xl flex h-full flex-wrap mx-auto my-32 lg:my-0 lg:mr-16">
      <div
        id="profile"
        class="min-w-full w-full lg:w-3/5 rounded-lg lg:rounded-l-lg lg:rounded-r-none shadow-2xl bg-white opacity-75 mx-6 lg:mx-0"
      >
        <div class="w-full">
          <button
            @click="deleteAzienda"
            class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
          >
            <delete-icon />
          </button>
          <button
            @click="editAzienda"
            class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
          >
            <pencil-outline-icon />
          </button>
        </div>
        <div class="p-4 md:p-12 text-center lg:text-left" >
          <!-- Image for mobile view-->
          <div
            class="block rounded-full shadow-xl mx-auto -mt-16 h-48 w-48 bg-cover bg-center"
            v-bind:style="{ backgroundImage: 'url(' + actualCompany.item.logo + ')' }"
          ></div>
          <h1 class="text-3xl font-bold pt-8 lg:pt-0">{{ actualCompany.item.name }}</h1>
          <div
            class="mx-auto lg:mx-0 w-4/5 pt-3 border-b-2 border-green-500 opacity-25"
          ></div>
          <p
            class="pt-4 text-base font-bold flex items-center justify-center lg:justify-start"
          >
            <address-icon />{{ actualCompany.item.address }}
          </p>
          <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
          >
            <web-icon /> {{ actualCompany.item.web }}
          </p>
          <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
          >
            <email-icon /> {{ actualCompany.item.contactEmail }}
          </p>
          <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
          >
            <phone-icon /> {{ actualCompany.item.contactPhone }}
          </p>
        </div>
      </div>
      <div></div>
    </div>
  </div>
</template>
<script>
import { mapState, mapActions } from "vuex";
import EventBus from "../components/eventBus";
export default {
  name: "ProfiloAzienda",
  data() {
    return {};
  },

  computed: {
    ...mapState("company", ["actualCompany"]),
  },
  methods: {
	...mapActions("company", { getCompanyById: "getCompanyById" }),
	deleteAzienda() {
		EventBus.$emit("DELETE_COMPANY",this.actualCompany);
	},
	editAzienda() {
		EventBus.$emit("EDIT_COMPANY",this.actualCompany);
	}
  },
};
</script>

<style scoped></style>
