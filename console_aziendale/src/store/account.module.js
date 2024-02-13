import { userService } from '../services';
import { oauthService } from '../services/oauth.services';
import { router } from '../routes';


function getHome(user) {
    if (user.canDo('list', 'companies')) {
        return {
            title: 'Gestione aziende',
            route: '/GestioneAziende'
        };
    }
    if (user.permissions.appuseronly) {
        return {
            title: 'aziende',
            route: '/GestioneAziende'
        };
    }
    return {
        title: 'Gestione dipendenti',
        route: '/GestioneDipendenti'
    };
}
function extendUserWithPermissions(user) {
    user.permissions = {};
    user.permissions.territories = [];
    user.permissions.campaigns = [];
    user.permissions.companies = [];
    user.permissions.admin = false;
    // permissions controller
    user.canDo = (op, resource, companyId, campaignId, territoryId) => {
        console.log('can do', op, resource, companyId, campaignId, territoryId);
        // admin can do everything
        if (user.permissions.admin) return true;
        // the territory manager can do everything as he does not see object out of her territories
        if (user.permissions.territories.length > 0) return true;
        //operations over company
        if (['users', 'employees', 'locations', 'stats', 'company'].indexOf(resource) >= 0 && user.permissions.companies.indexOf(companyId) >= 0) return true;
        // campaign manager may see the global stats
        if ('stats' == resource && !companyId && user.permissions.campaigns.length > 0) return true; 
        // everybody can see the campaigns
        if ('campaigns' == resource && op == 'view') return true;
        // campaign manager can view companies
        if ('companies' == resource && op == 'view') return user.permissions.campaigns.length > 0;
        return false;
    }
    user.appuseronly = !user.admin && user.permissions.territories.length == 0 && user.permissions.campaigns.length == 0 && user.permissions.companies.length == 0;
    user.roles.forEach(r => {
        if (r.role == 'ROLE_MOBILITY_MANAGER') {
            user.permissions.companies.push(r.companyId);
        }
        if (r.role == 'ROLE_CAMPAIGN_MANAGER') {
            user.permissions.campaigns.push(r.campaignId);
        }
        if (r.role == 'ROLE_TERRITORY_MANAGER') {
            user.permissions.territories.push(r.territoryId);
        }
        if (r.role == 'ROLE_ADMIN') {
            user.permissions.admin = true;
        }
    });
    return user;
}

const user = JSON.parse(localStorage.getItem('user'));
const state = user
    ? { status: { loggedIn: true }, user: extendUserWithPermissions(user), temporaryAdmin:false, home: getHome(user) }
    : { status: {}, user: null, temporaryAdmin:false,home: null};


function handleToken(commit, dispatch, token) {
    //todo reset old values
    commit('loginSuccess', token);
    userService.getAccount().then(user => {
        commit('userLogged', extendUserWithPermissions(user));
        var page = getHome(user);
        commit('homeUser', page);
        var userCompanies = userService.getCompanies(user);
        if (userCompanies.length > 0){
            dispatch('company/getCompanyById', userCompanies[0], { root: true });
            dispatch('company/initCompanyAdmin', userCompanies[0], { root: true }); 
            dispatch('campaign/getAll',userCompanies[0], { root: true });
            dispatch('employee/getAll', userCompanies[0], { root: true });
        }
        dispatch('navigation/changePage', page, { root: true });
        router.push(page.route);
    })
}

const actions = {

    login({ dispatch, commit }, { username, password }) {
        commit('loginRequest', { username });

        userService.login(username, password)
            .then(
                token => {
                    handleToken(commit, dispatch, token);
                },
                error => {
                    commit('loginFailure', error);
                    dispatch('alert/error', error, { root: true });
                }
            );
    },
    loginOAuth({ dispatch, commit }, {access_token}) {
        commit('loginRequest', { username: access_token });
        userService.loginOAuth(access_token)
        .then(
            token => {
                handleToken(commit, dispatch, token);
            },
            error => {
                commit('loginFailure', error);
                dispatch('alert/error', error, { root: true });
                userService.logout();
                oauthService.signout();
            }
        );
    }
    ,
    logout({ commit, dispatch }) {
        const oauthLogin = ('true' == localStorage.getItem('oauth_login'));
        if (oauthLogin) oauthService.signout();
        else {
            actions.logoutLocal({ commit, dispatch });
        }
    },
    logoutLocal({ commit, dispatch }) { 
        userService.logout();
        commit('logout');
        dispatch('alert/success', "Utente uscito con successo", { root: true });
        dispatch('company/logout', null, { root: true });
        dispatch('campaign/logout', null, { root: true });
        dispatch('employee/logout', null, { root: true });
        dispatch('location/logout', null, { root: true });
        dispatch('stat/logout', null, { root: true });
        router.push('/Login');
    },

    temporaryCompanyAdmin({ commit }) {
        commit('temporaryCompanyAdmin');
    },
    removedCompanyAdmin({ commit }) {
        commit('removedCompanyAdmin');
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
        userService.resetPasswordFinish(key,newPassword).then(function(){
            commit('resetPasswordFinishSuccess');
            dispatch('alert/success', "Password cambiata con successo", { root: true });

        } ,function(error){
            commit('cresetPasswordFinishFailure', error);
            dispatch('alert/error', error, { root: true });
        })
    },

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
    homeUser(state, home) {
        console.log('home')
        state.home = home;
    },
    loginFailure(state) {
        state.status = {};
        state.user = null;
        state.role = null;
        state.home = null;
        state.temporaryAdmin=false;
        state.token = null;
    },
    logout(state) {
        state.status = {};
        state.user = null;
        state.role = null;
        state.home = null;
        state.temporaryAdmin=false;
        state.token = null;
    },
    temporaryCompanyAdmin(state) {
        state.temporaryAdmin=true;
    },
    removedCompanyAdmin(state) {
        state.temporaryAdmin=false;
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