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
              <label class="font-bold block ml-5 text-primary text-left"
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

            <div class="mb-4">
              <label class="font-bold text-primary block ml-5 text-left"
                >Password</label
              >
              <div class="relative">
                <v-col
                cols="12"
                >
                  <v-text-field
                    :type="passwordFieldType"
                    placeholder="La tua password"
                    v-model="password"
                    name="password"
                    id="password"
                    solo
                    hide-details
                  ></v-text-field>
                </v-col>
                <div
                  class="absolute inset-y-0 right-0 pr-5 flex items-center text-sm leading-5"
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

            <v-row class="mb-8">
              <v-col
              cols="4"
              class="p-0"
              >
                <v-checkbox
                  class="px-7 py-0 m-0"
                  v-model="EventualeVariabile"
                  label="Ricordami"
                  color="primary"
                  value="primary"
                  hide-details
                ></v-checkbox>
              </v-col>
              <v-col
              cols="2"
              class="p-0 m-0"
              >
              </v-col>
              <v-col
              cols="6"
              class="p-0 m-0"
              >
                <p class="pwd-forgot text-right m-0 px-8" @click="resetPwd">Password dimenticata?</p>
              </v-col>
            </v-row>
            <v-row>
            </v-row>
            
            <div class="flex items-center justify-between">
              <div class="form-group w-full">
                <v-btn
                  depressed
                  color="primary"
                  class="bg-blue hover:bg-blue-dark text-white font-bold py-2 px-10 rounded text-center w-full"
                  type="button"
                  @click="handleSubmit"
                >
                  Login
                </v-btn>
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
  font-style: italic;
  color: #757575;
  cursor: pointer;
  font-weight: 600;
}
.eye-off-icon{
  cursor: pointer;
}
.eye-icon{
  cursor: pointer;
}
</style>
