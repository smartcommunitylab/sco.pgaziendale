import Vue from 'vue'
import Vuex from 'vuex'
// import routes from '../routes'
// import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    idToken: null,
    userId: null,
    user: null,
    campagna:null
  },
  mutations: {
      initApp (state) {
state.idToken=null;
state.userId=null;
state.user=null;
    state.campagna=null;
      },
    authUser (state, userData) {
      state.idToken = userData.token
      state.userId = userData.userId
    },
    storeUser (state, user) {
      state.user = user
    },
    clearAuthData (state) {
      state.idToken = null
      state.userId = null
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
            token: res.data.idToken,
            userId: res.data.localId
          })
          const now = new Date()
          const expirationDate = new Date(now.getTime() + res.data.expiresIn * 1000)
          localStorage.setItem('token', res.data.idToken)
          localStorage.setItem('userId', res.data.localId)
          localStorage.setItem('expirationDate', expirationDate)
          dispatch('storeUser', authData)
          dispatch('setLogoutTimer', res.data.expiresIn)

    },
    loginWithToken ({commit, dispatch}, dataToken) {
      
        const now = new Date()
        const expirationDate = new Date(now.getTime() + dataToken.expiresIn * 1000)
        localStorage.setItem('token', dataToken.idToken)
        localStorage.setItem('userId', 1)
        localStorage.setItem('expirationDate', expirationDate)
        commit('authUser', {
          token: dataToken.idToken,
          userId: 1
        })
        dispatch('setLogoutTimer', dataToken.expiresIn)
  },
    login ({commit, dispatch}, authData) {
          console.log(authData)
        var res = {
            data: {
                expiresIn:5000,
                idToken:'a',
                localId:'123'
            }
        }
          const now = new Date()
          const expirationDate = new Date(now.getTime() + res.data.expiresIn * 1000)
          localStorage.setItem('token', res.data.idToken)
          localStorage.setItem('userId', res.data.localId)
          localStorage.setItem('expirationDate', expirationDate)
          commit('authUser', {
            token: res.data.idToken,
            userId: res.data.localId
          })
          dispatch('setLogoutTimer', res.data.expiresIn)
    },
    tryAutoLogin ({commit}) {
      const token = localStorage.getItem('token')
      if (!token) {
        return
      }
      const expirationDate = localStorage.getItem('expirationDate')
      const now = new Date()
      if (now >= expirationDate) {
        return
      }
      const userId = localStorage.getItem('userId')
      commit('authUser', {
        token: token,
        userId: userId
      })
    },
    logout ({commit}) {

        // axios.get('https://tn.smartcommunitylab.it/aac/logout?target=http://localhost:8080/campagne')
            // .then(res => {
                commit('clearAuthData')
                  localStorage.removeItem('expirationDate')
                  localStorage.removeItem('token')
                  localStorage.removeItem('userId')
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