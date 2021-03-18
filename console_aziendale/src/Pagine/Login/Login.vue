<template>
  <html lang="en">
    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <meta http-equiv="X-UA-Compatible" content="ie=edge" />

      <link rel="stylesheet" href="dist.css" />

      <title>Login</title>
    </head>
    <body class="bg-primary h-screen w-screen font-sans relative">
      <div class="container mx-auto h-full flex justify-center items-center padding-bottom">
        <div class="w-1/3">
          <img src="@/assets/images/pgaziendale.png" alt="Logo" class="max-h-40 m-auto"/>

          <h1 class="font-hairline mb-6 text-center text-white text-3xl">
            Entra in Aziende Play&Go
          </h1>
          <div class="border-teal p-4  border-t-12 bg-white mb-6 rounded-lg shadow-lg">
            <div class="mb-4">
              <label class="font-bold block mb-2 text-primary text-center"
                >Username</label
              >
              <input
                type="text"
                class="block appearance-none w-full bg-white border border-grey-light hover:border-grey px-2 py-2 rounded shadow"
                placeholder="Il tuo username"
                v-model="username"
                name="username"
                id="username"
              />
            </div>

            <div class="mb-4">
              <label class="font-bold text-primary block mb-2 text-center"
                >Password</label
              >
              <div class="relative">
                <input
                  :type="passwordFieldType"
                  class="block appearance-none w-full bg-white border border-grey-light hover:border-grey px-2 py-2 rounded shadow"
                  placeholder="La tua password"
                  v-model="password"
                  name="password"
                  id="password"
                />
                <div
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm leading-5"
                >
                  <eye-off-icon
                    @click="switchVisibility"
                    :class="{
                      block: passwordFieldType == 'password',
                      hidden: passwordFieldType != 'text',
                    }"
                  />
                  <eye-icon
                    @click="switchVisibility"
                    :class="{
                      block: passwordFieldType != 'password',
                      hidden: passwordFieldType == 'text',
                    }"
                  />
                </div>
              </div>
            </div>
            <div class="flex items-center justify-between">
              <div class="form-group w-full">
                <button
                  class="bg-blue hover:bg-blue-dark text-white border-primary bg-primary border-solid border-4 font-bold py-2 px-4 rounded text-center w-full"
                  type="button"
                  @click="handleSubmit"
                >
                  Login
                </button>
                <p class="pwd-forgot text-primary" @click="resetPwd">Password dimenticata?</p>
              </div>
            </div>
          </div>
        </div>
      </div>
            <app-footer class="bottom-0"/>
    </body>
  </html>
</template>

<script>
import { mapState, mapActions } from "vuex";
import Footer from "@/components/Footer"

export default {
  components: {
    "app-footer":Footer
  },
  data() {
    return {
      username: "",
      password: "",
      passwordFieldType: "password",
      submitted: false,
    };
  },
  computed: {
    ...mapState("account", ["status"]),
  },
  created() {},
  methods: {
    ...mapActions("account", ["login", "logout"]),
    handleSubmit() {
      this.submitted = true;
      const { username, password } = this;
      if (username && password) {
        this.login({ username, password });
      }
    },
    resetPwd(){
      this.$router.push('resetpwd');
    },
    switchVisibility() {
      this.passwordFieldType =
        this.passwordFieldType === "password" ? "text" : "password";
    },
  },
};
</script>
<style scoped>
.pwd-forgot{
  text-align: center;
  margin: 10px 0px;
  font-weight: bold;
}
</style>
