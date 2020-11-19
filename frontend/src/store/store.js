import Vue from 'vue'
import Vuex from 'vuex'
import routes from '../routes'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    idToken: null,
    userId: null,
    user: null
  },
  mutations: {
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
    }
  },
  actions: {
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
      commit('clearAuthData')
      localStorage.removeItem('expirationDate')
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      routes.replace('/home')
    },
   },
  getters: {
    user: state => {
      return state.user
    },
    isAuthenticated: state => {
      return state.idToken !== null
    }
  }
})