<template>
    <div class="bg-primary h-screen w-screen font-sans relative">
      <div class="container h-full flex justify-center items-center">
        <div class="">
          <img src="@/assets/images/pgaziendale.png" alt="Logo" class="max-h-40 m-auto"/>

          <h1 class="font-hairline mb-6 text-center text-white text-3xl">
            Entra in Aziende Play&Go
          </h1>
          
          <div class="border-teal p-4 border-t-12 bg-white mb-6 rounded-lg shadow-lg">
            <div class="mb-4">
              <label class="font-bold block ml-5 text-primary text-left"
                >Username</label
              >
              <v-col
                cols="12"
              >
                <v-text-field
                  :rules="[rules.required]"
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
                    :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show ? 'text' : 'password'"
                    :rules="[rules.required]"
                    class="input-group--focused"
                    placeholder="La tua password"
                    v-model="password"
                    name="password"
                    id="password"
                    solo
                    hide-details
                    @click:append="show = !show"
                  ></v-text-field>
                </v-col>
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
              cols="8"
              class="text-right p-0 m-0"
              >
                <span class="pwd-forgot text-right m-0 pr-8" @click="resetPwd">Password dimenticata?</span>
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
    </div>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  data() {
    return {
      username: "",
      password: "",
      passwordFieldType: "password",
      submitted: false,
      show: false,
      rules: {
          required: value => !!value || 'Campo richiesto.',
      },
    };
  },

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
  },

  computed: {
    ...mapState("account", ["status"]),
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
  text-decoration: underline 0.1em rgba(0, 0, 0, 0);
  transition: text-decoration-color 500ms;
  transition: text-color 500ms;
}
.pwd-forgot:hover{
  color: #212121;
  text-decoration-color: #212121;
}
.eye-off-icon{
  cursor: pointer;
}
.eye-icon{
  cursor: pointer;
}
</style>
