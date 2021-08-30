<template>
  <div class="bg-primary h-screen w-screen font-sans">
    <div class="container mx-auto h-full flex justify-center items-center">
      <div class="w-1/3">
        <img src="@/assets/images/pgaziendale.png" alt="Logo" class="max-h-40 m-auto" />

        <h1 class="font-hairline mb-6 text-center text-white text-3xl">Reset Password</h1>
        <div
          class="relative border-teal p-4 border-t-12 bg-white mb-6 rounded-lg shadow-lg"
        >
          <!--
          <div class="backButton m-2" @click="back">
            <back-icon />
          </div>
          -->
          
          <v-col
            cols="12"
            class="m-0 p-0"
          >
            <v-btn
              icon
              color=primary
              @click="back"
            >
              <v-icon>mdi-arrow-left</v-icon>
            </v-btn>
          </v-col>


          <div v-if="step == 1">
            <div class="mb-4">
              <label class="font-bold block text-primary mt-2 ml-4 text-left"
                >Username</label
              >
              <v-col
                cols="12"
              >
                <v-text-field
                  placeholder="Il tuo username"
                  v-model="username"
                  name="username"
                  id="username"
                  solo
                  hide-details
                ></v-text-field>
              </v-col>
            </div>
          </div>
          <div v-if="step == 2">
            <div class="mb-4">
              <label class="font-bold text-primary block mt-2 ml-4 text-left"
                >Nuova Password</label
              >
              <div class="relative">
                <v-col
                cols="12"
                >
                  <v-text-field
                    :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show1 ? 'text' : 'password'"
                    class="input-group--focused"
                    placeholder="Nuova password"
                    v-model="passwordFirst"
                    name="passwordFirst"
                    id="passwordFirst"
                    solo
                    hide-details
                    @click:append="show1 = !show1"
                  ></v-text-field>
                </v-col>
              </div>
            </div>
            <div class="mb-4">
              <label class="font-bold text-primary block ml-4 text-left"
                >Ripeti la nuova password</label
              >
              <div class="relative">
                <v-col
                cols="12"
                >
                  <v-text-field
                    :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show2 ? 'text' : 'password'"
                    class="input-group--focused"
                    :class="{ 'password-different': passwordDifferent }"
                    placeholder="Ripeti la password"
                    v-model="passwordSecond"
                    name="passwordSecond"
                    id="passwordSecond"
                    solo
                    hide-details
                    @click:append="show2 = !show2"
                  ></v-text-field>
                </v-col>
              </div>
              <div v-if="passwordDifferent">
                <div class="error">Le due password non coincidono</div>
              </div>
            </div>
          </div>
          <div class="flex items-center justify-between">
            <div class="form-group w-full">
            <v-btn
                depressed
                v-if="step == 1"
                color="primary"
                class="bg-blue hover:bg-blue-dark text-white font-bold py-2 px-10 rounded text-center w-full"
                type="button"
                @click="resetInit"
              >
                Invia Email per il Recupero
              </v-btn>
              <v-btn
                depressed
                v-if="step == 2"
                color="primary"
                class="bg-blue hover:bg-blue-dark text-white font-bold py-2 px-10 rounded text-center w-full"
                type="button"
                @click="setPwd"
              >
                Imposta la nuova password
              </v-btn>
            </div>
          </div>
        </div>
      </div>
    </div>
    <app-footer />
  </div>
</template>

<script>
import { mapActions } from "vuex";
import Footer from "@/components/Footer";

export default {
  components: {
    "app-footer": Footer,
  },
  data() {
    return {
      step: 1,
      key: "",
      username: "",
      passwordFirst: "",
      passwordSecond: "",
      passwordFieldTypeFirst: "password",
      passwordFieldTypeSecond: "password",
      passwordDifferent: false,
      show1: false,
      show2: false,
    };
  },
  created() {
    if (this.$route.query.key) {
      this.step = 2;
      this.key = this.$route.query.key;
    }
  },
  methods: {
    ...mapActions("account", {
      resetPasswordInit: "resetPasswordInit",
      resetPasswordFinish: "resetPasswordFinish",
    }),

    back() {
      this.$router.push("login");
    },
    switchVisibilityFirst() {
      this.passwordFieldTypeFirst =
        this.passwordFieldTypeFirst === "password" ? "text" : "password";
    },
    switchVisibilitySecond() {
      this.passwordFieldTypeSecond =
        this.passwordFieldTypeSecond === "password" ? "text" : "password";
    },
    setPwd() {
      //check if equal
      if (this.passwordFirst === this.passwordSecond) {
        this.passwordDifferent = false;
        this.resetPasswordFinish({ key: this.key, newPassword: this.passwordSecond });
      } else this.passwordDifferent = true;
    },
    resetInit() {
      this.resetPasswordInit(this.username);
    },
  },
};
</script>
<style scoped>
.backButton {
  position: absolute;
  color: rgb(15, 112, 183);
  background: white;
  border: white 1px solid;
  border-radius: 16px;
  left: 0;
  top: 0;
}
.password-different {
  /* color: red; */
  animation: shake 0.82s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}
.error {
  color: red;
  text-align: center;
}
.eye-off-icon{
  cursor: pointer;
}
.eye-icon{
  cursor: pointer;
}
</style>
