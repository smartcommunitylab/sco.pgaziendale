<template>
  <div>
    <div class="w-full max-w-4xl flex h-full flex-wrap mx-auto my-32 lg:my-0 lg:mr-16">
      <div
        id="profile"
        class="min-w-full w-full lg:w-3/5 rounded-lg lg:rounded-l-lg lg:rounded-r-none  bg-white opacity-75 mx-6 lg:mx-0"
      >
        <div class="w-full">
          <button
            @click="deleteEmployee"
            class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
          >
            <delete-icon />
          </button>
          <button
            @click="editEmployee"
            class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
          >
            <pencil-outline-icon />
          </button>
        </div>
        <div class="p-4 md:p-12 text-center lg:text-left" >
          <!-- Image for mobile view-->
          <!-- <div
            class="block rounded-full shadow-xl mx-auto -mt-16 h-48 w-48 bg-cover bg-center"
            v-bind:style="{ backgroundImage: 'url(' + actualEmployee.item.logo + ')' }"
          ></div> -->
          <h1 class="text-3xl font-bold pt-8 lg:pt-0">{{ actualEmployee.item.name }}</h1>
          <div
            class="mx-auto lg:mx-0 w-4/5 pt-3 border-b-2 border-green-500 opacity-25"
          ></div>
          <p
            class="pt-4 text-base font-bold flex items-center justify-center lg:justify-start"
          >
            <address-icon />{{ actualEmployee.item.surname }}
          </p>
          <!-- <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
          >
            <web-icon /> {{ actualEmployee.item.web }}
          </p> -->
          <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
          >
            <email-icon /> {{ actualEmployee.item.companyEmail }}
          </p>
          <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
          >
            <phone-icon /> {{ actualEmployee.item.code }}
          </p>
           <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center justify-center lg:justify-start"
          >
            <phone-icon /> {{ actualEmployee.item.campaigns }}
          </p>
        </div>
      </div>
      <div></div>
    </div>
  </div>
</template>
<script>
import { mapState } from "vuex";
import EventBus from "@/components/eventBus";
export default {
  name: "ProfiloEmployee",
  data() {
    return {};
  },

  computed: {
    ...mapState("employee", ["actualEmployee"]),
  },
  methods: {
	deleteEmployee() {
		EventBus.$emit("DELETE_EMPLOYEE",this.actualEmployee);
	},
	editEmployee() {
		EventBus.$emit("EDIT_EMPLOYEE",this.actualEmployee);
	}
  },
};
</script>

<style scoped></style>
