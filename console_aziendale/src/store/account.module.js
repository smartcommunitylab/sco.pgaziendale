import { userService } from '../services';
import {router} from '../routes';

const user = JSON.parse(localStorage.getItem('user'));
const state = user
    ? { status: { loggedIn: true }, user, role: userService.getRole(user),home:userService.getHome(userService.getRole(user))}
    : { status: {}, user: null, role:null,home:null };

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
                        commit('homeUser', page);
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
    },
    setDefaultCompany({dispatch}) {
        var userCompanies=userService.getCompanies(user);
        if (userCompanies.length>0)
        dispatch('company/getCompanyById', userCompanies[0], {root:true}  );
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
    homeUser(state,home) {
        console.log('home')
        state.home = home;   
    },
    loginFailure(state) {
        state.status = {};
        state.user = null;
        state.role = null;
        state.home=null;
    },
    logout(state) {
        state.status = {};
        state.user = null;
        state.role = null;
        state.home=null;
    }
};

export const account = {
    namespaced: true,
    state,
    actions,
    mutations
};