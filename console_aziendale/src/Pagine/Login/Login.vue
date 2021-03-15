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
          <img src="@/assets/images/pgaziendale.png" alt="Logo" class="max-h-60 m-auto"/>

          <h1 class="font-hairline mb-6 text-center text-white text-3xl">
            Entra in Aziende Play&Go
          </h1>
          <div class="border-teal p-8 border-t-12 bg-white mb-6 rounded-lg shadow-lg">
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
                <!-- <button type="password" >show / hide</button> -->
                <div
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm leading-5"
                >
                  <!-- <svg class="h-6 text-gray-700" fill="none"  xmlns="http://www.w3.org/2000/svg"
                      viewbox="0 0 576 512"> -->
                  <!-- <path fill="currentColor"
                        d="M572.52 241.4C518.29 135.59 410.93 64 288 64S57.68 135.64 3.48 241.41a32.35 32.35 0 0 0 0 29.19C57.71 376.41 165.07 448 288 448s230.32-71.64 284.52-177.41a32.35 32.35 0 0 0 0-29.19zM288 400a144 144 0 1 1 144-144 143.93 143.93 0 0 1-144 144zm0-240a95.31 95.31 0 0 0-25.31 3.79 47.85 47.85 0 0 1-66.9 66.9A95.78 95.78 0 1 0 288 160z">
                      </path> -->
                  <eye-off-icon
                    @click="switchVisibility"
                    :class="{
                      block: passwordFieldType == 'password',
                      hidden: passwordFieldType != 'text',
                    }"
                  />
                  <!-- </svg> -->

                  <!-- <svg class="h-6 text-gray-700" fill="none" xmlns="http://www.w3.org/2000/svg"
                      viewbox="0 0 640 512"> -->
                  <!-- <path fill="currentColor"
                        d="M320 400c-75.85 0-137.25-58.71-142.9-133.11L72.2 185.82c-13.79 17.3-26.48 35.59-36.72 55.59a32.35 32.35 0 0 0 0 29.19C89.71 376.41 197.07 448 320 448c26.91 0 52.87-4 77.89-10.46L346 397.39a144.13 144.13 0 0 1-26 2.61zm313.82 58.1l-110.55-85.44a331.25 331.25 0 0 0 81.25-102.07 32.35 32.35 0 0 0 0-29.19C550.29 135.59 442.93 64 320 64a308.15 308.15 0 0 0-147.32 37.7L45.46 3.37A16 16 0 0 0 23 6.18L3.37 31.45A16 16 0 0 0 6.18 53.9l588.36 454.73a16 16 0 0 0 22.46-2.81l19.64-25.27a16 16 0 0 0-2.82-22.45zm-183.72-142l-39.3-30.38A94.75 94.75 0 0 0 416 256a94.76 94.76 0 0 0-121.31-92.21A47.65 47.65 0 0 1 304 192a46.64 46.64 0 0 1-1.54 10l-73.61-56.89A142.31 142.31 0 0 1 320 112a143.92 143.92 0 0 1 144 144c0 21.63-5.29 41.79-13.9 60.11z">
                      </path> -->
                  <eye-icon
                    @click="switchVisibility"
                    :class="{
                      block: passwordFieldType != 'password',
                      hidden: passwordFieldType == 'text',
                    }"
                  />
                  <!-- </svg> -->
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
