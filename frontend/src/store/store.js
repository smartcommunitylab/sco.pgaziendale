import Vue from 'vue'
import Vuex from 'vuex'
// import routes from '../routes'
// import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    idToken: null,
    user: null,
    campagna:null
  },
  mutations: {
      initApp (state) {
        state.idToken=null;
        state.user=null;
        state.campagna=null;
      },
    authUser (state, userData) {
      state.idToken = userData.token
    },
    storeUser (state, user) {
      state.user = user
    },
    clearAuthData (state) {
      state.idToken = null
    },
    enterCampagna (state,campagna) {
        state.campagna = campagna
    },
    exitCampagna(state) {
        state.campagna = null
    }
  },
  actions: {
      initApp({commit}) {
        commit('initApp')

    },
    enterCampagna ({commit}, campagna) {
        commit('enterCampagna',campagna)

    },
    exitCampagna ({commit}) {
        commit('exitCampagna')
    },
    setLogoutTimer ({commit}, expirationTime) {
      setTimeout(() => {
        commit('clearAuthData')
      }, expirationTime * 1000)
    },
    signup ({commit, dispatch}, authData) {
        var res = {
            data: {
                idToken:'a',
                localId:'123'
            }
        }
          commit('authUser', {
            token: res.data.idToken
          })
          // const now = new Date()
          // const expirationDate = new Date(now.getTime() + res.data.expiresIn * 1000)
          localStorage.setItem('token', res.data.idToken)
          localStorage.setItem('userId', res.data.localId)
          // localStorage.setItem('expirationDate', expirationDate)
          dispatch('storeUser', authData)
          dispatch('setLogoutTimer', res.data.expiresIn)

    },
    loginWithToken ({commit}, dataToken) {
      
        // const now = new Date()
        // const expirationDate = new Date(now.getTime() + dataToken.expiresIn * 1000)
        localStorage.setItem('token', dataToken.idToken)
        // localStorage.setItem('expirationDate', expirationDate)
        commit('authUser', {
          token: dataToken.idToken
        })
        //dispatch('setLogoutTimer', dataToken.expiresIn)
  },
    login ({commit}, authData) {
           console.log(authData)
        // var res = {
        //     data: {
        //         expiresIn:5000,
        //         idToken:'a',
        //         localId:'123'
        //     }
        // }
          // const now = new Date()
          // const expirationDate = new Date(now.getTime() + res.data.expiresIn * 1000)
          localStorage.setItem('token', authData.idToken)
          // localStorage.setItem('expirationDate', expirationDate)
          commit('authUser', {
            token: authData.idToken
          })
          //dispatch('setLogoutTimer', res.data.expiresIn)
    },
    tryAutoLogin ({commit}) {
      const token = localStorage.getItem('token')
      if (!token) {
        return false
      }
      // const expirationDate = localStorage.getItem('expirationDate')
      // const now = new Date()
      // if (now >= expirationDate) {
      //   return false
      // }
      // const userId = localStorage.getItem('userId')
      commit('authUser', {
        token: token
      })
      return true
    },
    logout ({commit}) {

        // axios.get('https://tn.smartcommunitylab.it/aac/logout?target=http://localhost:8080/campagne')
            // .then(res => {
                commit('clearAuthData')
                  // localStorage.removeItem('expirationDate')
                  localStorage.removeItem('token')
                //   console.log(res);
                //   routes.replace('/home');
            // })
            // .catch(error => console.log(error))

    },
   },
  getters: {
    user: state => {
      return state.user
    },
    isAuthenticated: state => {
      return state.idToken !== null
    },
    campagna: state => {
        return state.campagna
    }
  }
})