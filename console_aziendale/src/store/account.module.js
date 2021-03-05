import { userService } from '../services';
import { router } from '../routes';

const user = JSON.parse(localStorage.getItem('user'));
const state = user
    ? { status: { loggedIn: true }, user, role: userService.getRole(user), home: userService.getHome(userService.getRole(user)) }
    : { status: {}, user: null, role: null, home: null };


const actions = {
    login({ dispatch, commit }, { username, password }) {
        commit('loginRequest', { username });

        userService.login(username, password)
            .then(
                token => {
                    commit('loginSuccess', token);
                    userService.getAccount().then(user => {
                        commit('userLogged', user);
                        var role = userService.getRole(user);
                        commit('roleUser', role);
                        var page = userService.getHome(role);
                        commit('homeUser', page);
                        dispatch('navigation/changePage', page, { root: true });
                        var userCompanies = userService.getCompanies(user);
                        if (userCompanies.length > 0)
                            dispatch('company/getCompanyById', userCompanies[0], { root: true }); router.push(page.route);
                    })
                },
                error => {
                    commit('loginFailure', error);
                    dispatch('alert/error', error, { root: true });
                }
            );
    },
    logout({ commit, dispatch }) {
        userService.logout();
        commit('logout');
        dispatch('alert/success', "Utente uscito con successo", { root: true });
        dispatch('company/getCompanyById', null, { root: true });
        dispatch('company/resetCompanyAdmin', null, { root: true });
        router.push('/login');
    },
    setDefaultCompany({ dispatch }, user) {
        var userCompanies = userService.getCompanies(user);
        if (userCompanies.length > 0)
            dispatch('company/getCompanyById', userCompanies[0], { root: true });
    },
    changePassword({ commit, dispatch },{oldPassword,newPassword}){
        commit('changePassword');
        userService.changePassword(oldPassword,newPassword).then(function(){
            commit('changePasswordSuccess');
            dispatch('alert/success', "Password cambiata con successo", { root: true });

        } ,function(error){
            commit('changePasswordFailure', error);
            dispatch('alert/error', error, { root: true });
        })
    },
    resetPasswordInit({ commit, dispatch },username){
        commit('resetPasswordInit');
        userService.resetPasswordInit(username).then(function(){
            commit('resetPasswordInitSuccess');
            dispatch('alert/success', "Verificare la mail per confermare il cambio di password", { root: true });

        } ,function(error){
            commit('resetPasswordInitFailure', error);
            dispatch('alert/error', error, { root: true });
        })
    },
    resetPasswordFinish({ commit, dispatch },{key,newPassword}){
        commit('resetPasswordFinish');
        userService.changePassword(key,newPassword).then(function(){
            commit('resetPasswordFinishSuccess');
            dispatch('alert/success', "Password cambiata con successo", { root: true });

        } ,function(error){
            commit('cresetPasswordFinishFailure', error);
            dispatch('alert/error', error, { root: true });
        })
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
    homeUser(state, home) {
        console.log('home')
        state.home = home;
    },
    loginFailure(state) {
        state.status = {};
        state.user = null;
        state.role = null;
        state.home = null;
    },
    logout(state) {
        state.status = {};
        state.user = null;
        state.role = null;
        state.home = null;
    },
    changePassword() {
        
    },
    changePasswordSuccess() {
       
    },
    changePasswordFailure() {

    },
    resetPasswordInit() {
        
    },
    resetPasswordInitSuccess() {
       
    },
    resetPasswordInitFailure() {

    },
    resetPasswordFinish() {
        
    },
    resetPasswordFinishSuccess() {
       
    },
    resetPasswordFinishFailure() {

    }
};

export const account = {
    namespaced: true,
    state,
    actions,
    mutations
};