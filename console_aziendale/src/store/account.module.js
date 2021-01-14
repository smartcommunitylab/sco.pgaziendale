import { userService } from '../services';
import {router} from '../routes';

const user = JSON.parse(localStorage.getItem('user'));
const state = user
    ? { status: { loggedIn: true }, user}
    : { status: {}, user: null };

const actions = {
    login({ dispatch, commit }, { username, password }) {
        commit('loginRequest', { username });
    
        userService.login(username, password)
            .then(
                token => {
                    commit('loginSuccess', token);
                    userService.getAccount().then (user=>{
                        commit('userLogged', user);
                        var page = userService.getHome(user);
                        dispatch('navigation/changePage', page, {root:true}  );
                        router.push(page.route);
                    })
                },
                error => {
                    commit('loginFailure', error);
                    dispatch('alert/error', error, { root: true });
                }
            );
    },
    logout({ commit }) {
        userService.logout();
        commit('logout');
        router.push('/login');
    }
   
};

const mutations = {
    loginRequest(state, user) {
        state.status = { loggingIn: true };
        state.user = user;
    },
    loginSuccess(state, token) {
        console.log('logged and token')
        state.status = { loggedIn: true };
        state.token = token;
    },
    userLogged(state, user) {
        console.log('logged and user')
        state.status = { loggedIn: true };
        state.user = user;
    },
    loginFailure(state) {
        state.status = {};
        state.user = null;
    },
    logout(state) {
        state.status = {};
        state.user = null;
    },
    // eslint-disable-next-line no-unused-vars
    registerRequest(state, user) {
        state.status = { registering: true };
    },
    // eslint-disable-next-line no-unused-vars
    registerSuccess(state, user) {
        state.status = {};
    },
    // eslint-disable-next-line no-unused-vars
    registerFailure(state, error) {
        state.status = {};
    }
};

export const account = {
    namespaced: true,
    state,
    actions,
    mutations
};