<template>
    <div class="bg-primary h-screen w-screen font-sans">
      <div class="container h-full flex justify-center items-center">
        <div class="">
          <img src="@/assets/images/pgaziendale.png" alt="Logo" class="max-h-36 m-auto"/>

          <h1 class="font-hairline mb-10 text-center text-white text-3xl">
            Entra in Aziende Play&Go
          </h1>
          
          <v-row
            class="align-content-center justify-center"
          >
            <v-col
              cols="10"
              class="border-teal border-t-12 bg-white rounded-lg shadow-lg m-0"
            >
              <v-row>
                <v-col
                  cols="12"
                >
                  <v-text-field
                    :rules="[rules.required]"
                    label="Username"
                    placeholder="Il tuo username"
                    v-model="username"
                    name="username"
                    id="username"
                    outlined
                    hide-details
                  ></v-text-field>
                </v-col>

                <v-col
                  cols="12"
                >
                  <v-text-field
                    :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show ? 'text' : 'password'"
                    :rules="[rules.required]"
                    class="input-group--focused"
                    label="Password"
                    placeholder="La tua password"
                    v-model="password"
                    name="password"
                    id="password"
                    outlined
                    hide-details
                    @click:append="show = !show"
                  ></v-text-field>
                </v-col>
              </v-row>

              <v-row class="mb-6">
                <v-col
                cols="4"
                class="p-0 m-0"
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
                <v-col
                  cols="12"
                >
                  <v-btn
                    depressed
                    color="primary"
                    class="bg-blue hover:bg-blue-dark text-white font-bold py-2 px-10 rounded text-center w-full"
                    type="button"
                    @click="handleSubmit"
                  >
                    Login
                  </v-btn>
                </v-col>
              </v-row> 
            </v-col>
          </v-row>  
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
