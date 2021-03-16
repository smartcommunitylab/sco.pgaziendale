<template>
  <div class="bg-primary h-screen w-screen font-sans">

    <div class="container mx-auto h-full flex justify-center items-center">
      <div class="w-1/3">
        <img src="@/assets/images/pgaziendale.png" alt="Logo" class="max-h-40 m-auto" />

        <h1 class="font-hairline mb-6 text-center text-white text-3xl">Reset Password</h1>
        <div class="relative border-teal p-4 border-t-12 bg-white mb-6 rounded-lg shadow-lg">
                <div class="backButton" @click="back">
        <back-icon/>
    </div>
            <div v-if="step==1">
          <div class="mb-4" >
            <label class="font-bold block mb-2 text-primary text-center">Username</label>
            <input
              type="text"
              class="block appearance-none w-full bg-white border border-grey-light hover:border-grey px-2 py-2 rounded shadow"
              placeholder="Il tuo username"
              v-model="username"
              name="username"
              id="username"
            />
          </div>
            </div>
            <div v-if="step==2">
          <div class="mb-4">
            <label class="font-bold text-primary block mb-2 text-center"
              >New Password</label
            >
            <div class="relative">
              <input
                :type="passwordFieldTypeFirst"
                class="block appearance-none w-full bg-white border border-grey-light hover:border-grey px-2 py-2 rounded shadow"
                placeholder="La tua password"
                v-model="passwordFirst"
                name="passwordFirst"
                id="passwordFirst"
              />
              <div
                class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm leading-5"
              >
                <eye-off-icon
                  @click="switchVisibilityFirst"
                  :class="{
                    block: passwordFieldTypeFirst == 'password',
                    hidden: passwordFieldTypeFirst != 'text',
                  }"
                />

                <eye-icon
                  @click="switchVisibilityFirst"
                  :class="{
                    block: passwordFieldTypeFirst != 'password',
                    hidden: passwordFieldTypeFirst == 'text',
                  }"
                />
              </div>
            </div>
          </div>
          <div class="mb-4">
            <label class="font-bold text-primary block mb-2 text-center"
              >Repeat new password</label
            >
            <div class="relative">
              <input
                :type="passwordFieldTypeSecond"
                class="block appearance-none w-full bg-white border border-grey-light hover:border-grey px-2 py-2 rounded shadow"
                placeholder="Ripeti la tua password"
                v-model="passwordSecond"
                name="passwordSecond"
                id="passwordSecond"
              />
              <div
                class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm leading-5"
              >
                <eye-off-icon
                  @click="switchVisibilitySecond"
                  :class="{
                    block: passwordFieldTypeSecond == 'password',
                    hidden: passwordFieldTypeSecond != 'text',
                  }"
                />

                <eye-icon
                  @click="switchVisibilitySecond"
                  :class="{
                    block: passwordFieldTypeSecond != 'password',
                    hidden: passwordFieldTypeSecond == 'text',
                  }"
                />
              </div>
            </div>
          </div>
            </div>
          <div class="flex items-center justify-between">
            <div class="form-group w-full">
              <button v-if="step==1"
                class="bg-blue hover:bg-blue-dark text-white border-primary bg-primary border-solid border-4 font-bold py-2 px-4 rounded text-center w-full"
                type="button"
                @click="resetInit"
              >
                Invia email di reset 
              </button>
              <button  v-if="step==2"
                class="bg-blue hover:bg-blue-dark text-white border-primary bg-primary border-solid border-4 font-bold py-2 px-4 rounded text-center w-full"
                type="button"
                @click="setPwd"
              >
                Imposta la nuova password
              </button>

            </div>
          </div>
        </div>
      </div>
    </div>
            <app-footer />

  </div>

</template>

<script>
import {mapActions} from 'vuex';
import Footer from "@/components/Footer"

export default {
    components: {
    "app-footer":Footer
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
    };
  },
  created() {
    if (this.$route.query.key) {
      this.step = 2;
      this.key = this.$route.query.key;
    }
  },
  methods: {
        ...mapActions("account", {resetPasswordInit:"resetPasswordInit",resetPasswordFinish:"resetPasswordFinish"}),

      back() {
        this.$router.push('login');
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
      this.resetPasswordFinish({key:this.key,newPassword:this.passwordSecond});
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
    color: rgb(15, 112, 183);;
    background: white;
    border: white 1px solid;
    border-radius: 16px;
    left: 0;
    top: 0;}
    </style>
