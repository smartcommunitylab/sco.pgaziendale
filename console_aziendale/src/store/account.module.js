import { userService } from '../services';
import {router} from '../routes';

const user = JSON.parse(localStorage.getItem('user'));
const state = user
    ? { status: { loggedIn: true }, user, role: userService.getRole(user)}
    : { status: {}, user: null, role:null };

const actions = {
    login({ dispatch, commit }, { username, password }) {
        commit('loginRequest', { username });
    
        userService.login(username, password)
            .then(
                token => {
                    commit('loginSuccess', token);
                    userService.getAccount().then (user=>{
                        commit('userLogged', user);
                        var role =userService.getRole(user);
                        commit('roleUser', role);
                        var page = userService.getHome(role);
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
    roleUser(state, role) {
        console.log('role')
        state.role = role;
    },
    loginFailure(state) {
        state.status = {};
        state.user = null;
        state.role = null;
    },
    logout(state) {
        state.status = {};
        state.user = null;
        state.role = null;
    }
};

export const account = {
    namespaced: true,
    state,
    actions,
    mutations
};