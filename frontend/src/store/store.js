import Vue from 'vue'
import Vuex from 'vuex'

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
    storeCampagna (state,campagna) {
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
    storeCampagna ({commit}, campagna) {
      commit('storeCampagna',campagna)
  },
  
    exitCampagna ({commit}) {
        commit('exitCampagna')
    },
    setLogoutTimer ({commit}, expirationTime) {
      setTimeout(() => {
        commit('clearAuthData')
      }, expirationTime * 1000)
    },
    
    storeUser({commit}, user) {
      commit('storeUser', user)
    },

    loginWithToken ({commit}, dataToken) {
      
      sessionStorage.setItem('token', dataToken.idToken)
        commit('authUser', {
          token: dataToken.idToken
        })
  },
    login ({commit}, authData) {
           console.log(authData)
 
           sessionStorage.setItem('token', authData.idToken)
          commit('authUser', {
            token: authData.idToken
          })
    },
    tryAutoLogin ({commit}) {
      const token = sessionStorage.getItem('token')
      if (!token) {
        return false
      }

      commit('authUser', {
        token: token
      })
      return true
    },
    logout ({commit}) {
      commit('clearAuthData')
      sessionStorage.removeItem('token')
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