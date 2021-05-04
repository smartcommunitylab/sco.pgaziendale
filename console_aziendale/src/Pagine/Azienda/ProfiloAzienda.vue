<template>
  <div>
    <div class="w-full max-w-4xl flex h-full flex-wrap mx-auto my-32 lg:my-0 lg:mr-16">
      <div
        id="profile"
        class="min-w-full w-full lg:w-3/5 rounded-lg lg:rounded-l-lg lg:rounded-r-none bg-white opacity-75 mx-6 lg:mx-0"
      >
        <div class="w-full">
          <button
            v-show="$route.name !== 'azienda'"
            @click="deleteAzienda"
            class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
          >
            <delete-icon />
          </button>
          <button
            v-if="role == 'ROLE_ADMIN'"
            @click="editAzienda"
            class="float-right bg-grey-light hover:bg-grey text-grey-darkest font-bold py-2 px-4 rounded inline-flex items-center"
          >
            <pencil-outline-icon />
          </button>
        </div>
        <div
          class="p-4 md:p-12 text-center lg:text-left"
          v-if="actualCompany && actualCompany.item"
        >
          <div v-if="actualCompany.item.logo"
            class="block rounded-full shadow-xl mx-auto h-48 w-48 bg-cover bg-center"
            v-bind:style="{ backgroundImage: 'url(' + actualCompany.item.logo + ')' }"
          ></div>
          <div v-else class="mt-10">
          </div>
          <h1 class="text-3xl font-bold pt-8 lg:pt-0">{{ actualCompany.item.name }}</h1>
          <div
            class="mx-auto lg:mx-0 w-4/5 pt-3 border-b-2 border-green-500 opacity-25"
          ></div>
          <div class="pt-4 text-base font-bold items-center lg:justify-start">
            <div class="flex">
              <address-icon />
              <span class="detail-company">{{ actualCompany.item.address }}</span>
              <span class="detail-company">{{ actualCompany.item.streetNumber }} </span>
            </div>
            <div class="flex">
              <span class="w-6"></span>
              <span class="detail-company">{{ actualCompany.item.city }}</span>
              <span class="detail-company">{{ actualCompany.item.province }}</span>
              <span class="detail-company">{{ actualCompany.item.zip }}</span>
            </div>
          </div>

          <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center lg:justify-start"
          >
            <web-icon />
            <a
              class="link-web"
              :href="actualCompany.item.web"
              v-if="actualCompany.item.web"
              >{{ actualCompany.item.web }}</a
            ><span class="detail-company" v-else>Non é presente</span>
          </p>
          <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center lg:justify-start"
          >
            <email-icon />
            <a class="link-web" :href="`mailto:${actualCompany.item.contactEmail}`">{{
              actualCompany.item.contactEmail
            }}</a>
          </p>
          <p
            class="pt-2 text-gray-600 text-xs lg:text-sm flex items-center lg:justify-start"
          >
            <phone-icon />
            <span class="detail-company">{{ actualCompany.item.contactPhone }}</span>
          </p>
        </div>
        <div class="w-full flex">
          <button
            v-if="!adminCompany && actualCompany && role == 'ROLE_ADMIN'"
            type="button"
            class="btn-admin m-auto"
            @click="chooseCompanyAdmin"
            aria-label="Close modal"
          >
            Diventa amministratore
          </button>
          <button
            v-if="adminCompany && $route.name !== 'azienda'"
            class="btn-admin m-auto"
          >
            Sei amministratore
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState, mapActions } from "vuex";
import EventBus from "@/components/eventBus";
export default {
  name: "ProfiloAzienda",
  data() {
    return {};
  },

  computed: {
    ...mapState("company", ["adminCompany", "actualCompany"]),
    ...mapState("account", ["role"]),
  },
  methods: {
    ...mapActions("company", {
      getCompanyById: "getCompanyById",
      chooseCompanyAdminCall: "chooseCompanyAdmin",
    }),

    deleteAzienda() {
      EventBus.$emit("DELETE_COMPANY", this.actualCompany);
    },
    editAzienda() {
      EventBus.$emit("EDIT_COMPANY", this.actualCompany);
    },
    chooseCompanyAdmin() {
      this.chooseCompanyAdminCall(this.actualCompany);
    },
  },
};
</script>

<style scoped>
.btn-admin {
  border: none;
  font-size: 20px;
  padding: 20px;
  cursor: pointer;
  font-weight: bold;
  color: white;
  background: #5ab45f;
  border-radius: 26px;
}
.detail-company {
  margin: 4px 8px;
  font-size: medium;
}
.link-web {
  margin: 4px 8px;
  font-size: medium;
  text-decoration: underline;
  color: #5ab45f;
}
</style>
